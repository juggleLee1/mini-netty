package com.ms.channel;

import java.util.Queue;

/**
 * 创建任务队列的工厂
 */
public interface EventLoopTaskQueueFactory {


    Queue<Runnable> newTaskQueue(int maxCapacity);
}

