package com.ms.bootstrap;

import com.ms.channel.nio.NioEventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

/**
 * @Author: juggle
 * @Date: 2024/6/13 21:34
 * @Version:
 * @Description:
 **/
public class Bootstrap {
    private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    private NioEventLoop nioEventLoop;

    private SocketChannel socketChannel;

    public Bootstrap() {
    }

    public Bootstrap nioEventLoop(NioEventLoop nioEventLoop) {
        this.nioEventLoop = nioEventLoop;
        return this;
    }

    public Bootstrap socketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
        return this;
    }

    public void connect(String inetHost, int inetPort) {
        connect(new InetSocketAddress(inetHost, inetPort));
    }


    public void connect(SocketAddress localAddress) {
        doConnect(localAddress);
    }

    private void doConnect(SocketAddress localAddress) {
        //注册任务先提交
        nioEventLoop.register(socketChannel,this.nioEventLoop);
        //然后再提交连接服务器任务
        doConnect0(localAddress);
    }

    private void doConnect0(SocketAddress localAddress) {
        nioEventLoop.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    socketChannel.connect(localAddress);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        });
    }
}
