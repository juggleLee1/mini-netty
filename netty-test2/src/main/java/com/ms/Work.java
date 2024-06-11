package com.ms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author: juggle
 * @Date: 2024/6/10 20:00
 * @Version:
 * @Description:
 **/
public class Work implements Runnable {
    public static final Logger LOGGER = LoggerFactory.getLogger(Work.class);

    private Thread thread;
    private Selector workSelector = Selector.open();
    private boolean flags;

    public Work() throws IOException {
        thread = new Thread(this);
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public Selector getWorkSelector() {
        return workSelector;
    }

    public void setWorkSelector(Selector workSelector) {
        this.workSelector = workSelector;
    }

    public void start() {
        if (flags) return;
        flags = true;
        thread.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                workSelector.select();
                Set<SelectionKey> selectionKeys = workSelector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();
                    if (key.isReadable()) {
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        int read = channel.read(buffer);
                        LOGGER.info("读到的字节数：" + read);
                        if (read == -1) {
                            channel.close();
                            break;
                        }else {
                            //切换buffer的读模式
                            buffer.flip();
                            LOGGER.info(Charset.defaultCharset().decode(buffer).toString());
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
