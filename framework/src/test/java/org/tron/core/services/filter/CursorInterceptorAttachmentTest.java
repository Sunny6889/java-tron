package org.tron.core.services.filter;

import io.grpc.ForwardingServerCallListener.SimpleForwardingServerCallListener;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.ServerInterceptors;
import io.grpc.protobuf.services.ProtoReflectionService;
import io.grpc.reflection.v1alpha.ServerReflectionGrpc;
import io.grpc.reflection.v1alpha.ServerReflectionRequest;
import io.grpc.reflection.v1alpha.ServerReflectionResponse;
import io.grpc.stub.StreamObserver;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.tron.api.DatabaseGrpc;
import org.tron.api.DatabaseGrpc.DatabaseImplBase;
import org.tron.api.GrpcAPI.EmptyMessage;
import org.tron.protos.Protocol.Block;

/**
 * Why the cursor interceptor is attached to the service definitions rather than to the server.
 *
 * <p>Both wirings put it innermost, so the call order a read sees is the same either way — the
 * first test pins that, so the switch is provably behaviour-preserving. What differs is reach: a
 * server-level interceptor brackets every service on the port, including the streaming reflection
 * service, while a service-level one reaches only the services it is bound to. The second test
 * pins that, which is the reason for the choice: switching the PBFT cursor reads chain state, and
 * a call that never touches the database should not pay for it.
 */
public class CursorInterceptorAttachmentTest {

  private static final List<String> TRACE = new CopyOnWriteArrayList<>();

  /** Records when its listener runs, i.e. where it sits in the chain. */
  private static class OrderProbe implements ServerInterceptor {

    private final String tag;

    OrderProbe(String tag) {
      this.tag = tag;
    }

    @Override
    public <Q, A> ServerCall.Listener<Q> interceptCall(
        ServerCall<Q, A> call, Metadata headers, ServerCallHandler<Q, A> next) {
      return new SimpleForwardingServerCallListener<Q>(next.startCall(call, headers)) {
        @Override
        public void onHalfClose() {
          TRACE.add(tag);
          super.onHalfClose();
        }
      };
    }
  }

  /** Records at interceptCall, so it also registers for streaming calls. */
  private static class AttachProbe implements ServerInterceptor {

    @Override
    public <Q, A> ServerCall.Listener<Q> interceptCall(
        ServerCall<Q, A> call, Metadata headers, ServerCallHandler<Q, A> next) {
      TRACE.add("attached:" + call.getMethodDescriptor().getFullMethodName());
      return next.startCall(call, headers);
    }
  }

  private static class ProbeDatabaseApi extends DatabaseImplBase {

    @Override
    public void getNowBlock(EmptyMessage request, StreamObserver<Block> observer) {
      TRACE.add("handler");
      observer.onNext(Block.getDefaultInstance());
      observer.onCompleted();
    }
  }

  @Test
  public void testServiceLevelAttachmentKeepsTheSameCallOrder() throws Exception {
    Assert.assertEquals("attaching the cursor per service must not reorder the chain",
        callOrder(false), callOrder(true));
    // and it is innermost, immediately before the handler
    List<String> order = callOrder(true);
    Assert.assertEquals("cursor must run last before the handler",
        "cursor", order.get(order.size() - 2));
    Assert.assertEquals("handler", order.get(order.size() - 1));
  }

  @Test
  public void testServiceLevelAttachmentLeavesReflectionAlone() throws Exception {
    Assert.assertTrue("a server-level interceptor brackets reflection too",
        reflectionIsBracketed(false));
    Assert.assertFalse("a service-level interceptor must not bracket reflection",
        reflectionIsBracketed(true));
  }

  /** Drives one unary call and returns the observed chain order. */
  private List<String> callOrder(boolean serviceLevel) throws Exception {
    TRACE.clear();
    int port = freePort();
    ServerBuilder<?> builder = ServerBuilder.forPort(port);
    OrderProbe cursor = new OrderProbe("cursor");
    if (serviceLevel) {
      builder.addService(ServerInterceptors.intercept(new ProbeDatabaseApi(), cursor));
    } else {
      builder.addService(new ProbeDatabaseApi()).intercept(cursor);
    }
    // the server-level chain, registered exactly as RpcService#addInterceptor does
    builder.intercept(new OrderProbe("rateLimiter"));
    builder.intercept(new OrderProbe("apiAccess"));
    builder.intercept(new OrderProbe("liteFnQuery"));
    builder.intercept(new OrderProbe("prometheus"));

    Server server = builder.build().start();
    ManagedChannel channel =
        ManagedChannelBuilder.forAddress("127.0.0.1", port).usePlaintext().build();
    try {
      DatabaseGrpc.newBlockingStub(channel).getNowBlock(EmptyMessage.getDefaultInstance());
    } finally {
      shutdown(channel, server);
    }
    return new ArrayList<>(TRACE);
  }

  /** Drives one reflection call and reports whether the cursor interceptor saw it. */
  private boolean reflectionIsBracketed(boolean serviceLevel) throws Exception {
    TRACE.clear();
    int port = freePort();
    ServerBuilder<?> builder = ServerBuilder.forPort(port);
    AttachProbe cursor = new AttachProbe();
    if (serviceLevel) {
      builder.addService(ServerInterceptors.intercept(new ProbeDatabaseApi(), cursor));
    } else {
      builder.addService(new ProbeDatabaseApi()).intercept(cursor);
    }
    builder.addService(ProtoReflectionService.newInstance());

    Server server = builder.build().start();
    ManagedChannel channel =
        ManagedChannelBuilder.forAddress("127.0.0.1", port).usePlaintext().build();
    CountDownLatch done = new CountDownLatch(1);
    try {
      StreamObserver<ServerReflectionRequest> request =
          ServerReflectionGrpc.newStub(channel).serverReflectionInfo(
              new StreamObserver<ServerReflectionResponse>() {
                @Override
                public void onNext(ServerReflectionResponse value) {
                }

                @Override
                public void onError(Throwable t) {
                  done.countDown();
                }

                @Override
                public void onCompleted() {
                  done.countDown();
                }
              });
      request.onNext(ServerReflectionRequest.newBuilder().setListServices("").build());
      request.onCompleted();
      done.await(5, TimeUnit.SECONDS);
    } finally {
      shutdown(channel, server);
    }
    for (String entry : TRACE) {
      if (entry.startsWith("attached:") && entry.contains("ServerReflection")) {
        return true;
      }
    }
    return false;
  }

  private static void shutdown(ManagedChannel channel, Server server) throws Exception {
    channel.shutdownNow();
    server.shutdownNow();
    server.awaitTermination(5, TimeUnit.SECONDS);
  }

  private static int freePort() throws Exception {
    try (ServerSocket socket = new ServerSocket(0)) {
      return socket.getLocalPort();
    }
  }
}
