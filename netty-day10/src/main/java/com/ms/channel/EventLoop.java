package com.ms.channel;

import com.ms.util.concurrent.EventExecutor;


/**
 * 事件循环组的接口
 */
public interface EventLoop extends EventExecutor, EventLoopGroup{

    @Override
    EventLoopGroup parent();
}
