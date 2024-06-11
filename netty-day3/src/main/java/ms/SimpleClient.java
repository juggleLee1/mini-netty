package ms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @Author: juggle
 * @Date: 2024/6/10 19:30
 * @Version:
 * @Description:
 **/
public class SimpleClient {
    public static final Logger LOGGER = LoggerFactory.getLogger(SimpleClient.class);
    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        Selector selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        socketChannel.connect(new InetSocketAddress(8080));

        while (true) {
            selector.select();
            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                keyIterator.remove();
                if (key.isConnectable()) {
                    if (socketChannel.finishConnect()) {
                        socketChannel.register(selector,SelectionKey.OP_READ);
                        LOGGER.info("已经注册了读事件！");
                        //紧接着向服务端发送一条消息
                        socketChannel.write(ByteBuffer.wrap("客户端发送成功了".getBytes()));
                    }
                } else if (key.isReadable()) {
                    SocketChannel channel = (SocketChannel)key.channel();
                    //分配字节缓冲区来接受服务端传过来的数据
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    //向buffer写入客户端传来的数据
                    int len = channel.read(buffer);
                    byte[] readByte = new byte[len];
                    buffer.flip();
                    buffer.get(readByte);
                    LOGGER.info("读到来自服务端的数据：" + new String(readByte));
                }
            }
        }
    }
}
