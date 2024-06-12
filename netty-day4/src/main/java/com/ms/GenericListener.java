package com.ms;

/**
 * @Author: juggle
 * @Date: 2024/6/12 22:32
 * @Version:
 * @Description:
 **/
public interface GenericListener<P extends Promise<?>> {
    void operationComplete(P promise) throws Exception;
}
