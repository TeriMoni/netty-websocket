package com.liu.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 程序入口,启动应用
 * @author bin.liu
 *
 */
public class Main {
	
	public static void main(String[] args) {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workGroup);
			bootstrap.channel(NioServerSocketChannel.class);
			bootstrap.childHandler(new WebSocketChannelHander());
			System.out.println("服务端开启等待客户端链接....");
			Channel channel = bootstrap.bind(8888).sync().channel();
			channel.closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			//退出程序
			bossGroup.shutdownGracefully();
			workGroup.shutdownGracefully();
		}
	}

}
