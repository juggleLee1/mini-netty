package com.ms.channel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * channel的顶级接口,暂时引入部分方法
 */
public interface Channel {

    ChannelId id();

    EventLoop eventLoop();


    Channel parent();


    ChannelConfig config();


    boolean isOpen();


    boolean isRegistered();


    boolean isActive();


    SocketAddress localAddress();


    SocketAddress remoteAddress();


    ChannelFuture closeFuture();

    /**
     * 该方法并不在此接口，而是在ChannelOutboundInvoker接口，现在先放在这里
     * @return
     */
    ChannelFuture close();

    /**
     * 该方法并不在此接口，而是在ChannelOutboundInvoker接口，现在先放在这里
     * @param localAddress
     * @param promise
     */
    void bind(SocketAddress localAddress, ChannelPromise promise);

    /**
     * 该方法并不在此接口，而是在ChannelOutboundInvoker接口，现在先放在这里
     * @param remoteAddress
     * @param localAddress
     * @param promise
     */
    void connect(SocketAddress remoteAddress, final SocketAddress localAddress,ChannelPromise promise);

    /**
     * 该方法并不在此接口，而是在unsafe接口，现在先放在这里
     * @param eventLoop
     * @param promise
     */
    void register(EventLoop eventLoop, ChannelPromise promise);

    /**
     * 该方法并不在此接口，而是在unsafe接口，现在先放在这里
     */
    void beginRead();
}
