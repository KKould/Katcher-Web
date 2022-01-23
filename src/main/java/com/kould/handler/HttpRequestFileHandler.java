package com.kould.handler;

import com.kould.utils.HttpContentTypeUtils;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.RandomAccessFile;

public class HttpRequestFileHandler extends SimpleChannelInboundHandler<FullHttpRequest> {


    Logger logger = LoggerFactory.getLogger(HttpRequestFileHandler.class) ;

    private final String charsetCode ;

    private static final String charset_prefix = "; charset=" ;

    private static final String RESOURCE_PATH = System.getProperty("user.dir") + "\\src\\main\\resources\\";

    public HttpRequestFileHandler(String charsetCode) {
        this.charsetCode = charset_prefix + charsetCode  ;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        String uri = req.uri();
        File file = new File(RESOURCE_PATH + uri) ;
        if (!file.exists()) {
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            //判断是否是Http响应码100
            if (HttpUtil.is100ContinueExpected(req)) {
                FullHttpResponse response = new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
                ctx.writeAndFlush(response);
            }
            HttpResponse response = new DefaultHttpResponse(req.protocolVersion(), HttpResponseStatus.OK);
            boolean keepAlive = HttpUtil.isKeepAlive(req);
            if (keepAlive) {
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, file.length());
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            response.headers().set(HttpHeaderNames.CONTENT_TYPE,
                    //获取文件的后缀名并拿到对应的contentType
                    HttpContentTypeUtils.getContentType(uri.substring(uri.lastIndexOf("."))) + charsetCode) ;
            ctx.write(response);
            if (ctx.pipeline().get(SslHandler.class) == null) {
                // DefaultFileRegion是直接传输文件的
                // ChunkedNioFile是可以加工的
                ctx.write(new DefaultFileRegion(randomAccessFile.getChannel(), 0,  file.length()));
            } else {
                ctx.write(new ChunkedNioFile(randomAccessFile.getChannel())) ;
            }
            // LastHttpContent.EMPTY_LAST_CONTENT为结束标志
            ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT) ;
            if (!keepAlive) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
            randomAccessFile.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}

