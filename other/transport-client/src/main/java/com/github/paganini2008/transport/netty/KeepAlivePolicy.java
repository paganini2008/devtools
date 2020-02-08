package com.github.paganini2008.transport.netty;

import com.github.paganini2008.transport.KeepAliveTimeoutException;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 
 * KeepAlivePolicy
 *
 * @author Fred Feng
 * @created 2020-01
 * @revised 2020-02
 * @version 1.0
 */
@Sharable
public abstract class KeepAlivePolicy extends ChannelInboundHandlerAdapter {

	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent e = (IdleStateEvent) evt;
			switch (e.state()) {
			case READER_IDLE:
				whenReaderIdle(ctx);
				break;
			case WRITER_IDLE:
				whenWriterIdle(ctx);
				break;
			case ALL_IDLE:
				whenBothIdle(ctx);
				break;
			}
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}

	protected void whenReaderIdle(ChannelHandlerContext ctx) {
		throw new KeepAliveTimeoutException("Reading Idle.");
	}

	protected void whenWriterIdle(ChannelHandlerContext ctx) {
		throw new KeepAliveTimeoutException("Writing Idle.");
	}

	protected void whenBothIdle(ChannelHandlerContext ctx) {
		throw new KeepAliveTimeoutException("Reading or Writing Idle.");
	}

}
