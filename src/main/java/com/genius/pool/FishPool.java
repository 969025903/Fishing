package com.genius.pool;

import com.genius.config.Config;
import com.genius.pool.handler.FishPoolHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import com.genius.protocol.MessageCodec;
import com.genius.protocol.ProcotolFrameDecoder;

public class FishPool {
    private volatile ServerBootstrap pool;
    private final Object lock = new Object();
    private int port = Config.serverPort();
    private NioEventLoopGroup boss = new NioEventLoopGroup();
    private NioEventLoopGroup worker = new NioEventLoopGroup();
    private ServerBootstrap OpenPool(){
       if(pool==null){
           synchronized (lock){
               if (pool==null) {

                   LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
                   MessageCodec MESSAGE_CODEC = new MessageCodec();
                   FishPoolHandler FISH_POOL_HANDLER = new FishPoolHandler();
                       ServerBootstrap serverBootstrap = new ServerBootstrap();
                       serverBootstrap.channel(NioServerSocketChannel.class);
                       serverBootstrap.group(boss, worker);
                       serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                           @Override
                           protected void initChannel(SocketChannel ch) throws Exception {
                               ch.pipeline().addLast(new ProcotolFrameDecoder());
                               ch.pipeline().addLast(LOGGING_HANDLER);
                               ch.pipeline().addLast(MESSAGE_CODEC);
                               ch.pipeline().addLast(FISH_POOL_HANDLER);
                           }
                       });
                    pool = serverBootstrap;
               }
           }
       }
       return pool;
    }

    public void start(){
        OpenPool();
        try {
            Channel channel = pool.bind(port).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
