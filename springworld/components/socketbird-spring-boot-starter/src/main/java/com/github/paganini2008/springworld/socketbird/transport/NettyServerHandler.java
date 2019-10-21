package com.github.paganini2008.springworld.socketbird.transport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.github.paganini2008.springworld.socketbird.Tuple;
import com.github.paganini2008.springworld.socketbird.store.Store;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 
 * NettyServerHandlerAdapter
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
@Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter{

	@Autowired
	private Store store;

	@Value("${socketbird.store.collection.name}")
	private String collection;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object data) throws Exception {
		Tuple tuple = (Tuple) data;
		store.set(collection, tuple);
	}

}
