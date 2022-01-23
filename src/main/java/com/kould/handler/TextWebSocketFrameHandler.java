package com.kould.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;

public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    Logger logger = LoggerFactory.getLogger(TextWebSocketFrameHandler.class);

    private static final ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame twsf) throws Exception {
        Channel incoming = channelHandlerContext.channel() ;
        for (Channel channel : group) {
            if (channel != incoming) {
                channel.writeAndFlush(new TextWebSocketFrame("[" + incoming.remoteAddress() + "]" + twsf.text()));
            } else {
                channel.writeAndFlush(new TextWebSocketFrame("[you]" + twsf.text()));
            }
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        SocketAddress socketAddress = incoming.remoteAddress();
        for (Channel channel : group) {
            channel.writeAndFlush(new TextWebSocketFrame("[服务器] - " + socketAddress + "加入"));
        }
        group.add(incoming);
        logger.info("客户端:" + socketAddress + "加入");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        SocketAddress socketAddress = incoming.remoteAddress();
        for (Channel channel : group) {
            channel.writeAndFlush(new TextWebSocketFrame("[服务器] - " + socketAddress + "退出"));
        }
        group.add(incoming);
        logger.info("客户端:" + socketAddress + "退出");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        SocketAddress socketAddress = incoming.remoteAddress();
        logger.info("客户端:" + socketAddress + "在线");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("客户端:"+incoming.remoteAddress()+"下线");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        Channel incoming = ctx.channel();
        logger.info("客户端:"+incoming.remoteAddress()+"异常");
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }
}
