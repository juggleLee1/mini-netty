package com.ms.test;

import com.ms.bootstrap.ServerBootstrap;
import com.ms.channel.Channel;
import com.ms.channel.ChannelFuture;
import com.ms.channel.nio.AbstractNioChannel;
import com.ms.channel.nio.NioEventLoop;
import com.ms.channel.nio.NioEventLoopGroup;
import com.ms.channel.socket.NioServerSocketChannel;
import com.ms.util.concurrent.DefaultPromise;
import com.ms.util.concurrent.Future;
import com.ms.util.concurrent.GenericFutureListener;
import com.ms.util.concurrent.Promise;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;

public class ServerTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(2);
        ChannelFuture channelFuture = serverBootstrap.group(bossGroup,workerGroup).
                channel(NioServerSocketChannel.class).
                bind(8080).addListener(future -> System.out.println("我绑定成功了")).sync();
        Channel channel = channelFuture.channel();
        //        NioServerSocketChannel channel = new NioServerSocketChannel();
       // AbstractNioChannel.NioUnsafe unsafe = (AbstractNioChannel.NioUnsafe) channel.unsafe();
//        channel.writeAndFlush()
//        socketChannel.write(ByteBuffer.wrap("我还不是netty，但我知道你上线了".getBytes()));
    }
}
