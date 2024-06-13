package com.ms.util.concurrent;

/**
 * @Author: juggle
 * @Date: 2024/6/12 23:57
 * @Version:
 * @Description:
 **/
public interface RejectedExecutionHandler {
    void rejected(Runnable task, SingleThreadEventExecutor executor);
}
