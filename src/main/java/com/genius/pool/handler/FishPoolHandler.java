package com.genius.pool.handler;

import com.genius.virgin.FishCenter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import com.genius.message.RequestRob;
import com.genius.message.ResponseFish;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FishPoolHandler extends SimpleChannelInboundHandler<RequestRob> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestRob msg) throws Exception {
        ResponseFish fish = new ResponseFish();
        System.out.println("开启远程调用");
        try{
            fish.setSequenceId(msg.getSequenceId());
            Object tempFish = FishCenter.getFish(msg.getServiceName());
            Object[] value = msg.getParameterValue();
            Class[] types = msg.getParameterTypes();
            Method method = tempFish.getClass().getMethod(msg.getMethod(),types);
            Object invoke = method.invoke(tempFish,value);
            fish.setReturnValue(invoke);
        }catch (Exception e){
            e.printStackTrace();
            String message = e.getCause().getMessage();
            fish.setExceptionValue(new Exception("远程调用出错:"+message));
            System.out.println(fish.getExceptionValue().getMessage());
        }
        ctx.writeAndFlush(fish);
    }
}
