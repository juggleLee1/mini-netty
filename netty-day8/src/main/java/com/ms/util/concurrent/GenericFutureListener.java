package com.ms.util.concurrent;

import java.util.EventListener;

/**
 * 监听器的接口
 * @param <F>
 */
public interface GenericFutureListener<F extends Future<?>> extends EventListener {

    void operationComplete(F future) throws Exception;
}
