package com.ms.test;

import com.ms.bootstrap.ServerBootstrap;
import com.ms.channel.ChannelFuture;
import com.ms.channel.ChannelOption;
import com.ms.channel.nio.NioEventLoopGroup;
import com.ms.channel.socket.nio.NioServerSocketChannel;

import java.io.IOException;

import static com.ms.channel.ChannelOption.valueOf;

public class ServerTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(2);
        ChannelFuture channelFuture = serverBootstrap.group(bossGroup,workerGroup).
                channel(NioServerSocketChannel.class).
                bind(8080).addListener(future -> System.out.println("我绑定成功了")).sync();


        ChannelOption<Integer> CHANGLIANG = ChannelOption.valueOf("CHANGLIANG");
    }
}
