package com.ms.test;

import com.ms.bootstrap.Bootstrap;
import com.ms.channel.nio.NioEventLoopGroup;
import com.ms.channel.socket.nio.NioSocketChannel;

public class Test {
    public static void main(String[] args) {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(1);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup).
                channel(NioSocketChannel.class);
        NioEventLoopGroup workerGroup1 = new NioEventLoopGroup(1);
        Bootstrap bootstrap1 = new Bootstrap();
        bootstrap1.group(workerGroup1).
                channel(NioSocketChannel.class);
        NioEventLoopGroup workerGroup2 = new NioEventLoopGroup(1);
        Bootstrap bootstrap2 = new Bootstrap();
        bootstrap2.group(workerGroup2).
                channel(NioSocketChannel.class);

        new Thread(new Runnable() {
            @Override
            public void run() {
                bootstrap.connect("127.0.0.1",8080);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                bootstrap1.connect("127.0.0.1",8080);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                bootstrap2.connect("127.0.0.1",8080);
            }
        }).start();
    }
}
