package com.genius.fishMan;

import com.alibaba.nacos.api.exception.NacosException;
import com.genius.fishMan.handler.FishManHandler;
import com.genius.message.RequestRob;
import com.genius.protocol.MessageCodec;
import com.genius.protocol.ProcotolFrameDecoder;
import com.genius.util.InetUtils;
import com.genius.virgin.NacosServerDiscovery;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class FishMan {
    private static final AtomicInteger id ;
    private static Map<String,Channel> readyRob;

    private static final Bootstrap bootstrap;
    private static NacosServerDiscovery nd;
    private static NioEventLoopGroup group;

    static {
        bootstrap = new Bootstrap();
        group = new NioEventLoopGroup();
        initChannel();
        readyRob = new ConcurrentHashMap<>();
        id = new AtomicInteger();
        nd = new NacosServerDiscovery(null);
    }

    private static int nextId(){
        return id.getAndIncrement();
    }

    public static <T> T goFishing(String url,Object...parameter){
        String[] res = url.split("/");
        T fish = null;
        if ((res.length<2)) {
            throw new RuntimeException("错误的路径");
        }
        String service = res[0];
        String method = res[1];
        try {
            InetSocketAddress ndService = nd.getService(service); //从nacos拉取
            //TODO 根据nacos返回的服务进行方法调用
            fish = getProxyService(ndService,service,method,parameter);
        }catch (NacosException e){
            e.setErrMsg("没有该服务");
        }
        return fish;
    }

    public static <T> T getProxyService(InetSocketAddress address,String serviceName,String method,Object...parameters){
        Class[] types = new Class[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            types[i] = parameters[i].getClass();
        }
        int sequenceId = nextId();
         RequestRob msg = new RequestRob(
             sequenceId,
             serviceName,
             method,
             types,
             parameters
         );
        Channel channel = getChannel(address);
        if(channel==null||!channel.isActive()||!channel.isRegistered()){
            return null;
        }
        channel.writeAndFlush(msg);
        DefaultPromise<Object> promise = new DefaultPromise<>(channel.eventLoop());
        FishManHandler.PROMISES.put(sequenceId, promise);
        try {
            promise.await();
            if(promise.isSuccess()) {
                // 调用正常
                return(T)promise.getNow();
            } else {
                // 调用失败
                throw new RuntimeException(promise.cause());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
         return null;
    }

    private static Channel getChannel(InetSocketAddress address){
        String strAddress = address.toString();
        synchronized (strAddress){
             if(!readyRob.containsKey(strAddress)){
                 tryConnect(address);
             }
        }
        Channel channel = readyRob.get(strAddress);
        if(channel!=null&&channel.isActive()){
            return channel;
        }
        readyRob.remove(strAddress);
        return null;
    }

    /**
     * 尝试连接服务，并且将其放入连接列表中
     * @param address
     * @return
     */
    private static Channel tryConnect(InetSocketAddress address){
        Channel channel = null;
        try {
            channel = bootstrap.connect(address.getAddress(), address.getPort()).sync().channel();
            readyRob.put(address.toString(),channel);
            channel.closeFuture().addListener(future -> {
                group.shutdownGracefully();
            });
        } catch (Exception e) {
            log.error("client error", e);
            return null;
        }
        readyRob.put(address.toString(),channel);
        return channel;
    }
    // 初始化 channel 方法
    private static Bootstrap initChannel() {
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodec MESSAGE_CODEC = new MessageCodec();
        FishManHandler FISH_MAN_HANDLER = new FishManHandler();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ProcotolFrameDecoder());
                ch.pipeline().addLast(MESSAGE_CODEC);
                ch.pipeline().addLast(LOGGING_HANDLER);
                ch.pipeline().addLast(FISH_MAN_HANDLER);
            }
        });
       return bootstrap;
    }

}
