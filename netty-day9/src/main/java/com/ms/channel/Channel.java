package com.ms.channel;

import sun.misc.Unsafe;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * channel的顶级接口,暂时引入部分方法
 */
public interface Channel extends  ChannelOutboundInvoker{

    ChannelId id();


    EventLoop eventLoop();


    Channel parent();


//    ChannelConfig config();


    boolean isOpen();


    boolean isRegistered();


    boolean isActive();


    SocketAddress localAddress();


    SocketAddress remoteAddress();


    ChannelFuture closeFuture();

    /**
     * 终于引入了Unsafe类
     * @return
     */
    Unsafe unsafe();

    @Override
    Channel read();

    @Override
    Channel flush();

    /**
     * 看到这个接口中的方法，是不是发现很多都和ChannelOutboundInvoker这个类中的重复？
     * 稍微想一想就会明白，channel调用方法，但真正执行还是由unsafe的实现类来执行，虽然最后有可能还是调用到channel中
     */
    interface Unsafe {

        SocketAddress localAddress();

        SocketAddress remoteAddress();

        void register(EventLoop eventLoop, ChannelPromise promise);

        void bind(SocketAddress localAddress, ChannelPromise promise);

        void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise);

        void disconnect(ChannelPromise promise);

        void close(ChannelPromise promise);

        void closeForcibly();

        void deregister(ChannelPromise promise);

        void beginRead();

        void write(Object msg, ChannelPromise promise);

        void flush();


    }
}
