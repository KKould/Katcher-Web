package com.kould;

import com.kould.server.ServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class ServerApplication {

    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    private Channel channel ;

    public ChannelFuture start(InetSocketAddress address) {
        ServerBootstrap b = new ServerBootstrap();
        b.group(workerGroup).channel(NioServerSocketChannel.class)
                .childHandler(new ServerInitializer());
        ChannelFuture f = b.bind(address).syncUninterruptibly();
        channel = f.channel();
        return f ;
    }

    public void destroy() {
        if (channel != null) {
            channel.close();
        }
        workerGroup.shutdownGracefully();
    }

    public static void main(String[] args) {
        final ServerApplication server = new ServerApplication();
        ChannelFuture f = server.start(new InetSocketAddress(2048));
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run(){
                server.destroy();
            }
        });
        f.channel().closeFuture().syncUninterruptibly();
    }
}
