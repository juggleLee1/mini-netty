package com.ms.channel.nio;

import com.ms.channel.Channel;
import com.ms.channel.ChannelPipeline;

import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

public abstract class AbstractNioByteChannel extends AbstractNioChannel{

    protected AbstractNioByteChannel(Channel parent, SelectableChannel ch) {
        super(parent, ch, SelectionKey.OP_READ);
    }

    @Override
    protected AbstractNioUnsafe newUnsafe() {
        return new NioByteUnsafe();
    }

    protected class NioByteUnsafe extends AbstractNioUnsafe {
        /**
         * @Author: PP-jessica
         * @Description:该方法回到了正确的位置
         */
        @Override
        public final void read() {
            //得到ChannelPipeline
            final ChannelPipeline pipeline = pipeline();
            //暂时用最原始简陋的方法处理
            ByteBuffer byteBuf = ByteBuffer.allocate(1024);
            try {
                int bytes = doReadBytes(byteBuf);
                //源码中并没有下面这个判断分支，这里这么写是为了再客户端channel关闭的时候，服务端可以不报错。后面我们会逐步完善。
                if (bytes == -1) {
                    return;
                }
                //把数据向后面的handler传递做处理
                pipeline.fireChannelRead(byteBuf);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected abstract int doReadBytes(ByteBuffer buf) throws Exception;

    @Override
    protected void doWrite(Object masg) throws Exception {

    }
}
