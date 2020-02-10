package com.github.paganini2008.springworld.transport.transport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.github.paganini2008.springworld.transport.buffer.BufferZone;
import com.github.paganini2008.transport.ChannelEvent;
import com.github.paganini2008.transport.ChannelEvent.EventType;
import com.github.paganini2008.transport.ChannelEventListener;
import com.github.paganini2008.transport.Tuple;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 
 * NettyServerHandler
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
@Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

	@Autowired
	private BufferZone store;

	@Value("${spring.transport.bufferzone.collectionName:default}")
	private String collectionName;

	@Autowired(required = false)
	private ChannelEventListener<Channel> channelEventListener;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		fireChannelEvent(ctx.channel(), EventType.CONNECTED, null); 
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		fireChannelEvent(ctx.channel(), EventType.CLOSED, null); 
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		fireChannelEvent(ctx.channel(), EventType.FAULTY, cause); 
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object data) throws Exception {
		store.set(collectionName, (Tuple) data);
	}

	private void fireChannelEvent(Channel channel, EventType eventType, Throwable cause) {
		if (channelEventListener != null) {
			channelEventListener.fireChannelEvent(new ChannelEvent<Channel>(channel, eventType, cause));
		}
	}

}
