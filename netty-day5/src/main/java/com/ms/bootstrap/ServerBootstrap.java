package com.ms.bootstrap;

import com.ms.channel.nio.NioEventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;

/**
 * @Author: juggle
 * @Date: 2024/6/13 21:36
 * @Version:
 * @Description:
 **/
public class ServerBootstrap {
    private static final Logger logger = LoggerFactory.getLogger(ServerBootstrap.class);

    private NioEventLoop nioEventLoop;

    private ServerSocketChannel serverSocketChannel;

    public ServerBootstrap serverSocketChannel(ServerSocketChannel serverSocketChannel) {
        this.serverSocketChannel = serverSocketChannel;
        return this;
    }

    public ServerBootstrap nioEventLoop(NioEventLoop nioEventLoop) {
        this.nioEventLoop = nioEventLoop;
        return this;
    }

    public void bind(String host,int inetPort) {
        bind(new InetSocketAddress(host,inetPort));
    }

    public void bind(SocketAddress localAddress) {
        doBind(localAddress);
    }

    private void doBind(SocketAddress localAddress) {
        nioEventLoop.register(serverSocketChannel,this.nioEventLoop);
        doBind0(localAddress);
    }

    /**
     * 这里把绑定端口号封装成一个runnable，提交到单线程执行器的任务队列，
     * @param localAddress
     */
    private void doBind0(SocketAddress localAddress) {
        nioEventLoop.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocketChannel.bind(localAddress);
                    logger.info("服务端channel和端口号绑定了");
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        });
    }
}
