package ms;

import java.nio.channels.SocketChannel;

/**
 * @Author: juggle
 * @Date: 2024/6/11 10:43
 * @Version:
 * @Description:
 **/
public class Bootstrap {
    private EventLoopGroup eventLoopGroup;

    public Bootstrap() {

    }

    public Bootstrap group(EventLoopGroup eventLoopGroup) {
        this.eventLoopGroup = eventLoopGroup;
        return this;
    }

    public void register(SocketChannel channel) {
        eventLoopGroup.register(channel);
    }
}
