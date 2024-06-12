package com.ms.util.concurrent;

import com.ms.channel.EventLoopTaskQueueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * @Author: juggle
 * @Date: 2024/6/12 23:40
 * @Version:
 * @Description: 单线程执行器，实际上这个类就是一个单线程的线程池，netty中所有任务都是被该执行器执行的
 **/
public abstract class SingleThreadEventExecutor implements Executor {
    private static final Logger LOGGER = LoggerFactory.getLogger(SingleThreadEventExecutor.class);

    private static final int ST_NOT_STARTED = 1;
    private static final int ST_STARTED = 2;

    private volatile int state = ST_NOT_STARTED;
    private static final AtomicIntegerFieldUpdater<SingleThreadEventExecutor> STATE_UPDATER =
            AtomicIntegerFieldUpdater.newUpdater(SingleThreadEventExecutor.class, "state");

    protected static final int DEFAULT_MAX_PENDING_TASKS = Integer.MAX_VALUE;
    private final Queue<Runnable> taskQueue;
    private volatile Thread thread;
    //创建线程的执行器
    private Executor executor;
    private volatile boolean interrupted;
    private final RejectedExecutionHandler rejectedExecutionHandler;

    protected SingleThreadEventExecutor(Executor executor,
                                        EventLoopTaskQueueFactory queueFactory,
                                        ThreadFactory threadFactory) {
        this(executor,queueFactory,threadFactory, RejectedExecutionHandlers.reject());
    }

    protected SingleThreadEventExecutor(Executor executor,
                                        EventLoopTaskQueueFactory queueFactory,
                                        ThreadFactory threadFactory,
                                        RejectedExecutionHandler rejectedExecutionHandler) {
        if (executor == null) {
            this.executor = new ThreadPerTaskExecutor(threadFactory);
        }
        this.taskQueue = queueFactory == null? newTaskQueue(DEFAULT_MAX_PENDING_TASKS):queueFactory.newTaskQueue(DEFAULT_MAX_PENDING_TASKS);
        this.rejectedExecutionHandler = rejectedExecutionHandler;
    }

    private Queue<Runnable> newTaskQueue(int maxPendingTasks) {
        return new LinkedBlockingQueue<>(maxPendingTasks);
    }

    /**
     * nioEventLoop 实现
     */
    protected abstract void run();

    @Override
    public void execute(Runnable task) {
        if (task == null) {
            throw new RuntimeException("任务不能为空");
        }
        // 将任务添加到任务队列
        addTask(task);
        // 开始线程执行
        startThread();
    }

    private void startThread() {
        if (STATE_UPDATER.compareAndSet(this, ST_NOT_STARTED, ST_STARTED)) {
            boolean success = false;
            try {
                doStartThread();
                success = true;
            } finally {
                if (!success) {
                    STATE_UPDATER.compareAndSet(this, ST_STARTED, ST_NOT_STARTED);
                }
            }
        }
    }

    private void doStartThread() {
        // 创建线程 执行 nioEventLoop 的 run 方法
        executor.execute(new Runnable() {
            @Override
            public void run() {
                //Thread.currentThread得到的就是正在执行任务的单线程执行器的线程，这里把它赋值给thread属性十分重要
                //暂时先记住这一点
                thread = Thread.currentThread();
                if (interrupted) {
                    thread.interrupt();
                }
                //线程开始轮询处理IO事件，父类中的关键字this代表的是子类对象，这里调用的是nioeventloop中的run方法
                SingleThreadEventExecutor.this.run();
                LOGGER.info("单线程执行器的线程错误结束了！");
            }
        });
    }

    private void addTask(Runnable task) {
        if (!offer(task)) {
            reject(task);
        }
    }

    private void reject(Runnable task) {
        throw new RejectedExecutionException("event executor terminated");
    }

    private boolean offer(Runnable task) {
        return taskQueue.offer(task);
    }
}
