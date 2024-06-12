package com.ms;

import java.util.ArrayList;
import java.util.List;
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
    private int waiters;
    public static final Object SUCCESS = new Object();

    //这就是观察者模式中的监听器集合，回调函数就定义在监听器中
    private List<GenericListener> listeners = new ArrayList();

    //promise和future的区别就是，promise可以让用户自己设置成功的返回值，
    //也可以设置失败后返回的错误
    public Promise<T> setSuccess(T result) {
        if (setSuccess0(result)) {
            return this;
        }
        throw new IllegalStateException("complete already: " + this);
    }

    private boolean setSuccess0(T result) {
        //设置成功结果，如果结果为null，则将SUCCESS赋值给result
        return set(result == null ? SUCCESS : result);
    }

    private boolean set(Object object) {
        if (object != null) {
            this.result = object;
            checkNotifyWaiters();
            notifyListener();
            return true;
        }
        return false;
    }

    private void notifyListener() {
        for (GenericListener listener : listeners) {
            try {
                listener.operationComplete(this);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    //添加监听器的方法
    public Promise<T> addListener(GenericListener<? extends Promise<? super T>> listener) {
        synchronized (this) {
            //添加监听器
            listeners.add(listener);
        }
        //判断任务是否完成，实际上就是检查result是否被赋值了
        if (isDone()) {
            //唤醒监听器，让监听器去执行
            notifyListener();
        }
        //最后返回当前对象
        return this;
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

    //服务器和客户端经常会调用该方法同步等待结果
    public Promise<T> sync() throws InterruptedException {
        await();
        return this;
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
