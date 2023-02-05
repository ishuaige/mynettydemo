package com.netty.mynettydemo.rpc.server.handler;



import com.netty.mynettydemo.rpc.message.RpcRequestMessage;
import com.netty.mynettydemo.rpc.message.RpcResponseMessage;
import com.netty.mynettydemo.rpc.server.service.ServicesFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;

/**
 * @author niumazlb
 * @create 2023-01-08 15:37
 */

public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage msg) throws Exception {
        String interfaceName = msg.getInterfaceName();
        String methodName = msg.getMethodName();
        int sequenceId = msg.getSequenceId();
        Object[] parameterValue = msg.getParameterValue();
        Class[] parameterTypes = msg.getParameterTypes();

        RpcResponseMessage response = new RpcResponseMessage();
        response.setSequenceId(sequenceId);
        //通过反射来方法调用
        try {
            Object service = ServicesFactory.getService(Class.forName(interfaceName));
            Method method = service.getClass().getMethod(methodName,parameterTypes);
            Object invoke = method.invoke(service, parameterValue);
            response.setReturnValue(invoke);
        } catch (Exception e) {
            e.printStackTrace();
            response.setExceptionValue(new Exception("error:"+e.getCause().getMessage()));
        }
        ctx.writeAndFlush(response);
    }
}
