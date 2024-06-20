package com.ms.test;

import com.ms.bootstrap.ServerBootstrap;
import com.ms.channel.ChannelFuture;
import com.ms.channel.ChannelOption;
import com.ms.channel.nio.NioEventLoopGroup;
import com.ms.channel.socket.nio.NioServerSocketChannel;
import com.ms.util.AttributeKey;

import java.io.IOException;

public class ServerTest {

   public static AttributeKey<Integer> INDEX_KEY = AttributeKey.valueOf("常量");
    public static void main(String[] args) throws IOException, InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(2);
        ChannelFuture channelFuture = bootstrap.group(bossGroup,workerGroup).
                channel(NioServerSocketChannel.class).
                option(ChannelOption.SO_BACKLOG,2).
                handler(new TestHandlerOne()).
                attr(INDEX_KEY,10).
                childAttr(INDEX_KEY,10).
                bind(8080).
                addListener(future -> System.out.println("我绑定成功了")).sync();
    }
}
