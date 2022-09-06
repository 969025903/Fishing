package com.genius.pool.handler;

import com.genius.virgin.FishCenter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import com.genius.message.RequestRob;
import com.genius.message.ResponseFish;

import java.lang.reflect.Method;

public class FishPoolHandler extends SimpleChannelInboundHandler<RequestRob> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestRob msg) throws Exception {
        ResponseFish fish = new ResponseFish();
        fish.setSequenceId(msg.getSequenceId());
        try{
            Object tempFish = FishCenter.getFish(msg.getClass());
            Method method = tempFish.getClass().getMethod(msg.getMethod(), msg.getParameterTypes());
            Object invoke = method.invoke(tempFish,msg.getParameterValue());
            fish.setReturnValue(invoke);
        }catch (Exception e){
            e.printStackTrace();
            String message = e.getCause().getMessage();
            fish.setExceptionValue(new Exception("远程调用出错:"+message));
        }
        ctx.writeAndFlush(fish);
    }

}
