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
 * Switches the current thread's read cursor for the duration of a gRPC call and restores it
 * afterwards, so every call served through this interceptor reads from the snapshot its subclass
 * selects. The services behind it never touch the cursor, so one instance can serve HEAD, SOLIDITY
 * and PBFT semantics on different servers.
 *
 * <p>Two invariants it relies on:
 * <ul>
 * <li>The bracket wraps {@code onHalfClose()}, not {@code interceptCall}: gRPC invokes the unary
 * handler inline from {@code onHalfClose}, while {@code interceptCall} may run on another thread of
 * the pool. The cursor is a {@link ThreadLocal}, so setting it elsewhere fails silently and the
 * port serves HEAD data.
 * <li>Database handlers must be synchronous: the cursor is reset when {@code onHalfClose} returns,
 * so a handler that defers its reads to another thread would read HEAD. All cursor-port methods are
 * synchronous unary handlers.
 * </ul>
 *
 * <p>The {@code finally} reset is mandatory: gRPC reuses a fixed thread pool, so a leftover cursor
 * would leak into the next call on the same thread.
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
          // For SOLIDITY/PBFT the offset is computed inside Manager#setCursor at call time.
          dbManager.setCursor(cursor);
          super.onHalfClose();
        } finally {
          dbManager.resetCursor();
        }
      }
    };
  }
}
