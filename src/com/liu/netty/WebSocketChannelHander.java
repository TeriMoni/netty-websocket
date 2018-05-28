package com.liu.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * ��ʼ���������
 * @author bin.liu
 *
 */
public class WebSocketChannelHander extends ChannelInitializer{

	protected void initChannel(Channel ch) throws Exception {
		ch.pipeline().addLast("http-codec",new HttpServerCodec());// HttpServerCodec���������Ӧ����Ϣ����ΪHTTP��Ϣ
		ch.pipeline().addLast("aggregator",new HttpObjectAggregator(65536));//HttpObjectAggregator����HTTP��Ϣ�Ķ�����ֺϳ�һ��������HTTP��Ϣ
		ch.pipeline().addLast("http-chunked",new ChunkedWriteHandler());//ChunkedWriteHandler����ͻ��˷���HTML5�ļ�
		ch.pipeline().addLast("hander",new WebSocketHander());//�ڹܵ�����������Լ��Ľ�������ʵ�ַ���
	}

}
