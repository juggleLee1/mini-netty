package com.ms.channel;

import java.net.SocketAddress;

/**
 * 这个接口定义了channel出站时候的方法，这样到channelHandler的时候才明白，但这里要提前引入一下
 * 到时候我会再讲这个的。这里多说一句，在netty中，很多接口定义了一些同名方法，这只是为了让某个类可以调用，但真正干活的
 * 是另一个类中的同名方法。就像NioEventLoopGroup和NioEventLoop那样，一个负责管理，一个负责真正执行
 *
 */
public interface ChannelOutboundInvoker {

    ChannelFuture bind(SocketAddress localAddress);

    ChannelFuture connect(SocketAddress remoteAddress);

    ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress);

    ChannelFuture disconnect();

    ChannelFuture close();

    ChannelFuture deregister();

    ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise);

    ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise);

    ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise);

    ChannelFuture disconnect(ChannelPromise promise);

    ChannelFuture close(ChannelPromise promise);

    ChannelFuture deregister(ChannelPromise promise);

    ChannelOutboundInvoker read();

    ChannelFuture write(Object msg);

    ChannelFuture write(Object msg, ChannelPromise promise);

    ChannelOutboundInvoker flush();

    ChannelFuture writeAndFlush(Object msg, ChannelPromise promise);

    ChannelFuture writeAndFlush(Object msg);

    ChannelPromise newPromise();

    ChannelFuture newSucceededFuture();

    ChannelFuture newFailedFuture(Throwable cause);
}
