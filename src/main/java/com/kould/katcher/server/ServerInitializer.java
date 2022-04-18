package com.kould.katcher.server;

import com.kould.katcher.handler.GlobalExceptionHandler;
import com.kould.katcher.handler.HttpRequestFileHandler;
import com.kould.katcher.handler.HttpRequestActionHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

public class ServerInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline() ;
        pipeline.addLast(new HttpServerCodec()) ;
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new ChunkedWriteHandler()) ;
        pipeline.addLast(new HttpRequestActionHandler());
        pipeline.addLast(new HttpRequestFileHandler("UTF-8"));
        pipeline.addLast(new GlobalExceptionHandler());
    }
}
