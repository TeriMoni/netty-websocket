package com.liu.netty;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;




/**
 * netty全局变量配置信息类
 * @author bin.liu
 *
 */
public class NettyConfig {
	
	/**
	 * 存储每一个客户端进来的channel对象
	 */
	public static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
}
