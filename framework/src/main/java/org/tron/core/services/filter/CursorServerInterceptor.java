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
 * {@code interceptCall}.</b> {@code interceptCall} runs when the call arrives and may execute on an
 * IO thread, while the handler that reads the database runs in {@code onHalfClose()} on an executor
 * thread. The cursor is a {@link ThreadLocal}: setting it on the IO thread has no effect on the
 * executor thread, and nothing throws — the port silently serves HEAD data instead. {@code
 * GrpcInterceptorProbeTest} asserts this thread affinity; re-run it before changing this class.
 *
 * <p><b>All handlers must be synchronous unary.</b> The cursor is reset as soon as
 * {@code onHalfClose} returns, which requires handlers to complete {@code onNext/onCompleted}
 * inline. A handler that defers its database reads to another thread or an asynchronous callback
 * would read after the reset and observe HEAD data.
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
