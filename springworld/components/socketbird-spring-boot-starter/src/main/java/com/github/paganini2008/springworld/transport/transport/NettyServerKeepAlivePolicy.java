package com.github.paganini2008.springworld.transport.transport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.github.paganini2008.transport.ChannelEvent;
import com.github.paganini2008.transport.ChannelEvent.EventType;
import com.github.paganini2008.transport.ChannelEventListener;
import com.github.paganini2008.transport.Tuple;
import com.github.paganini2008.transport.netty.KeepAlivePolicy;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * NettyServerKeepAlivePolicy
 *
 * @author Fred Feng
 * @created 2020-01
 * @revised 2020-02
 * @version 1.0
 */
@Slf4j
public class NettyServerKeepAlivePolicy extends KeepAlivePolicy {

	private static final String PING = "PING";
	private static final String PONG = "PONG";

	@Value("${spring.transport.nioserver.keepalive.response:true}")
	private boolean keepaliveResposne;

	@Value("${spring.transport.nioserver.idleTimeout:60}")
	private int idleTimeout;

	@Autowired(required = false)
	private ChannelEventListener<Channel> channelEventListener;

	@Override
	protected void whenReaderIdle(ChannelHandlerContext ctx) {
		if (log.isTraceEnabled()) {
			log.trace("A keep-alive message was not received within {} second(s).", idleTimeout);
		}
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object data) throws Exception {
		if (isPing(data)) {
			if (channelEventListener != null) {
				channelEventListener.fireChannelEvent(new ChannelEvent<Channel>(ctx.channel(), EventType.PING, null));
			}
			if (keepaliveResposne) {
				ctx.writeAndFlush(Tuple.by(PONG));
			}
		} else {
			ctx.fireChannelRead(data);
		}
	}

	protected boolean isPing(Object data) {
		return (data instanceof Tuple) && (PING.equals(((Tuple) data).getField("content")));
	}

}
