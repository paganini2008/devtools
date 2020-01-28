package com.github.paganini2008.transport.netty;

import com.github.paganini2008.transport.ChannelContext;
import com.github.paganini2008.transport.ChannelStateListener;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 
 * NettyChannelContextAware
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
public abstract class NettyChannelContextAware extends ChannelInboundHandlerAdapter implements ChannelContext<Channel> {

	private ChannelStateListener channelStateListener;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		addChannel(ctx.channel());
		channelStateListener.onConnected(ctx.channel().remoteAddress());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		removeChannel(ctx.channel().remoteAddress());
		channelStateListener.onClosed(ctx.channel().remoteAddress());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		removeChannel(ctx.channel().remoteAddress());
		channelStateListener.onError(ctx.channel().remoteAddress(), cause);
	}

	@Override
	public void setChannelStateListener(ChannelStateListener channelStateListener) {
		this.channelStateListener = channelStateListener;
	}

}
