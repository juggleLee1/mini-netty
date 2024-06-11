package ms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author: juggle
 * @Date: 2024/6/10 19:20
 * @Version:
 * @Description:
 **/
public class SimpleServer {
    public static final Logger LOGGER = LoggerFactory.getLogger(SimpleServer.class);

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        serverSocketChannel.bind(new InetSocketAddress(8080));
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup workGroup = new NioEventLoopGroup(2);
        bootstrap.group(workGroup);

        while (true) {
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                keyIterator.remove();
                if (key.isAcceptable()) {
                    ServerSocketChannel serverSocketChannel1 = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = serverSocketChannel1.accept();

                    bootstrap.register(socketChannel);
                    LOGGER.info("客户端连接成功！");
                    //连接成功之后，用客户端的channel写回一条消息
                    socketChannel.write(ByteBuffer.wrap("我发送成功了".getBytes()));
                    LOGGER.info("向客户端发送数据成功！");
                }
            }
        }
    }
}
