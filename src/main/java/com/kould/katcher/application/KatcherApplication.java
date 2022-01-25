package com.kould.katcher.application;

import com.kould.katcher.config.KatcherConfig;
import com.kould.katcher.server.ServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.net.InetSocketAddress;

/**
 * Katcher服务启动入口类
 */
public class KatcherApplication {

    Logger logger = LoggerFactory.getLogger(KatcherApplication.class);

    private static final String BANNER = "\n" +
            "88      a8P                                   88                                      \n" +
            "88    ,88'                  ,d                88                                      \n" +
            "88  ,88\"                    88                88                                      \n" +
            "88,d88'       ,adPPYYba,  MM88MMM  ,adPPYba,  88,dPPYba,    ,adPPYba,  8b,dPPYba,     \n" +
            "8888\"88,      \"\"     `Y8    88    a8\"     \"\"  88P'    \"8a  a8P_____88  88P'   \"Y8     \n" +
            "88P   Y8b     ,adPPPPP88    88    8b          88       88  8PP\"\"\"\"\"\"\"  88             \n" +
            "88     \"88,   88,    ,88    88,   \"8a,   ,aa  88       88  \"8b,   ,aa  88             \n" +
            "88       Y8b  `\"8bbdP\"Y8    \"Y888  `\"Ybbd8\"'  88       88   `\"Ybbd8\"'  88";

    //用于接受响应，较少线程上下文消耗
    private final EventLoopGroup bossGroup = new NioEventLoopGroup();

    //用于处理响应
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    private Channel channel ;

    public ChannelFuture start(InetSocketAddress address) {
        logger.info(BANNER);
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
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

    public static void run(String[] args, int port, String scanPath) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(KatcherConfig.class);
        ctx.scan(scanPath);
        ctx.refresh();
        final KatcherApplication server = new KatcherApplication();
        ChannelFuture f = server.start(new InetSocketAddress(port));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.destroy();
            ctx.close();
        }));
        f.channel().closeFuture().syncUninterruptibly();
    }
}
