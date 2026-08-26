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
 * Switches the current thread's read cursor for the duration of the synchronous handler callback
 * and restores it afterwards, so a handler served through this interceptor reads from the snapshot
 * its subclass selects. The services behind it never touch the cursor, so one instance can serve
 * HEAD, SOLIDITY and PBFT semantics on different servers.
 *
 * <p>The scope is deliberately the handler callback, not the call: a call spans several listener
 * callbacks and may span several threads, so call-lifetime scoping is not a safe model for a
 * {@link ThreadLocal}.
 *
 * <p>Two invariants it relies on:
 * <ul>
 * <li>The bracket wraps {@code onHalfClose()}, not {@code interceptCall}: gRPC invokes the unary
 * handler inline from {@code onHalfClose}, while {@code interceptCall} runs as a separate task and
 * may land on another thread of the same pool — a {@code SerializingExecutor} orders the callbacks
 * but does not pin them to one thread. The cursor is a {@link ThreadLocal}, so setting it elsewhere
 * fails silently and the port serves HEAD data.
 * <li>Database handlers must be synchronous: the cursor is reset when {@code onHalfClose} returns,
 * so a handler that defers its reads to another executor, thread or future would read HEAD. Every
 * method behind the cursor ports is a synchronous unary handler today, but that is an
 * implementation invariant rather than a type-level guarantee — a future handler that moves a
 * database read off this thread needs explicit cursor propagation.
 * </ul>
 *
 * <p>The {@code finally} reset is mandatory: gRPC reuses a fixed thread pool, so a leftover cursor
 * would leak into the next call on the same thread.
 *
 * <p>Attach this at the service level ({@code ServerInterceptors.intercept}) rather than the
 * server level, so it stays inside the server-wide chain by construction instead of by registration
 * order, and only brackets the services that actually read chain state.
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
          // PBFT additionally needs a head-to-pbft offset, computed inside Manager#setCursor
          // per call.
          dbManager.setCursor(cursor);
          super.onHalfClose();
        } finally {
          dbManager.resetCursor();
        }
      }
    };
  }
}
