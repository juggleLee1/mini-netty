package ms;

import java.nio.channels.SocketChannel;

/**
 * @Author: juggle
 * @Date: 2024/6/10 22:43
 * @Version:
 * @Description:
 **/
public interface EventLoopGroup extends EventExecutorGroup {
    void register(SocketChannel channel);

    /***
     * 父接口 定义了 EventExecutor next(); 但是 子接口 重写了 EventLoop next();
     * 这里 EventLoop 必须是 EventExecutor 的子类或者子接口。否则会报错
     * @return
     */
    EventLoop next();
}
