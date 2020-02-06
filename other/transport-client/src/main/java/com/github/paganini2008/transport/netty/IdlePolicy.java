package com.github.paganini2008.transport.netty;

import com.github.paganini2008.transport.Ping;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 
 * IdlePolicy
 *
 * @author Fred Feng
 * @created 2020-01
 * @revised 2020-02
 * @version 1.0
 */
@Sharable
public abstract class IdlePolicy extends ChannelInboundHandlerAdapter {

	public static final IdlePolicy CLOSE_BY_SERVER = new IdlePolicy() {

		protected void whenReaderIdle(ChannelHandlerContext ctx) {
			ctx.close();
		}

	};

	public static final IdlePolicy CLOSE_BY_CLIENT = new IdlePolicy() {

		protected void whenWriterIdle(ChannelHandlerContext ctx) {
			ctx.close();
		}

	};

	public static final IdlePolicy NOOP = new IdlePolicy() {
	};

	public static final IdlePolicy PING = new IdlePolicy() {

		protected void whenWriterIdle(ChannelHandlerContext ctx) {
			ctx.writeAndFlush(new Ping());
		}
	};

	public void channelRead(ChannelHandlerContext ctx, Object data) throws Exception {
		if (data instanceof Ping) {
			System.out.println(data);
		} else {
			ctx.fireChannelRead(data);
		}
	}

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
				whenAllIdle(ctx);
				break;
			}
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}

	protected void whenReaderIdle(ChannelHandlerContext ctx) {
	}

	protected void whenWriterIdle(ChannelHandlerContext ctx) {
	}

	protected void whenAllIdle(ChannelHandlerContext ctx) {
	}

}
