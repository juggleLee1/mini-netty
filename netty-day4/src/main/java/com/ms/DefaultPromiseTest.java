package com.ms;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @Author: juggle
 * @Date: 2024/6/11 23:42
 * @Version:
 * @Description:
 **/
public class DefaultPromiseTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                //睡眠一会
                Thread.sleep(5000);
                return 1314;
            }
        };
        //创建一个DefaultPromise，把任务传进DefaultPromise中
        Promise<Integer> promise = new DefaultPromise<Integer>(callable);
        //创建一个线程
        Thread t = new Thread(promise);
        t.start();
        //无超时获取结果
//        System.out.println(promise.get());
        //有超时获取结果
        System.out.println(promise.get(500, TimeUnit.MILLISECONDS));
    }
}
