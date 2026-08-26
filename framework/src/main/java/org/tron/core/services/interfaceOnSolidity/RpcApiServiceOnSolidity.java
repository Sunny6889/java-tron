package org.tron.core.services.interfaceOnSolidity;

import io.grpc.ServerInterceptors;
import io.grpc.netty.NettyServerBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.tron.common.application.RpcService;
import org.tron.core.config.args.Args;
import org.tron.core.services.RpcApiService;
import org.tron.core.services.filter.SolidityCursorInterceptor;

@Slf4j(topic = "API")
public class RpcApiServiceOnSolidity extends RpcService {

  @Autowired
  private RpcApiService rpcApiService;

  @Autowired
  private SolidityCursorInterceptor solidityCursorInterceptor;

  public RpcApiServiceOnSolidity() {
    port = Args.getInstance().getRpcOnSolidityPort();
    enable = isFullNode() && Args.getInstance().isRpcSolidityEnable();
    executorName = "rpc-solidity-executor";
  }

  /**
   * Binds the SOLIDITY cursor to the two shared services rather than to the server. A service-level
   * interceptor lives inside the {@code ServerServiceDefinition}, so it always sits between the
   * server-level chain and the handler regardless of registration order, and it reaches only these
   * two services — the server-level chain (rate limiter, api access, lite-fullnode, prometheus) is
   * left exactly as the base class builds it, and reflection is not bracketed.
   */
  @Override
  protected void addService(NettyServerBuilder serverBuilder) {
    serverBuilder.addService(
        ServerInterceptors.intercept(rpcApiService.getDatabaseApi(), solidityCursorInterceptor));
    serverBuilder.addService(ServerInterceptors.intercept(
        rpcApiService.getWalletSolidityApi(), solidityCursorInterceptor));
  }

}
