package com.ms.test;

import com.ms.channel.Channel;
import com.ms.channel.ChannelFuture;
import com.ms.channel.nio.NioEventLoopGroup;
import com.ms.channel.socket.nio.NioSocketChannel;
import com.ms.bootstrap.Bootstrap;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ClientTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(1);
        Bootstrap bootstrap = new Bootstrap();
        ChannelFuture channelFuture = bootstrap.group(workerGroup).
                channel(NioSocketChannel.class).
                handler(new TestHandlerOne()).
                connect("127.0.0.1",8080).sync();
        Channel channel = channelFuture.channel();
        channel.writeAndFlush(ByteBuffer.wrap("我是真正的netty！".getBytes()));
    }
}
