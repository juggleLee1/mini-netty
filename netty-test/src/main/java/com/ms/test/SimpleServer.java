package com.ms.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author: juggle
 * @Date: 2024/6/5 14:11
 * @Version:
 * @Description:
 **/
public class SimpleServer {
    public static void main(String[] args) throws IOException {
        Logger logger = LoggerFactory.getLogger(SimpleServer.class);

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        serverSocketChannel.configureBlocking(false);
        Selector selector = Selector.open();
        SelectionKey selectionKey = serverSocketChannel.register(selector, 0);
        selectionKey.interestOps(SelectionKey.OP_ACCEPT);

        serverSocketChannel.bind(new InetSocketAddress(8080));

        NioEventLoop nioEventLoop = new NioEventLoop();

        while (true) {
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                keyIterator.remove();
                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = channel.accept();
                    socketChannel.configureBlocking(false);
                    nioEventLoop.register(socketChannel, nioEventLoop);
                    logger.info(Thread.currentThread().getName() + "客户端连接成功！");
                    //连接成功之后，用客户端的channel写回一条消息
                    socketChannel.write(ByteBuffer.wrap("我发送成功了".getBytes()));
                    logger.info(Thread.currentThread().getName() + "向客户端发送数据成功！");
                }
            }
        }
    }
}
