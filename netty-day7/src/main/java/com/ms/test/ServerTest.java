package com.ms.test;

import com.ms.bootstrap.ServerBootstrap;
import com.ms.channel.nio.NioEventLoopGroup;
import com.ms.util.concurrent.GenericFutureListener;
import com.ms.util.concurrent.Future;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;

public class ServerTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(2);
         serverBootstrap.group(bossGroup,workerGroup).
                serverSocketChannel(serverSocketChannel).
                bind("127.0.0.1",8080).
                 // 给返回的promise添加一个监听器
                addListener(new GenericFutureListener<Future<? super Object>>() {
                            @Override
                            public void operationComplete(Future<? super Object> future) throws Exception {
                                System.out.println("监听器随后也执行了！");
                            }
                }).sync();
                // sync 等待 promise 被设置成 success
                // 当 serverBootstrap 在bind方法执行完成，会设置 promise 的 result，从而触发
                // promise 里面的 监听器（作为nioEventLoop单线程执行器的一个任务而执行）
    }
}
