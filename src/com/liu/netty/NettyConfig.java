package com.liu.netty;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;




/**
 * nettyȫ�ֱ���������Ϣ��
 * @author bin.liu
 *
 */
public class NettyConfig {
	
	/**
	 * �洢ÿһ���ͻ��˽�����channel����
	 */
	public static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
}
