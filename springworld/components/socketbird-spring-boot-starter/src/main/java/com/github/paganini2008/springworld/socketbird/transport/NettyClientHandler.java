package com.github.paganini2008.springworld.socketbird.transport;

import org.springframework.beans.factory.annotation.Autowired;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * NettyClientHandler
 * 
 * @author Fred Feng
 * @version 1.0
 */
@Slf4j
@Sharable
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

	private final ChannelContext channelContext = new ChannelContext();

	@Autowired
	private ChannelStateListener channelStateListener;

	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		channelContext.addChannel(new NettyChannel(ctx.channel()));
		log.info("add channel " + ctx.channel());
		channelStateListener.onOpen(ctx.channel().remoteAddress());
	}

	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		channelContext.removeChannel(ctx.channel().remoteAddress());
		log.info("remove channel " + ctx.channel());
		channelStateListener.onClose(ctx.channel().remoteAddress());
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
		log.error(e.getMessage(), e);
		try {
			ctx.channel().close();
		} finally {
			channelContext.removeChannel(ctx.channel().remoteAddress());
			log.info("remove channel " + ctx.channel());
			
			channelStateListener.onError(ctx.channel().remoteAddress(), e);
		}
	}

	public void channelRead(ChannelHandlerContext ctx, Object data) throws Exception {
	}

	public ChannelContext getChannelContext() {
		return channelContext;
	}

}
