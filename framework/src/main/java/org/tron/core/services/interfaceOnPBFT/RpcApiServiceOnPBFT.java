package org.tron.core.services.interfaceOnPBFT;

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

  @Override
  protected void addService(NettyServerBuilder serverBuilder) {
    serverBuilder.addService(rpcApiService.getDatabaseApi());
    serverBuilder.addService(rpcApiService.getWalletSolidityApi());
  }

  @Override
  protected void addInterceptor(NettyServerBuilder serverBuilder) {
    // Registered first so it is innermost, wrapping the handler alone (in gRPC 1.83.0 the
    // first-registered interceptor is closest to the handler, pinned by GrpcInterceptorProbeTest).
    // It scopes the PBFT cursor to the data read.
    serverBuilder.intercept(pbftCursorInterceptor);
    super.addInterceptor(serverBuilder);
  }

}
