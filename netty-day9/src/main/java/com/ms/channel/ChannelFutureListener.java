package com.ms.channel;

import com.ms.util.concurrent.GenericFutureListener;


/**
 * 和channel有关的监听器
 */
public interface ChannelFutureListener extends GenericFutureListener<ChannelFuture> {

    ChannelFutureListener CLOSE = new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) {
            future.channel().close();
        }
    };


    ChannelFutureListener CLOSE_ON_FAILURE = new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) {
            if (!future.isSuccess()) {
                future.channel().close();
            }
        }
    };


//    ChannelFutureListener FIRE_EXCEPTION_ON_FAILURE = new ChannelFutureListener() {
//        @Override
//        public void operationComplete(ChannelFuture future) {
//            if (!future.isSuccess()) {
//                future.channel().pipeline().fireExceptionCaught(future.cause());
//            }
//        }
//    };

}
