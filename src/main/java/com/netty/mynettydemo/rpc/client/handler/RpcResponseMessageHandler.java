package com.netty.mynettydemo.rpc.client.handler;


import com.netty.mynettydemo.rpc.message.RpcResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ChannelHandler.Sharable
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {

    //                       序号      用来接收结果的 promise 对象
    public static final Map<Integer, Promise<Object>> PROMISES = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage msg) throws Exception {
        log.debug("{}", msg);
        int sequenceId = msg.getSequenceId();
        Object returnValue = msg.getReturnValue();
        Exception exceptionValue = msg.getExceptionValue();
        Promise<Object> promise = PROMISES.remove(sequenceId);
        if(promise != null){
            if(exceptionValue != null){
                promise.setFailure(exceptionValue);
            }else {
                promise.setSuccess(returnValue);
            }
        }
    }
}
