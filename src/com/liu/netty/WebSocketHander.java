package com.liu.netty;

import java.util.Date;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.util.CharsetUtil;

/**
 * 接收/处理/相应客户端websocket请求
 * @author bin.liu
 *
 */
public class WebSocketHander extends SimpleChannelInboundHandler{
	
	private WebSocketServerHandshaker handshaker;
	
	private static final String WEB_SOCKET_URL = "ws://localhost:8888/webSocket";
	
	//客户端与服务端创建连接调用
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		NettyConfig.group.add(ctx.channel());
		System.out.println("客户端与服务端连接开启...");
	}
	
	//客户端与服务端断开连接调用
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		NettyConfig.group.remove(ctx.channel());
		System.out.println("客户端与服务端连接关闭...");
	}
	
	
	//客户端接收服务端发送数据结束时候调用
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
	
	
	//客户端与服务端通信发生异常调用
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
	
	
	//服务端处理客户端websocket核心方法
	protected void channelRead0(ChannelHandlerContext context, Object msg)
			throws Exception {
		//处理客户端与服务端发生的http请求
		if(msg instanceof FullHttpRequest){
			handerHttpRequest(context, (FullHttpRequest) msg);
		}else if(msg instanceof WebSocketFrame){ //处理websocket请求
			handerWebsocketFrame(context, (WebSocketFrame) msg);
		}
	}
	
	/**
	 * 处理http请求业务
	 * @param context
	 * @param request
	 */
	private void handerHttpRequest(ChannelHandlerContext context,FullHttpRequest request){
		if(!request.getDecoderResult().isSuccess() || !("websocket".equals(request.headers().get("Upgrade")))){
			sendHttpResponse(context, request, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
			return;
		}
		WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(WEB_SOCKET_URL, null, false);
		handshaker = wsFactory.newHandshaker(request);
		if(handshaker == null){
			WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(context.channel());
		}else{
			handshaker.handshake(context.channel(), request);
		}
	}
	
	
	/**
	 * 处理websocket请求业务
	 * @param context
	 * @param frame
	 */
	private void handerWebsocketFrame(ChannelHandlerContext context,WebSocketFrame frame){
		
		//判断是否是websoceket关闭指令
		if(frame instanceof CloseWebSocketFrame){
			handshaker.close(context.channel(), (CloseWebSocketFrame) frame.retain());
		}
		//判断是否是ping指令
		if(frame instanceof PingWebSocketFrame){
			context.channel().write(new PongWebSocketFrame(frame.content().retain()));
			return;
		}
		//判断是否是二进制消息,如果是二进制消息，则抛出异常
		if(!(frame instanceof TextWebSocketFrame)){
			System.out.println("不支持二进制消息");
			throw new RuntimeException("["+this.getClass().getName()+" 不支持消息]");
		}
		//返回应答消息
		//获取客户端向服务端发送的消息
		String request = ((TextWebSocketFrame)frame).text();
		System.out.println("服务端收到客户端发送的消息---->"+request);
		TextWebSocketFrame tws = new TextWebSocketFrame(new Date().toString() + context.channel().id() +"--->"+request);
		//群发消息，服务端向所有链接客户端发送消息
		NettyConfig.group.writeAndFlush(tws);
	}
	
	/**
	 * 服务端相应客户端的http请求
	 * @param context
	 * @param request
	 * @param response
	 */
	private void sendHttpResponse(ChannelHandlerContext context,FullHttpRequest request,DefaultFullHttpResponse response){
		
		if(response.getStatus().code() != 200){
			ByteBuf buf = Unpooled.copiedBuffer(response.getStatus().toString(),CharsetUtil.UTF_8);
			response.content().writeBytes(buf);
			buf.release();
		}
		//服务端像客户端发送消息
		ChannelFuture future = context.channel().writeAndFlush(response);
		if(response.getStatus().code() != 200){
			future.addListener(ChannelFutureListener.CLOSE);
		}
	}
}
