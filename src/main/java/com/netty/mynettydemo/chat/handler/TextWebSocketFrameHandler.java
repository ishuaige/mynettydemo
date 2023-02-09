package com.netty.mynettydemo.chat.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * 用来解析websocket连接时的url参数
 * 参考：https://blog.csdn.net/mfkarj/article/details/103279559
 * @author niumazlb
 */
public class TextWebSocketFrameHandler extends
		SimpleChannelInboundHandler<TextWebSocketFrame> {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof FullHttpRequest) {
			FullHttpRequest request = (FullHttpRequest) msg;
			String uri = request.uri();
			String origin = request.headers().get("Origin");
			if (null == origin) {
				ctx.close();
			} else {
				if (null != uri && uri.contains("?")) {
					String[] uriArray = uri.split("\\?");
					if (null != uriArray && uriArray.length > 1) {
						String[] paramsArray = uriArray[1].split("=");
						if (null != paramsArray && paramsArray.length > 1) {
							//TODO 验证连接权限，不通过关闭
						}
					}
					//将uri设置回来，否则下一个handler通过不了就建立不了连接
					request.setUri("/ws");
				}
			}
		}
		super.channelRead(ctx, msg);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {

	}

}