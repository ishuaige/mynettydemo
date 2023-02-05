package com.netty.mynettydemo.chat.config;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import javax.annotation.Resource;

/**
 * 存储每个连接
 * @author niumazlb
 */
@Resource
public class NettyConfig {
    /**
     * 储存每个客户端接入进来的channel对象,如果需要额外信息可以改为map
     */
    public static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
}