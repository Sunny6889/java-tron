package org.tron.core.services.filter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import io.grpc.ForwardingServerCallListener.SimpleForwardingServerCallListener;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.stub.StreamObserver;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.tron.api.DatabaseGrpc;
import org.tron.api.DatabaseGrpc.DatabaseImplBase;
import org.tron.api.GrpcAPI.EmptyMessage;
import org.tron.core.db.Manager;
import org.tron.core.db2.core.Chainbase;
import org.tron.protos.Protocol.Block;

/**
 * Pins down the two gRPC properties {@link CursorServerInterceptor} relies on.
 *
 * <p>First, which of two registered interceptors ends up innermost, i.e. closest to the handler.
 * This determines whether a cursor interceptor must be registered before or after the interceptors
 * added by the base service class.
 *
 * <p>Second, whether {@code Listener.onHalfClose()} runs on the same thread as the handler. The
 * cursor is a {@link ThreadLocal}, so a cursor set anywhere else has no effect on the read path and
 * fails silently, serving HEAD data from a cursor port.
 */
public class GrpcInterceptorProbeTest {

  /** Phase and thread of every observed step, in occurrence order. */
  private static final List<String> TRACE = new CopyOnWriteArrayList<>();

  private static String handlerThread;

  private Server server;
  private ManagedChannel channel;
  private ExecutorService executor;

  /** Records its position in the call flow without altering behaviour. */
  private static class ProbeInterceptor implements ServerInterceptor {

    private final String tag;

    ProbeInterceptor(String tag) {
      this.tag = tag;
    }

    @Override
    public <Q, A> ServerCall.Listener<Q> interceptCall(
        ServerCall<Q, A> call, Metadata headers, ServerCallHandler<Q, A> next) {
      record(tag, "interceptCall");
      return new SimpleForwardingServerCallListener<Q>(next.startCall(call, headers)) {
        @Override
        public void onHalfClose() {
          record(tag, "onHalfClose-IN");
          super.onHalfClose();
          record(tag, "onHalfClose-OUT");
        }
      };
    }
  }

  private static void record(String tag, String phase) {
    String line = String.format("%-14s %-16s thread=%s",
        "[" + tag + "]", phase, Thread.currentThread().getName());
    TRACE.add(line);
    System.out.println(line);
  }

  /** Minimal synchronous unary service, matching the shape of the cursor-port services. */
  private static class ProbeDatabaseApi extends DatabaseImplBase {
    @Override
    public void getNowBlock(EmptyMessage request, StreamObserver<Block> observer) {
      handlerThread = Thread.currentThread().getName();
      record("HANDLER", "execute");
      observer.onNext(Block.getDefaultInstance());
      observer.onCompleted();
    }
  }

  @Before
  public void setUp() throws Exception {
    TRACE.clear();
    handlerThread = null;
    int port = freePort();

    // A fixed thread pool mirrors the production server configuration.
    executor = Executors.newFixedThreadPool(2, r -> {
      Thread t = new Thread(r);
      t.setName("probe-rpc-executor-" + t.getId());
      return t;
    });

    server = ServerBuilder.forPort(port)
        .executor(executor)
        .addService(new ProbeDatabaseApi())
        .intercept(new ProbeInterceptor("A"))
        .intercept(new ProbeInterceptor("B"))
        .build()
        .start();

    channel = ManagedChannelBuilder.forAddress("127.0.0.1", port)
        .usePlaintext().directExecutor().build();
  }

  @After
  public void tearDown() throws Exception {
    if (channel != null) {
      channel.shutdownNow();
    }
    if (server != null) {
      server.shutdownNow();
    }
    if (executor != null) {
      executor.shutdown();
      if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
        executor.shutdownNow();
      }
    }
  }

  /**
   * Registration order versus nesting depth. The interceptor whose {@code onHalfClose} runs later
   * is the innermost one; the printed conclusion states where a cursor interceptor belongs.
   */
  @Test
  public void testInterceptorOrdering() {
    DatabaseGrpc.newBlockingStub(channel).getNowBlock(EmptyMessage.getDefaultInstance());

    int aIn = indexOf("[A]", "onHalfClose-IN");
    int bIn = indexOf("[B]", "onHalfClose-IN");
    Assert.assertTrue("no onHalfClose captured for A", aIn >= 0);
    Assert.assertTrue("no onHalfClose captured for B", bIn >= 0);

    System.out.println("\n===== ordering =====");
    System.out.println("registered: intercept(A) then intercept(B)");
    System.out.println(bIn > aIn
        ? "B (registered last) is innermost -> register the cursor interceptor LAST"
        : "A (registered first) is innermost -> register the cursor interceptor FIRST");
    System.out.println("====================\n");

    int handlerIdx = indexOf("[HANDLER]", "execute");
    Assert.assertTrue("handler did not run inside both interceptors",
        handlerIdx > aIn && handlerIdx > bIn);

    // The innermost interceptor enters onHalfClose last. RpcApiServiceOnSolidity /
    // RpcApiServiceOnPBFT register their cursor interceptor BEFORE super.addInterceptor(...)
    // precisely because the first-registered one ends up innermost, wrapping the handler alone.
    // Asserted, not merely printed: if a gRPC upgrade flips this, those two services silently
    // start bracketing the other interceptors instead of the handler.
    Assert.assertTrue(
        "first-registered interceptor must be innermost; the cursor services depend on it",
        aIn > bIn);
  }

  /**
   * Thread affinity between {@code onHalfClose} and the handler. They must coincide for a
   * ThreadLocal cursor set in the interceptor to be visible to the read path.
   */
  @Test
  public void testThreadAffinity() {
    DatabaseGrpc.newBlockingStub(channel).getNowBlock(EmptyMessage.getDefaultInstance());

    String halfCloseThread = threadOf("[B]", "onHalfClose-IN");
    Assert.assertNotNull("no thread captured for onHalfClose", halfCloseThread);
    Assert.assertNotNull("no thread captured for the handler", handlerThread);

    System.out.println("\n===== thread affinity =====");
    System.out.println("onHalfClose thread = " + halfCloseThread);
    System.out.println("handler     thread = " + handlerThread);
    System.out.println(halfCloseThread.equals(handlerThread)
        ? "same thread -> a cursor set in onHalfClose reaches the handler"
        : "different threads -> a ThreadLocal cursor cannot reach the handler");
    System.out.println("===========================\n");

    Assert.assertEquals(
        "onHalfClose and the handler must share a thread for the ThreadLocal cursor to apply",
        handlerThread, halfCloseThread);
  }

  /**
   * End-to-end check that a ThreadLocal written in {@code onHalfClose} is observable by the
   * handler, which is exactly how the cursor reaches the read path.
   */
  @Test
  public void testThreadLocalPropagation() throws Exception {
    final ThreadLocal<String> probe = new ThreadLocal<>();
    final String[] seenByHandler = new String[1];

    int port = freePort();
    Server s = ServerBuilder.forPort(port)
        .executor(executor)
        .addService(new DatabaseImplBase() {
          @Override
          public void getNowBlock(EmptyMessage req, StreamObserver<Block> obs) {
            seenByHandler[0] = probe.get();
            obs.onNext(Block.getDefaultInstance());
            obs.onCompleted();
          }
        })
        .intercept(new ServerInterceptor() {
          @Override
          public <Q, A> ServerCall.Listener<Q> interceptCall(
              ServerCall<Q, A> call, Metadata headers, ServerCallHandler<Q, A> next) {
            return new SimpleForwardingServerCallListener<Q>(next.startCall(call, headers)) {
              @Override
              public void onHalfClose() {
                try {
                  probe.set("SET_BY_INTERCEPTOR");
                  super.onHalfClose();
                } finally {
                  probe.remove();
                }
              }
            };
          }
        })
        .build()
        .start();

    ManagedChannel ch = ManagedChannelBuilder.forAddress("127.0.0.1", port)
        .usePlaintext().directExecutor().build();
    try {
      DatabaseGrpc.newBlockingStub(ch).getNowBlock(EmptyMessage.getDefaultInstance());
      System.out.println("\n===== ThreadLocal propagation =====");
      System.out.println("value seen by handler = " + seenByHandler[0]);
      System.out.println("===================================\n");

      Assert.assertEquals(
          "a ThreadLocal set in onHalfClose must be visible to the handler",
          "SET_BY_INTERCEPTOR", seenByHandler[0]);
    } finally {
      ch.shutdownNow();
      s.shutdownNow();
      s.awaitTermination(5, TimeUnit.SECONDS);
    }
  }

  /**
   * The production interceptor itself, not a stand-in: the cursor must be set on the thread that
   * runs the handler and must be restored before the call ends, even when the handler throws.
   */
  @Test
  public void testProductionInterceptorSetsCursorOnTheHandlerThread() throws Exception {
    final List<String> setOn = new CopyOnWriteArrayList<>();
    final List<String> resetOn = new CopyOnWriteArrayList<>();
    final String[] handlerOn = new String[1];

    Manager manager = mock(Manager.class);
    doAnswer(inv -> setOn.add(Thread.currentThread().getName()))
        .when(manager).setCursor(any(Chainbase.Cursor.class));
    doAnswer(inv -> resetOn.add(Thread.currentThread().getName()))
        .when(manager).resetCursor();

    SolidityCursorInterceptor interceptor = new SolidityCursorInterceptor();
    Field dbManager = CursorServerInterceptor.class.getDeclaredField("dbManager");
    dbManager.setAccessible(true);
    dbManager.set(interceptor, manager);

    int port = freePort();
    Server s = ServerBuilder.forPort(port)
        .executor(executor)
        .addService(new DatabaseImplBase() {
          @Override
          public void getNowBlock(EmptyMessage req, StreamObserver<Block> obs) {
            handlerOn[0] = Thread.currentThread().getName();
            obs.onNext(Block.getDefaultInstance());
            obs.onCompleted();
          }
        })
        .intercept(interceptor)
        .build()
        .start();

    ManagedChannel ch = ManagedChannelBuilder.forAddress("127.0.0.1", port)
        .usePlaintext().directExecutor().build();
    try {
      DatabaseGrpc.newBlockingStub(ch).getNowBlock(EmptyMessage.getDefaultInstance());

      Assert.assertEquals("cursor must be set exactly once per call", 1, setOn.size());
      Assert.assertEquals("cursor must be restored exactly once per call", 1, resetOn.size());
      Assert.assertNotNull("handler did not run", handlerOn[0]);
      // the ThreadLocal cursor only reaches the read path if it is set on the handler's thread
      Assert.assertEquals("cursor was set on a thread other than the handler's",
          handlerOn[0], setOn.get(0));
      Assert.assertEquals("cursor was restored on a thread other than the handler's",
          handlerOn[0], resetOn.get(0));
      verify(manager).setCursor(Chainbase.Cursor.SOLIDITY);
    } finally {
      ch.shutdownNow();
      s.shutdownNow();
      s.awaitTermination(5, TimeUnit.SECONDS);
    }
  }

  /** A handler that throws must still leave the cursor restored, or it leaks into the next call. */
  @Test
  public void testProductionInterceptorRestoresCursorWhenHandlerThrows() throws Exception {
    Manager manager = mock(Manager.class);
    SolidityCursorInterceptor interceptor = new SolidityCursorInterceptor();
    Field dbManager = CursorServerInterceptor.class.getDeclaredField("dbManager");
    dbManager.setAccessible(true);
    dbManager.set(interceptor, manager);

    int port = freePort();
    Server s = ServerBuilder.forPort(port)
        .executor(executor)
        .addService(new DatabaseImplBase() {
          @Override
          public void getNowBlock(EmptyMessage req, StreamObserver<Block> obs) {
            throw new IllegalStateException("boom");
          }
        })
        .intercept(interceptor)
        .build()
        .start();

    ManagedChannel ch = ManagedChannelBuilder.forAddress("127.0.0.1", port)
        .usePlaintext().directExecutor().build();
    try {
      try {
        DatabaseGrpc.newBlockingStub(ch).getNowBlock(EmptyMessage.getDefaultInstance());
        Assert.fail("expected the handler failure to surface");
      } catch (RuntimeException expected) {
        // the call fails; what matters is the cursor below
      }
      verify(manager).setCursor(Chainbase.Cursor.SOLIDITY);
      verify(manager).resetCursor();
    } finally {
      ch.shutdownNow();
      s.shutdownNow();
      s.awaitTermination(5, TimeUnit.SECONDS);
    }
  }

  private static int freePort() throws Exception {
    try (ServerSocket socket = new ServerSocket(0)) {
      return socket.getLocalPort();
    }
  }

  private int indexOf(String tag, String phase) {
    for (int i = 0; i < TRACE.size(); i++) {
      String line = TRACE.get(i);
      if (line.contains(tag) && line.contains(phase)) {
        return i;
      }
    }
    return -1;
  }

  private String threadOf(String tag, String phase) {
    int i = indexOf(tag, phase);
    if (i < 0) {
      return null;
    }
    String line = TRACE.get(i);
    return line.substring(line.indexOf("thread=") + "thread=".length()).trim();
  }
}
