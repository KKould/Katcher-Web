package com.kould.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private final ChannelGroup group;

    public TextWebSocketFrameHandler(ChannelGroup group) {
        this.group = group;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame twsf) throws Exception {
        group.writeAndFlush(twsf.retain()) ;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            ctx.pipeline().remove(HttpRequestFileHandler.class);
            group.writeAndFlush(new TextWebSocketFrame("用户:" + ctx.channel().id() + "加入频道"));
            group.add(ctx.channel()) ;
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
