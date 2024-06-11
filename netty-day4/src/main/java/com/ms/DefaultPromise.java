package com.ms;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @Author: juggle
 * @Date: 2024/6/11 23:26
 * @Version:
 * @Description:
 **/
public class DefaultPromise<T> implements Promise<T> {

    private volatile Object result;
    private Callable<T> callable;
    private int waiters;

    public DefaultPromise(Callable<T> callable) {
        this.callable = callable;
    }

    @Override
    public void run() {
        T object;
        try {
            object = callable.call();
            set(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void set(T object) {
        this.result = object;
        checkNotifyWaiters();
    }

    private synchronized void checkNotifyWaiters() {
        if (waiters > 0) {
            notifyAll();
        }
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return result != null;
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        if (result == null) {
            await();
        }
        return getNow();
    }

    //等待结果的方法
    public Promise<T> await() throws InterruptedException {
        //如果已经执行完成，直接返回即可
        if (isDone()) {
            return this;
        }
        //如果线程中断，直接抛出异常
        if (Thread.interrupted()) {
            throw new InterruptedException(toString());
        }
        //wait要和synchronized一起使用，在futurtask的源码中
        //这里使用了LockSupport.park方法
        synchronized (this) {
            //如果成功赋值则直接返回，不成功进入循环
            while (!isDone()) {
                //waiters字段加一，记录在此阻塞的线程数量
                ++waiters;
                try {
                    //释放锁并等待
                    wait();
                } finally {
                    //等待结束waiters字段减一
                    --waiters;
                }
            }
        }
        return this;
    }

    private T getNow() {
        return (T) result;
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (await(timeout, unit)) {
            return getNow();
        }
        return null;
    }

    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return await0(unit.toMillis(timeout), true);
    }

    private boolean await0(long timeoutNanos, boolean interruptable) throws InterruptedException {
        //执行成功则直接返回
        if (isDone()) {
            return true;
        }
        //传入的时间小于0则直接判断是否执行完成
        if (timeoutNanos <= 0) {
            return isDone();
        }
        //interruptable为true则允许抛出中断异常，为false则不允许，判断当前线程是否被中断了
        //如果都为true则抛出中断异常
        if (interruptable && Thread.interrupted()) {
            throw new InterruptedException(toString());
        }
        //获取当前纳秒时间
        long startTime = System.currentTimeMillis();
        //用户设置的等待时间
        long waitTime = timeoutNanos;
        for (;;) {
            synchronized (this) {
                //再次判断是否执行完成了
                if (isDone()) {
                    return true;
                }
                //如果没有执行完成，则开始阻塞等待，阻塞线程数加一
                ++waiters;
                try {
                    //阻塞在这里
                    wait(timeoutNanos);
                } finally {
                    //阻塞线程数减一
                    --waiters;
                }
            }
            //走到这里说明线程被唤醒了
            if (isDone()) {
                return true;
            } else {
                //可能是虚假唤醒。
                //System.nanoTime() - startTime得到的是经过的时间
                //得到新的等待时间，如果等待时间小于0，表示已经阻塞了用户设定的等待时间。如果waitTime大于0，则继续循环
                waitTime = timeoutNanos - (System.currentTimeMillis() - startTime);
                if (waitTime <= 0) {
                    return isDone();
                }
            }
        }
    }
}
