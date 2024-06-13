package com.ms.test;
import com.ms.bootstrap.ServerBootstrap;
import com.ms.channel.nio.NioEventLoop;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;

public class ServerTest {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        NioEventLoop boss = new NioEventLoop(serverSocketChannel, null);
        NioEventLoop worker = new NioEventLoop(serverSocketChannel, null);
        //把worker执行器设置到boss执行器中，这样在boss执行器中接收到客户端连接，可以立刻提交给worker执行器
        boss.setWorker(worker);
        serverBootstrap.nioEventLoop(boss).
                serverSocketChannel(serverSocketChannel);
        // bind --> register 启动eventloop单线程  task1：注册accept事件 --> task2：绑定端口 --> 读事件（交给work执行）
        serverBootstrap.bind("127.0.0.1",8080);
    }
}
