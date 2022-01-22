package com.kould.handler;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.RandomAccessFile;

/**
 *
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {


    Logger logger = LoggerFactory.getLogger(HttpRequestHandler.class) ;

    private final String wsUri ;

    private static final String RESOURCE_PATH = System.getProperty("user.dir") + "\\src\\main\\resources\\";

    public HttpRequestHandler(String wsUri) {
        this.wsUri = wsUri ;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        String uri = req.uri();
        logger.error(uri);
        if (!wsUri.equalsIgnoreCase(uri)) {
            ctx.fireChannelRead(req.retain()) ;
        } else {
            if (HttpUtil.is100ContinueExpected(req)) {
                FullHttpResponse response = new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
                ctx.writeAndFlush(response);
            }
            RandomAccessFile file = new RandomAccessFile( RESOURCE_PATH + "/index.html"
                    , "r");
            HttpResponse response = new DefaultHttpResponse(req.protocolVersion(), HttpResponseStatus.OK);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
            boolean keepAlive = HttpUtil.isKeepAlive(req);
            logger.info(String.valueOf(file.length()));
            if (keepAlive) {
                logger.info(String.valueOf(file.length()));
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, file.length());
                response.headers().set(HttpHeaderNames.CONNECTION,HttpHeaderValues.KEEP_ALIVE);
            }
            ctx.write(response);
            if (ctx.pipeline().get(SslHandler.class) == null) {
                // DefaultFileRegion是直接传输文件的
                // ChunkedNioFile是可以加工的
                ctx.write(new DefaultFileRegion(file.getChannel(), 0,  file.length()));
            } else {
                ctx.write(new ChunkedNioFile(file.getChannel())) ;
            }
            // LastHttpContent.EMPTY_LAST_CONTENT为结束标志
            ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT) ;
            if (!keepAlive) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
            file.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}

