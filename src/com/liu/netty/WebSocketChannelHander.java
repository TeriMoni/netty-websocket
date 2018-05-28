package com.liu.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * 初始化链接组件
 * @author bin.liu
 *
 */
public class WebSocketChannelHander extends ChannelInitializer{

	protected void initChannel(Channel ch) throws Exception {
		ch.pipeline().addLast("http-codec",new HttpServerCodec());// HttpServerCodec：将请求和应答消息解码为HTTP消息
		ch.pipeline().addLast("aggregator",new HttpObjectAggregator(65536));//HttpObjectAggregator：将HTTP消息的多个部分合成一条完整的HTTP消息
		ch.pipeline().addLast("http-chunked",new ChunkedWriteHandler());//ChunkedWriteHandler：向客户端发送HTML5文件
		ch.pipeline().addLast("hander",new WebSocketHander());//在管道中添加我们自己的接收数据实现方法
	}

}
