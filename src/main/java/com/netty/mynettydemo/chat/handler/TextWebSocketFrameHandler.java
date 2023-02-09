package com.netty.mynettydemo.chat.handler;

import com.netty.mynettydemo.chat.config.NettyConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

/**
 * 用来解析websocket连接时的url参数
 * 参考：https://blog.csdn.net/mfkarj/article/details/103279559
 * @author niumazlb
 */
@Slf4j
@ChannelHandler.Sharable
public class TextWebSocketFrameHandler extends
		SimpleChannelInboundHandler<TextWebSocketFrame> {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		//有新的连接加入，就加到我们的连接组里
		Channel channel = ctx.channel();
		NettyConfig.group.add(channel);
	}

	/**
	 * 解析数据。第一次url参数也是走这个方法
	 * 注意：一定要回调父类该方法。不然内存会泄露。不要以为重写该方法后，在最后面添加清除ByteBuf告诉你这是无用的.
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (null != msg && msg instanceof FullHttpRequest) {
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

	/**
	 * 处理每次发送过来的数据。
	 * @param channelHandlerContext
	 * @param msg
	 * @throws Exception
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame msg) throws Exception {
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
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		NettyConfig.group.remove(channel);
	}
}