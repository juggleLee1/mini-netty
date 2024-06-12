package com.ms;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
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
    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException, IOException {
        //创建一个selector
        Selector selector = Selector.open();
        //创建一个服务端的通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        //创建一个promise
        DefaultPromise promise = new DefaultPromise();
        promise.addListener(new GenericListener<Promise<?>>() {
            @Override
            public void operationComplete(Promise<?> promise) throws Exception {
                //服务端channel绑定端口号
                System.out.println(333);
                serverSocketChannel.bind(new InetSocketAddress("127.0.0.1",8080));
            }
        });
        //创建一个runnable，异步任务
        Runnable runnable = () -> {
            //将channel注册到selector上,关注接收事件
            try {
                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            } catch (ClosedChannelException e) {
                throw new RuntimeException(e);
            }
            System.out.println(22);
            //在这里给promise中的result成员变量赋值
            promise.setSuccess(null);
        };

        //异步执行runnable任务
        Thread thread = new Thread(runnable);
        //启动线程
        thread.start();
        //主线程阻塞，直到promise.setSuccess(null)这行代码执行了才继续向下运行
        promise.sync();

        System.out.println(111);
    }
}
