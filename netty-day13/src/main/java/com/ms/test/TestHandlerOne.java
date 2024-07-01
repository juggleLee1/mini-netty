package com.ms.test;

import com.ms.channel.ChannelHandlerContext;
import com.ms.channel.ChannelInboundHandlerAdapter;

import java.nio.ByteBuffer;


public class TestHandlerOne extends ChannelInboundHandlerAdapter {

    /**
     * @Author: PP-jessica
     * @Description:验证一下我们这节课着重讲解的回调方法，根据我们讲解的内容，应该是handlerAdded方法最先被回调，因为当服务端channel
     * 注册到单线程执行器成功的那一刻， pipeline.invokeHandlerAddedIfNeeded()就会被执行，接着会执行pipeline.fireChannelRegistered();
     * 最后，在NioServerSocketChannel绑定端口号成功之后，执行pipeline.fireChannelActive();，表明通channel被激活了
     * 这里我们就按顺序验证一下。因为没有测试客户端发送数据，所以我们暂时不测试channelRead方法。
     * 当然，随着我们课程的进展，handler中的回调方法都会被我们讲解到。
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuffer byteBuffer = (ByteBuffer)msg;
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);
        System.out.println("channelRead中输出的+++++++客户端收到消息:{}"+new String(bytes));
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }
}
