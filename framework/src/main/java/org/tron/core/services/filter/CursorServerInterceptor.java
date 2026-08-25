package org.tron.core.services.filter;

import io.grpc.ForwardingServerCallListener.SimpleForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.tron.core.db.Manager;
import org.tron.core.db2.core.Chainbase;

/**
 * Switches the read cursor of the current thread for the duration of a gRPC call, and restores it
 * afterwards. Every call served by a server carrying this interceptor therefore reads from the
 * snapshot the subclass selects.
 *
 * <p>The service implementations behind it make no assumption about the cursor and never touch it;
 * which snapshot a read resolves to is decided solely by the calling thread's cursor. A single
 * service instance can thus serve HEAD, SOLIDITY and PBFT semantics on different servers.
 *
 * <h3>Two invariants this class depends on</h3>
 *
 * <p><b>The bracket must wrap {@code Listener.onHalfClose()}, not the body of
 * {@code interceptCall}.</b> gRPC delivers a call's listener callbacks as separate tasks through a
 * per-call {@code SerializingExecutor} over the server's application executor. They are serialized
 * with respect to each other, but they are <em>not</em> pinned to one thread, so
 * {@code interceptCall} and {@code onHalfClose} routinely run on different threads of the same
 * pool — {@code GrpcInterceptorProbeTest} shows exactly that. The cursor is a {@link ThreadLocal},
 * so a cursor set in {@code interceptCall} reaches the read path only by luck, and always reaches
 * it when the pool holds a single thread, which is why the mistake does not reproduce on a small
 * machine. The handler, by contrast, is invoked inline by gRPC's unary listener from
 * {@code onHalfClose()}, so it always shares that thread. Setting the cursor anywhere else fails
 * silently: nothing throws and the port serves HEAD data.
 *
 * <p><b>Every handler that reads the database must be synchronous.</b> The cursor is reset as soon
 * as {@code onHalfClose} returns, which requires such handlers to complete {@code onNext} /
 * {@code onCompleted} inline. A handler that defers its database reads to another thread or an
 * asynchronous callback would read after the reset and observe HEAD data. Every method of the
 * services mounted on the cursor ports is a synchronous unary handler. The one streaming service
 * these servers also carry, {@code ProtoReflectionService}, is bracketed too but reads no chain
 * state, so the reset racing its responses is harmless.
 *
 * <p>The {@code finally} block is mandatory: gRPC serves calls from a fixed thread pool, so a
 * cursor left behind would leak into the next call handled by the same thread.
 */
public abstract class CursorServerInterceptor implements ServerInterceptor {

  @Autowired
  protected Manager dbManager;

  /** Snapshot every call on this server reads from; set by the subclass. */
  protected Chainbase.Cursor cursor;

  @Override
  public <Q, A> ServerCall.Listener<Q> interceptCall(
      ServerCall<Q, A> call, Metadata headers, ServerCallHandler<Q, A> next) {
    return new SimpleForwardingServerCallListener<Q>(next.startCall(call, headers)) {
      @Override
      public void onHalfClose() {
        try {
          // For PBFT the offset is computed inside Manager#setCursor at call time.
          dbManager.setCursor(cursor);
          super.onHalfClose();
        } finally {
          dbManager.resetCursor();
        }
      }
    };
  }
}
