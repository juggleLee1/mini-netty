package ms;

import java.nio.channels.SocketChannel;

/**
 * @Author: juggle
 * @Date: 2024/6/11 0:04
 * @Version:
 * @Description:
 **/
public abstract class MultiThreadEventLoopGroup extends MultiThreadEventExecutorGroup implements EventLoopGroup {
    public MultiThreadEventLoopGroup(int threads) {
        super(threads);
    }


    @Override
    public void register(SocketChannel channel) {
        EventLoop eventLoop = next();
        eventLoop.register(channel);
    }

    @Override
    public EventLoop next() {
        return (EventLoop) super.next();
    }
}
