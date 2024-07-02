package com.ms.util.concurrent;

import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Author: PP-jessica
 * @Description:循环组的接口,暂时先不继承ScheduledExecutorService接口了
 */
public interface EventExecutorGroup extends ScheduledExecutorService, Iterable<EventExecutor> {

    EventExecutor next();

    /**
     * @Author: PP-jessica
     * @Description:下面这三个方法暂时不实现，源码中并不在本接口中，这里只是为了不报错，暂时放在这里
     */
    void shutdownGracefully();


    //boolean isShuttingDown();


    //Future<?> shutdownGracefully();


    //Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit);

    //Future<?> terminationFuture();

//    @Override
//    @Deprecated
//    void shutdown();
//
//    @Override
//    @Deprecated
//    List<Runnable> shutdownNow();


    @Override
    Iterator<EventExecutor> iterator();


    @Override
    Future<?> submit(Runnable task);

    @Override
    <T> Future<T> submit(Runnable task, T result);

    @Override
    <T> Future<T> submit(Callable<T> task);

    @Override
    ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit);

    @Override
    <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit);

    @Override
    ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit);

    @Override
    ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit);
}
