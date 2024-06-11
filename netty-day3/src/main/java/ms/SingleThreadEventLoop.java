package ms;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @Author: juggle
 * @Date: 2024/6/10 21:23
 * @Version:
 * @Description:
 **/
public abstract class SingleThreadEventLoop extends SingleThreadEventExecutor implements EventLoop {

    @Override
    public EventLoop next() {
        return this;
    }

    @Override
    public void register(SocketChannel channel) {
        //如果执行该方法的线程就是执行器中的线程，直接执行方法即可
        EventLoop eventLoop = this;
        if (inEventLoop(Thread.currentThread())) {
            register0(channel, eventLoop);
        }else {
            //在这里，第一次向单线程执行器中提交任务的时候，执行器终于开始执行了,新的线程也开始创建
            this.execute(new Runnable() {
                @Override
                public void run() {
                    register0(channel, eventLoop);
                    LOGGER.info("客户端的channel已注册到新线程的多路复用器上了！");
                }
            });
        }
    }

    // 将注册作为一个异步任务 灌进去
    private void register0(SocketChannel socketChannel, EventLoop eventLoop) {
        try {
            socketChannel.configureBlocking(false);
            socketChannel.register(((NioEventLoop)eventLoop).getSelector(), SelectionKey.OP_READ);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


}
