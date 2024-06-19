package com.ms.test;

import com.ms.bootstrap.Bootstrap;
import com.ms.channel.ChannelFuture;
import com.ms.channel.nio.NioEventLoopGroup;
import com.ms.channel.socket.nio.NioSocketChannel;

import java.io.IOException;

public class ClientTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(1);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup).
                channel(NioSocketChannel.class);
        ChannelFuture channelFuture = bootstrap.connect("127.0.0.1",8080);
    }

}
