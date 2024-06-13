package com.ms.test;

import com.ms.bootstrap.Bootstrap;
import com.ms.channel.nio.NioEventLoop;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class ClientTest {

    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.nioEventLoop(new NioEventLoop(null,socketChannel)).
                socketChannel(socketChannel);
        // connect --> register 启动eventloop单线程  task1：注册connect事件 --> task2：连接服务器 --> 读事件
        bootstrap.connect("127.0.0.1",8080);
    }

}
