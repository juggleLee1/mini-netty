package com.ms.util.concurrent;

import java.util.concurrent.RejectedExecutionException;

/**
 * @Author: juggle
 * @Date: 2024/6/12 23:56
 * @Version:
 * @Description: 创建拒绝策略处理器
 **/
public class RejectedExecutionHandlers {
    private static final RejectedExecutionHandler REJECT = new RejectedExecutionHandler() {

        @Override
        public void reject(Runnable task, SingleThreadEventExecutor executor) {
            throw new RejectedExecutionException();
        }
    };

    private RejectedExecutionHandlers() { }


    public static RejectedExecutionHandler reject() {
        return REJECT;
    }
}
