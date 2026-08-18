package org.tron.core.services.interfaceOnSolidity;

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

  @Override
  protected void addService(NettyServerBuilder serverBuilder) {
    serverBuilder.addService(rpcApiService.getDatabaseApi());
    serverBuilder.addService(rpcApiService.getWalletSolidityApi());
  }

  @Override
  protected void addInterceptor(NettyServerBuilder serverBuilder) {
    // Registered first so it is innermost, wrapping the handler alone (in gRPC 1.83.0 the
    // first-registered interceptor is closest to the handler, pinned by GrpcInterceptorProbeTest).
    // It scopes the SOLIDITY cursor to the data read.
    serverBuilder.intercept(solidityCursorInterceptor);
    super.addInterceptor(serverBuilder);
  }

}
