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
            giveFish(msg, fish);
        }catch (Exception e){
            e.printStackTrace();
            String message = e.getCause().getMessage();
            fish.setExceptionValue(new Exception("远程调用出错:"+message));
        }
        ctx.writeAndFlush(fish);
    }

    private void giveFish(RequestRob msg, ResponseFish fish) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        fish.setSequenceId(msg.getSequenceId());
        Object tempFish = FishCenter.getFish(msg.getServiceName());
        for (Class parameterType : msg.getParameterTypes()) {
            System.out.println(parameterType);
        }
        Method method = tempFish.getClass().getMethod(msg.getMethod(), msg.getParameterTypes());
        Object invoke = method.invoke(tempFish,msg.getParameterValue());
        fish.setReturnValue(invoke);
    }
}
