package com.ms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author: juggle
 * @Date: 2024/6/10 20:22
 * @Version:
 * @Description:
 **/
public abstract class SingleThreadEventExecutor implements Executor {
    public static final Logger LOGGER = LoggerFactory.getLogger(SingleThreadEventExecutor.class);
    //任务队列的容量，默认是Integer的最大值
    protected static final int DEFAULT_MAX_PENDING_TASKS = Integer.MAX_VALUE;

    private final Queue<Runnable> taskQueue;

    private final RejectedExecutionHandler rejectedExecutionHandler;

    private volatile boolean start = false;

    private Thread thread;

    public SingleThreadEventExecutor() {
        this.taskQueue = newTaskQueue(DEFAULT_MAX_PENDING_TASKS);
        this.rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();
    }

    private Queue<Runnable> newTaskQueue(int defaultMaxPendingTasks) {
        return new LinkedBlockingQueue<>(defaultMaxPendingTasks);
    }


    @Override
    public void execute(Runnable task) {
        if (task == null) {
            throw new NullPointerException("task");
        }
        //把任务提交到任务队列中
        addTask(task);
        //启动单线程执行器中的线程
        startThread();
    }

    private void startThread() {
        if (start) return;
        start = true;
        new Thread(() -> {
            //这里是得到了新创建的线程
            thread = Thread.currentThread();
            //执行run方法，在run方法中，就是对io事件的处理
            SingleThreadEventExecutor.this.run();
        }).start();
    }

    protected abstract void run();

    protected void runAllTasks() {
        runAllTasksFrom(taskQueue);
    }

    private void runAllTasksFrom(Queue<Runnable> taskQueue) {
        //从任务队列中拉取任务,如果第一次拉取就为null，说明任务队列中没有任务，直接返回即可
        Runnable task = pollTaskFrom(taskQueue);
        if (task == null) {
            return;
        }
        for (;;) {
            //执行任务队列中的任务
            safeExecute(task);
            //执行完毕之后，拉取下一个任务，如果为null就直接返回
            task = pollTaskFrom(taskQueue);
            if (task == null) {
                return;
            }
        }
    }

    private void safeExecute(Runnable task) {
        try {
            task.run();
        } catch (Throwable t) {
            LOGGER.warn("A task raised an exception. Task: {}", task, t);
        }
    }

    private Runnable pollTaskFrom(Queue<Runnable> taskQueue) {
        return taskQueue.poll();
    }

    protected boolean hasTasks() {
        return !taskQueue.isEmpty();
    }

    private void addTask(Runnable task) {
        if (task == null) {
            throw new RuntimeException("task is null");
        }

        if (!taskQueue.offer(task)) {
            reject(task);
        }
    }

    private void reject(Runnable task) {
    }

    public boolean inEventLoop(Thread thread) {
        return this.thread == thread;
    }
}
