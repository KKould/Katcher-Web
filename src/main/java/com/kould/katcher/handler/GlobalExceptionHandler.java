package com.kould.katcher.handler;

import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlobalExceptionHandler extends ChannelDuplexHandler {

    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(cause!=null){
            try {
                throw cause;
            } catch (Throwable e) {
                logger.error(e.getMessage());
            }
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ctx.write(msg,promise.addListener((ChannelFutureListener) channelFuture -> {
            if(!channelFuture.isSuccess()){
                try {
                    throw channelFuture.cause();
                } catch (Throwable e) {
                    logger.error(e.getMessage());
                }
            }
        }));
    }
}

