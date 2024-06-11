package ms;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author: juggle
 * @Date: 2024/6/10 21:23
 * @Version:
 * @Description:
 **/
public class NioEventLoop extends SingleThreadEventLoop {

    private final SelectorProvider provider;

    private Selector selector;

    public NioEventLoop() {
        //java中的方法，通过provider不仅可以得到selector，还可以得到ServerSocketChannel和SocketChannel
        this.provider = SelectorProvider.provider();
        this.selector = openSelector();
    }

    private Selector openSelector() {
        try {
            return provider.openSelector();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void processSelectedKeys(Set<SelectionKey> selectionKeys) throws IOException {
        if (selectionKeys.isEmpty()) {
            return;
        }
        Iterator<SelectionKey> i = selectionKeys.iterator();
        for (;;) {
            final SelectionKey k = i.next();
            i.remove();
            //处理就绪事件
            processSelectedKey(k);
            if (!i.hasNext()) {
                break;
            }
        }
    }

    private void processSelectedKey(SelectionKey key) throws IOException {
        if (key.isReadable()) {
            SocketChannel channel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int read = channel.read(buffer);
            LOGGER.info("读到的字节数：" + read);
            if (read == -1) {
                channel.close();
            } else {
                //切换buffer的读模式
                buffer.flip();
                LOGGER.info(Charset.defaultCharset().decode(buffer).toString());
            }
        }
    }

    protected void select() throws IOException {
        Selector selector = this.selector;
        while (true) {
            int select = selector.select(3000);
            if (select != 0 || hasTasks()) {
                break;
            }
        }
    }

    public Selector getSelector() {
        return selector;
    }

    public void run() {
        while (true) {
            try {
                //没有事件就阻塞在这里
                select();
                //如果走到这里，就说明selector没有阻塞了
                processSelectedKeys(selector.selectedKeys());
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                //执行单线程执行器中的所有任务
                runAllTasks();
            }
        }
    }
}
