package com.ms.test;

import com.ms.channel.ChannelFuture;
import com.ms.channel.ChannelInitializer;
import com.ms.channel.ChannelPipeline;
import com.ms.channel.nio.NioEventLoopGroup;
import com.ms.channel.socket.nio.NioServerSocketChannel;
import com.ms.channel.socket.nio.NioSocketChannel;
import com.ms.handler.timeout.IdleStateHandler;
import com.ms.bootstrap.ServerBootstrap;

import java.io.IOException;

public class ServerTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(2);
        ChannelFuture channelFuture = serverBootstrap.group(bossGroup,workerGroup).
                channel(NioServerSocketChannel.class).
                childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new IdleStateHandler(1,1,0));
                        p.addLast(new TestHandlerOne());
                    }
                }).
                bind(8080).
                addListener(future -> System.out.println("我绑定端口号成功了")).sync();

    }
}
