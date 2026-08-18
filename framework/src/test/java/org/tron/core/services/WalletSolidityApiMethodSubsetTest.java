package org.tron.core.services;

import io.grpc.stub.StreamObserver;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;

/**
 * Guards the precondition for the future WalletApi / WalletSolidityApi dedup (clean-rpc.md §10).
 *
 * <p>{@code WalletSolidityApi} (serving {@code protocol.WalletSolidity}) and {@code WalletApi}
 * (serving {@code protocol.Wallet}) implement the same read methods twice as byte-identical glue
 * over the shared {@code Wallet} object. Deduping means replacing the WalletSolidity bodies with a
 * one-line delegation, which is only sound while every WalletSolidity gRPC method also exists on
 * WalletApi. This test pins that subset relationship so the invariant cannot silently break.
 */
public class WalletSolidityApiMethodSubsetTest {

  /** Signatures of the gRPC unary handlers a service impl overrides: {@code name(requestType)}. */
  private static Set<String> grpcHandlerSignatures(Class<?> impl) {
    return Arrays.stream(impl.getDeclaredMethods())
        .filter(m -> m.getReturnType() == void.class)
        .filter(m -> m.getParameterCount() == 2)
        .filter(m -> m.getParameterTypes()[1] == StreamObserver.class)
        .map(m -> m.getName() + "(" + m.getParameterTypes()[0].getName() + ")")
        .collect(Collectors.toCollection(TreeSet::new));
  }

  @Test
  public void testWalletSolidityApiMethodsAllExistOnWalletApi() throws ClassNotFoundException {
    Class<?> walletApi = Class.forName("org.tron.core.services.RpcApiService$WalletApi");
    Class<?> walletSolidityApi =
        Class.forName("org.tron.core.services.RpcApiService$WalletSolidityApi");

    Set<String> onWalletApi = grpcHandlerSignatures(walletApi);
    Set<String> solidityOnly = new TreeSet<>(grpcHandlerSignatures(walletSolidityApi));
    solidityOnly.removeAll(onWalletApi);

    Assert.assertTrue(
        "these WalletSolidityApi methods have no WalletApi counterpart and cannot be dedup'd by "
            + "delegating to a shared Wallet handler (see clean-rpc.md §10): " + solidityOnly,
        solidityOnly.isEmpty());
  }
}
