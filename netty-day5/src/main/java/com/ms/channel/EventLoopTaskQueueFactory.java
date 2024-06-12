package com.ms.channel;

import java.util.Queue;

/**
 * @Author: juggle
 * @Date: 2024/6/12 23:49
 * @Version:
 * @Description:  创建任务队列的工厂
 **/
public interface EventLoopTaskQueueFactory {
    Queue<Runnable> newTaskQueue(int maxCapacity);
}
