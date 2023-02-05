package com.netty.mynettydemo.chat.handler;

import com.netty.mynettydemo.chat.config.NettyConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author niuma
 * @create 2023-02-05 16:07
 */
@Component
@ChannelHandler.Sharable
@Slf4j
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    @Override
    protected void channelRead0(ChannelHandlerContext cxt, TextWebSocketFrame msg){
        // 获取接收到的消息
        String content = msg.text();
        log.info("接收到的消息：{}" , content);
        for (Channel userChannel : NettyConfig.group) {
            //将消息转换成 TextWebSocketFrame 才能被web接收到
            TextWebSocketFrame webSocketFrame = new TextWebSocketFrame(content);
            userChannel.writeAndFlush(webSocketFrame);
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        //有新的连接加入，就加到我们的连接组里
        Channel channel = ctx.channel();
        NettyConfig.group.add(channel);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        NettyConfig.group.remove(channel);
    }
}
