package com.ms.channel.nio;

import com.ms.channel.EventLoopTaskQueueFactory;
import com.ms.channel.SingleThreadEventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * @Author: juggle
 * @Date: 2024/6/13 21:27
 * @Version:
 * @Description:
 **/
public class NioEventLoop extends SingleThreadEventLoop {
    private static final Logger logger = LoggerFactory.getLogger(NioEventLoop.class);

    private final ServerSocketChannel serverSocketChannel;

    private final SocketChannel socketChannel;

    /**
     * worker 属性是暂时的
     */
    private NioEventLoop worker;

    private final Selector selector;

    private final SelectorProvider provider;

    public NioEventLoop(ServerSocketChannel serverSocketChannel,SocketChannel socketChannel) {
        this(null,SelectorProvider.provider(), null,serverSocketChannel, socketChannel);
    }
    public NioEventLoop(Executor executor, SelectorProvider selectorProvider, EventLoopTaskQueueFactory queueFactory,
                        ServerSocketChannel serverSocketChannel, SocketChannel socketChannel) {
        super(executor,queueFactory);
        if (selectorProvider == null) {
            throw new NullPointerException("selectorProvider");
        }
        if (serverSocketChannel != null && socketChannel != null) {
            throw new RuntimeException("only one channel can be here! server or client!");
        }
        this.provider = selectorProvider;
        this.serverSocketChannel = serverSocketChannel;
        this.socketChannel = socketChannel;
        this.selector = openSelector();
    }

    public void setWorker(NioEventLoop worker) {
        this.worker = worker;
    }

    private Selector openSelector() {
        //未包装过的选择器
        final Selector unwrappedSelector;
        try {
            unwrappedSelector = provider.openSelector();
            return unwrappedSelector;
        } catch (IOException e) {
            throw new RuntimeException("failed to open a new selector", e);
        }
    }

    public Selector unwrappedSelector() {
        return selector;
    }

    @Override
    protected void run() {
        for (;;) {
            try {
                //没有事件就阻塞在这里
                select();
                //如果有事件,就处理就绪事件
                processSelectedKeys();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //执行单线程执行器中的所有任务
                runAllTasks();
            }
        }
    }

    private void select() throws IOException {
        Selector selector = this.selector;
        //这里是一个死循环
        for (;;){
            //如果没有就绪事件，就在这里阻塞3秒
            int selectedKeys = selector.select(3000);
            //如果有事件或者单线程执行器中有任务待执行，就退出循环
            if (selectedKeys != 0 || hasTasks()) {
                break;
            }
        }
    }

    private void processSelectedKeys() throws Exception {
        //采用优化过后的方式处理事件,Netty默认会采用优化过的Selector对就绪事件处理。
        //processSelectedKeysOptimized();
        //未优化过的处理事件方式
        processSelectedKeysPlain(selector.selectedKeys());
    }

    private void processSelectedKeysPlain(Set<SelectionKey> selectedKeys) throws Exception {
        if (selectedKeys.isEmpty()) {
            return;
        }
        Iterator<SelectionKey> i = selectedKeys.iterator();
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

    /**
     * 这里的代码 十分臃肿，因为不知道传进来的是客户端channel 还是服务器channel
     * @param k
     * @throws IOException
     */
    private void processSelectedKey(SelectionKey k) throws IOException {
        //说明传进来的是客户端channel，要处理客户端的事件
        // 如果是 客户端的话 统一 由 当前类 也就是 boss 的 eventLoop 中 注册读事件和连接事件
        if (socketChannel != null) {
            if (k.isConnectable()) {
                //channel已经连接成功
                if (socketChannel.finishConnect()) {
                    //注册读事件
                    socketChannel.register(selector,SelectionKey.OP_READ);
                }
            }
            //如果是读事件
            if (k.isReadable()) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                int len = socketChannel.read(byteBuffer);
                byte[] buffer = new byte[len];
                byteBuffer.flip();
                byteBuffer.get(buffer);
                logger.info("客户端收到消息:{}",new String(buffer));
            }
            return;
        }
        //运行到这里说明是服务端的channel
        if (serverSocketChannel != null) {
            //连接事件
            if (k.isAcceptable()) {
                SocketChannel socketChannel = serverSocketChannel.accept();
                socketChannel.configureBlocking(false);
                //由worker执行器去执行注册
                // 服务端的 socket 读 由 work eventloop 去处理
                worker.registerRead(socketChannel,worker);
                socketChannel.write(ByteBuffer.wrap("我还不是netty，但我知道你上线了".getBytes()));
                logger.info("服务器发送消息成功！");
            }
            if (k.isReadable()) {
                SocketChannel channel = (SocketChannel)k.channel();
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                int len = channel.read(byteBuffer);
                if (len == -1) {
                    logger.info("客户端通道要关闭！");
                    channel.close();
                    return;
                }
                byte[] bytes = new byte[len];
                byteBuffer.flip();
                byteBuffer.get(bytes);
                logger.info("收到客户端发送的数据:{}",new String(bytes));
            }
        }
    }
}
