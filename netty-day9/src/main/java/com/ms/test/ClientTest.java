package com.ms.test;

import com.ms.bootstrap.Bootstrap;
import com.ms.channel.nio.NioEventLoop;
import com.ms.channel.nio.NioEventLoopGroup;
import com.ms.channel.socket.NioSocketChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class ClientTest {
    public static void main(String[] args) throws IOException {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(1);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup).
                channel(NioSocketChannel.class);
        bootstrap.connect("127.0.0.1",8080);
    }

}
