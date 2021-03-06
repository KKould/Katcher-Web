package com.kould.katcher.handler;

import com.kould.katcher.utils.HttpContentTypeUtil;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.RandomAccessFile;
import java.net.URL;

public class HttpRequestFileHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    Logger logger = LoggerFactory.getLogger(HttpRequestFileHandler.class) ;

    private final String charsetCode ;

    private static final String charset_prefix = "; charset=" ;

    public HttpRequestFileHandler(String charsetCode) {
        this.charsetCode = charset_prefix + charsetCode  ;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        String uri = req.uri();
        URL fileUri = this.getClass().getResource(uri);
        if (fileUri == null) {
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
            RandomAccessFile randomAccessFile = new RandomAccessFile(fileUri.getFile(), "r");
            long fileLength = randomAccessFile.length();
            if (keepAlive) {
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, fileLength);
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }
            response.headers().set(HttpHeaderNames.CONTENT_TYPE,
                    //获取文件的后缀名并拿到对应的contentType
                    HttpContentTypeUtil.getContentType(uri.substring(uri.lastIndexOf("."))) + charsetCode) ;
            ctx.write(response);
            if (ctx.pipeline().get(SslHandler.class) == null) {
                // DefaultFileRegion是直接传输文件的
                // ChunkedNioFile是可以加工的
                ctx.write(new DefaultFileRegion(randomAccessFile.getChannel(), 0,  fileLength));
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

