package com.ms.util.concurrent;

import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 循环组的接口,暂时先不继承ScheduledExecutorService接口了
 */
public interface EventExecutorGroup extends Executor{

    EventExecutor next();

    /**
     * 该方法暂不实现，
     */
    void shutdownGracefully();

    /**
     * 下面这两个方法暂时不实现，源码中并不在本接口中，这里只是为了不报错，暂时放在这里
     * @return
     */
    boolean isTerminated();

    void awaitTermination(Integer integer, TimeUnit timeUnit) throws InterruptedException;
}
