package com.ms.channel;


import com.ms.util.concurrent.EventExecutorGroup;

import java.nio.channels.ServerSocketChannel;


/**
 * 事件循环组接口
 */
public interface EventLoopGroup extends EventExecutorGroup {

    @Override
    EventLoop next();


}
