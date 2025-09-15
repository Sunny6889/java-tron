package org.tron.core.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.Test;
import org.tron.api.GrpcAPI.EmptyMessage;
import org.tron.api.GrpcAPI.NumberMessage;
import org.tron.api.WalletGrpc;
import org.tron.common.utils.PublicMethod;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 测试gRPC服务未正确关闭导致JVM无法退出的情况
 */
public class GrpcServerShutdownTest {

  private Server server;
  
  /**
   * 测试gRPC服务正确关闭的情况
   */
  @Test
  public void testGrpcServerProperShutdown() throws IOException, InterruptedException {
    // 启动gRPC服务器
    startGrpcServer();
    
    System.out.println("gRPC服务器已启动，等待3秒...");
    Thread.sleep(3000);
    
    // 正确关闭gRPC服务器
    System.out.println("正确关闭gRPC服务器...");
    if (server != null) {
      server.shutdown().awaitTermination(5, TimeUnit.SECONDS);
      System.out.println("gRPC服务器已正确关闭");
    }
    
    // 这里JVM应该能正常退出
    System.out.println("测试完成，JVM应该能正常退出");
  }
  
  /**
   * 测试gRPC服务未关闭导致JVM无法退出的情况
   * 注意：此测试需要手动运行，因为它会导致JVM挂起
   * 运行方法：
   * java -cp <classpath> org.junit.runner.JUnitCore org.tron.core.grpc.GrpcServerShutdownTest#testGrpcServerNoShutdown
   */
  @Test
  public void testGrpcServerNoShutdown() throws IOException, InterruptedException {
    for(int i = 0; i < 100; i ++) {
      // 启动gRPC服务器
      startGrpcServer();
      
      System.out.println("gRPC服务器已启动，但不会关闭它");
      System.out.println("此测试将导致JVM无法正常退出，除非使用Ctrl+C强制终止");
      
      // 等待一段时间，观察JVM是否会退出
      CountDownLatch latch = new CountDownLatch(1);
      latch.await(10, TimeUnit.SECONDS);
      
      // 注意：这里没有关闭gRPC服务器，JVM将无法正常退出
      System.out.println("测试结束，但JVM应该仍在运行，因为gRPC服务器未关闭");
      
      // 如果要让测试能够结束，取消下面的注释
      // if (server != null) {
      //   server.shutdown().awaitTermination(5, TimeUnit.SECONDS);
      // }
    }
  }
  
  /**
   * 启动一个简单的gRPC服务器
   */
  private void startGrpcServer() throws IOException {
    server = ServerBuilder.forPort(PublicMethod.chooseRandomPort())
        .addService(new WalletImplBase())
        .build()
        .start();
    
    System.out.println("gRPC服务器启动在端口: 50099");
    
    // // 添加JVM关闭钩子
    // Runtime.getRuntime().addShutdownHook(new Thread(() -> {
    //   System.out.println("JVM关闭钩子被触发");
    //   if (server != null && !server.isShutdown()) {
    //     System.out.println("在关闭钩子中关闭gRPC服务器");
    //     server.shutdown();
    //   }
    // }));
  }
  
  /**
   * 简单的gRPC服务实现
   */
  private static class WalletImplBase extends WalletGrpc.WalletImplBase {
    @Override
    public void getBlockByNum(NumberMessage request, StreamObserver<org.tron.protos.Protocol.Block> responseObserver) {
      // 返回空块
      responseObserver.onNext(org.tron.protos.Protocol.Block.getDefaultInstance());
      responseObserver.onCompleted();
    }
    
    @Override
    public void listNodes(EmptyMessage request, StreamObserver<org.tron.api.GrpcAPI.NodeList> responseObserver) {
      // 返回空节点列表
      responseObserver.onNext(org.tron.api.GrpcAPI.NodeList.getDefaultInstance());
      responseObserver.onCompleted();
    }
  }
}