package com.ms.bootstrap;

import com.ms.util.internal.ObjectUtil;
import com.ms.util.internal.StringUtil;
import com.ms.channel.Channel;
import com.ms.channel.ChannelFactory;
import com.ms.channel.EventLoopGroup;

import java.net.SocketAddress;


/**
 * @Author: PP-jessica
 * @Description:我们这门课上到这里，我想应该不用再让我多解释这些抽象类的功能了吧类中没几个方法，随便看看就能明白。，
 */
public abstract class AbstractBootstrapConfig<B extends AbstractBootstrap<B, C>, C extends Channel> {

    protected final B bootstrap;

    protected AbstractBootstrapConfig(B bootstrap) {
        this.bootstrap = ObjectUtil.checkNotNull(bootstrap, "bootstrap");
    }

    public final SocketAddress localAddress() {
        return bootstrap.localAddress();
    }


    @SuppressWarnings("deprecation")
    public final ChannelFactory<? extends C> channelFactory() {
        return bootstrap.channelFactory();
    }



    @SuppressWarnings("deprecation")
    public final EventLoopGroup group() {
        return bootstrap.group();
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder()
                .append(StringUtil.simpleClassName(this))
                .append('(');
        EventLoopGroup group = group();
        if (group != null) {
            buf.append("group: ")
                    .append(StringUtil.simpleClassName(group))
                    .append(", ");
        }
        @SuppressWarnings("deprecation")
        ChannelFactory<? extends C> factory = channelFactory();
        if (factory != null) {
            buf.append("channelFactory: ")
                    .append(factory)
                    .append(", ");
        }
        SocketAddress localAddress = localAddress();
        if (localAddress != null) {
            buf.append("localAddress: ")
                    .append(localAddress)
                    .append(", ");
        }
        if (buf.charAt(buf.length() - 1) == '(') {
            buf.append(')');
        } else {
            buf.setCharAt(buf.length() - 2, ')');
            buf.setLength(buf.length() - 1);
        }
        return buf.toString();
    }
}
