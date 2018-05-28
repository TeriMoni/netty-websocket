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
 * ����/����/��Ӧ�ͻ���websocket����
 * @author bin.liu
 *
 */
public class WebSocketHander extends SimpleChannelInboundHandler{
	
	private WebSocketServerHandshaker handshaker;
	
	private static final String WEB_SOCKET_URL = "ws://localhost:8888/webSocket";
	
	//�ͻ��������˴������ӵ���
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		NettyConfig.group.add(ctx.channel());
		System.out.println("�ͻ������������ӿ���...");
	}
	
	//�ͻ��������˶Ͽ����ӵ���
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		NettyConfig.group.remove(ctx.channel());
		System.out.println("�ͻ������������ӹر�...");
	}
	
	
	//�ͻ��˽��շ���˷������ݽ���ʱ�����
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
	
	
	//�ͻ���������ͨ�ŷ����쳣����
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
	
	
	//����˴���ͻ���websocket���ķ���
	protected void channelRead0(ChannelHandlerContext context, Object msg)
			throws Exception {
		//����ͻ��������˷�����http����
		if(msg instanceof FullHttpRequest){
			handerHttpRequest(context, (FullHttpRequest) msg);
		}else if(msg instanceof WebSocketFrame){ //����websocket����
			handerWebsocketFrame(context, (WebSocketFrame) msg);
		}
	}
	
	/**
	 * ����http����ҵ��
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
	 * ����websocket����ҵ��
	 * @param context
	 * @param frame
	 */
	private void handerWebsocketFrame(ChannelHandlerContext context,WebSocketFrame frame){
		
		//�ж��Ƿ���websoceket�ر�ָ��
		if(frame instanceof CloseWebSocketFrame){
			handshaker.close(context.channel(), (CloseWebSocketFrame) frame.retain());
		}
		//�ж��Ƿ���pingָ��
		if(frame instanceof PingWebSocketFrame){
			context.channel().write(new PongWebSocketFrame(frame.content().retain()));
			return;
		}
		//�ж��Ƿ��Ƕ�������Ϣ,����Ƕ�������Ϣ�����׳��쳣
		if(!(frame instanceof TextWebSocketFrame)){
			System.out.println("��֧�ֶ�������Ϣ");
			throw new RuntimeException("["+this.getClass().getName()+" ��֧����Ϣ]");
		}
		//����Ӧ����Ϣ
		//��ȡ�ͻ��������˷��͵���Ϣ
		String request = ((TextWebSocketFrame)frame).text();
		System.out.println("������յ��ͻ��˷��͵���Ϣ---->"+request);
		TextWebSocketFrame tws = new TextWebSocketFrame(new Date().toString() + context.channel().id() +"--->"+request);
		//Ⱥ����Ϣ����������������ӿͻ��˷�����Ϣ
		NettyConfig.group.writeAndFlush(tws);
	}
	
	/**
	 * �������Ӧ�ͻ��˵�http����
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
		//�������ͻ��˷�����Ϣ
		ChannelFuture future = context.channel().writeAndFlush(response);
		if(response.getStatus().code() != 200){
			future.addListener(ChannelFutureListener.CLOSE);
		}
	}
}
