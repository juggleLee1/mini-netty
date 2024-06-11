package com.ms.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author: juggle
 * @Date: 2024/6/5 13:56
 * @Version:
 * @Description:
 **/
public class SimpleClient {
    public static void main(String[] args) throws IOException {
        Logger logger = LoggerFactory.getLogger(SimpleClient.class);

        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        Selector selector = Selector.open();
        SelectionKey selectionKey = socketChannel.register(selector, 0);
        selectionKey.interestOps(SelectionKey.OP_CONNECT);

        socketChannel.connect(new InetSocketAddress(8080));

        while (true) {
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey next = iterator.next();
                iterator.remove();
                if (next.isConnectable()) {
                    if (socketChannel.finishConnect()) {
                        socketChannel.register(selector, SelectionKey.OP_READ);
                        logger.info("客户端连接上了");
                        socketChannel.write(ByteBuffer.wrap("客户端发送信息...".getBytes()));
                    }
                } else if (next.isReadable()) {
                    SocketChannel channel = (SocketChannel)next.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    int read = channel.read(byteBuffer);
                    byte[] res = new byte[read];
                    byteBuffer.flip();
                    byteBuffer.get(res);
                    logger.info("收到服务端的数据：" + new String(res));
                }
            }
        }
    }
}
