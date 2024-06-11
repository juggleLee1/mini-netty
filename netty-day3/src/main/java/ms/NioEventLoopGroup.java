package ms;

import java.nio.channels.SocketChannel;

/**
 * @Author: juggle
 * @Date: 2024/6/10 22:41
 * @Version:
 * @Description:
 **/
public class NioEventLoopGroup extends MultiThreadEventLoopGroup {

    public NioEventLoopGroup(int threads) {
        super(threads);
    }

    @Override
    protected EventLoop newChild() {
        return new NioEventLoop();
    }
}
