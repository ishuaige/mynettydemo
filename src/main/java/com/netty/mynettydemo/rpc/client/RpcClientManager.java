package com.netty.mynettydemo.rpc.client;


import com.netty.mynettydemo.rpc.client.handler.RpcResponseMessageHandler;
import com.netty.mynettydemo.rpc.message.RpcRequestMessage;
import com.netty.mynettydemo.rpc.protocol.MessageCodecSharable;
import com.netty.mynettydemo.rpc.protocol.ProtocolFrameDecoder;
import com.netty.mynettydemo.rpc.server.service.HelloService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultPromise;

import java.lang.reflect.Proxy;
import java.util.Random;

/**
 * @author niumazlb
 * @create 2023-01-08 15:51
 */
public class RpcClientManager {
    public static void main(String[] args) {
        HelloService proxyService = getProxyService(HelloService.class);
        System.out.println(proxyService.sayHello("zhangsan"));
//        System.out.println(proxyService.sayHello("lisi"));
//        System.out.println(proxyService.sayHello("wangwu"));
//        System.out.println(proxyService.sayHello("zhaoliu"));

    }

    public static <T> T getProxyService(Class<T> serviceClass) {
        ClassLoader loader = serviceClass.getClassLoader();

        Class<?>[] interfaces = new Class[]{serviceClass};
        Object o = Proxy.newProxyInstance(loader, interfaces, (proxy, method, args) -> {
            Random random = new Random();
            int sequenceId = random.nextInt();
            RpcRequestMessage message = new RpcRequestMessage(
                    sequenceId,
                    serviceClass.getName(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    args
            );

            getChannel().writeAndFlush(message);

            DefaultPromise<Object> promise = new DefaultPromise<>(getChannel().eventLoop());
            RpcResponseMessageHandler.PROMISES.put(sequenceId, promise);


            promise.await();

            if (promise.isSuccess()) {
                return promise.getNow();
            } else {
                throw new RuntimeException(promise.cause());
            }
        });
        return (T) o;
    }


    private static Channel channel = null;
    private static final Object LOCK = new Object();

    public static Channel getChannel() {
        if (channel != null) {
            return channel;
        }
        synchronized (LOCK) {
            if (channel != null) {
                return channel;
            }
            initChannel();
            return channel;
        }
    }

    private static void initChannel() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        RpcResponseMessageHandler RPC_HANDLER = new RpcResponseMessageHandler();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ProtocolFrameDecoder());
                ch.pipeline().addLast(LOGGING_HANDLER);
                ch.pipeline().addLast(MESSAGE_CODEC);
                ch.pipeline().addLast(RPC_HANDLER);
            }
        });
        try {
            channel = bootstrap.connect("localhost", 8080).sync().channel();
            channel.closeFuture().addListener(future -> {
                group.shutdownGracefully();
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
