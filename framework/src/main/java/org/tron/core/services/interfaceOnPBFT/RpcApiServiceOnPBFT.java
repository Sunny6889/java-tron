package org.tron.core.services.interfaceOnPBFT;

import io.grpc.ServerInterceptors;
import io.grpc.netty.NettyServerBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.tron.common.application.RpcService;
import org.tron.core.config.args.Args;
import org.tron.core.services.RpcApiService;
import org.tron.core.services.filter.PbftCursorInterceptor;

@Slf4j(topic = "API")
public class RpcApiServiceOnPBFT extends RpcService {

  @Autowired
  private RpcApiService rpcApiService;

  @Autowired
  private PbftCursorInterceptor pbftCursorInterceptor;

  public RpcApiServiceOnPBFT() {
    port = Args.getInstance().getRpcOnPBFTPort();
    enable = isFullNode() && Args.getInstance().isRpcPBFTEnable();
    executorName = "rpc-pbft-executor";
  }

  /**
   * Binds the PBFT cursor to the two shared services rather than to the server. A service-level
   * interceptor lives inside the {@code ServerServiceDefinition}, so it always sits between the
   * server-level chain and the handler regardless of registration order, and it reaches only these
   * two services — the server-level chain (rate limiter, api access, lite-fullnode, prometheus) is
   * left exactly as the base class builds it, and reflection is not bracketed. That last point
   * matters here: switching to the PBFT cursor reads the head and latest-pbft block numbers, so it
   * should not run for calls that never touch chain state.
   */
  @Override
  protected void addService(NettyServerBuilder serverBuilder) {
    serverBuilder.addService(
        ServerInterceptors.intercept(rpcApiService.getDatabaseApi(), pbftCursorInterceptor));
    serverBuilder.addService(ServerInterceptors.intercept(
        rpcApiService.getWalletSolidityApi(), pbftCursorInterceptor));
  }

}
