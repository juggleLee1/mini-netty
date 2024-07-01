package com.ms.test;

import com.ms.channel.ChannelFuture;
import com.ms.channel.ChannelOption;
import com.ms.channel.nio.NioEventLoopGroup;
import com.ms.channel.socket.nio.NioServerSocketChannel;
import com.ms.bootstrap.ServerBootstrap;
import com.ms.util.AttributeKey;

import java.net.UnknownHostException;

public class ServerTest {

    public static void main(String[] args) throws UnknownHostException, InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        ChannelFuture channelFuture = bootstrap.group(bossGroup, workGroup).
                channel(NioServerSocketChannel.class).
                handler(new TestHandlerTwo()).
                option(ChannelOption.SO_BACKLOG,128).
                childAttr(AttributeKey.valueOf("常量"),10).
                childHandler(new TestHandlerOne()).
                bind(8080).
                addListener(future -> System.out.println("我绑定成功了")).sync();
    }
}
