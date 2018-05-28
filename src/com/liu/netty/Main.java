package com.liu.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * �������,����Ӧ��
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
			System.out.println("����˿����ȴ��ͻ�������....");
			Channel channel = bootstrap.bind(8888).sync().channel();
			channel.closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			//�˳�����
			bossGroup.shutdownGracefully();
			workGroup.shutdownGracefully();
		}
	}

}
