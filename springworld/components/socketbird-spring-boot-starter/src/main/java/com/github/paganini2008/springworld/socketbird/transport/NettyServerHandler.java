package com.github.paganini2008.springworld.socketbird.transport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.github.paganini2008.springworld.socketbird.Tuple;
import com.github.paganini2008.springworld.socketbird.buffer.BufferZone;

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
public class NettyServerHandler extends ChannelInboundHandlerAdapter{

	@Autowired
	private BufferZone store;

	@Value("${socketbird.bufferzone.collectionName}")
	private String collectionName;
	
	@Autowired
	private ChannelStateListener channelStateListener;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		channelStateListener.onClientConnected(ctx.channel().remoteAddress());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		channelStateListener.onClientClosed(ctx.channel().remoteAddress());
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		channelStateListener.onClientError(ctx.channel().remoteAddress(), cause);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object data) throws Exception {
		Tuple tuple = (Tuple) data;
		store.set(collectionName, tuple);
	}

}
