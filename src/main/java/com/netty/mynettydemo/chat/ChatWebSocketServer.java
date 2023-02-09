package com.netty.mynettydemo.chat;

import com.netty.mynettydemo.chat.handler.WebSocketServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 聊天室服务
 * @author niuma
 * @create 2023-02-05 15:58
 */
@Component
public class ChatWebSocketServer {


    public void run() {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap
                    // 设置主从线程组
                    .group(boss, work)
                    // 设置NIO双向通道类型
                    .channel(NioServerSocketChannel.class)
                    //设置子路由器
                    .childHandler(new WebSocketServerInitializer());
            Channel channel = serverBootstrap.bind(8089).sync().channel();
            channel.closeFuture().addListener(future -> {
                boss.shutdownGracefully();
                work.shutdownGracefully();
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap
                    // 设置主从线程组
                    .group(boss, work)
                    // 设置NIO双向通道类型
                    .channel(NioServerSocketChannel.class)
                    //设置子路由器
                    .childHandler(new WebSocketServerInitializer());
            Channel channel = serverBootstrap.bind(8089).sync().channel();
            channel.closeFuture().addListener(future -> {
                boss.shutdownGracefully();
                work.shutdownGracefully();
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
