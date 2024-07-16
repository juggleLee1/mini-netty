package com.ms.test;

import com.ms.channel.ChannelHandlerContext;
import com.ms.channel.ChannelInboundHandlerAdapter;


public class TestHandlerTwo extends ChannelInboundHandlerAdapter {

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("TestHandlerTwo 第一个回调 handlerAdded");
        super.handlerAdded(ctx);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("TestHandlerTwo 第二个回调 channelRegistered");
        super.channelRegistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("TestHandlerTwo 第三个回调 channelActive");
        super.channelActive(ctx);
    }
}
