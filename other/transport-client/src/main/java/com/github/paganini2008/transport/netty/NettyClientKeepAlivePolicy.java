package com.github.paganini2008.transport.netty;

import java.util.concurrent.atomic.AtomicLong;

import com.github.paganini2008.transport.Tuple;

import io.netty.channel.ChannelHandlerContext;

/**
 * 
 * NettyClientKeepAlivePolicy
 *
 * @author Fred Feng
 * @created 2020-01
 * @revised 2020-02
 * @version 1.0
 */
public class NettyClientKeepAlivePolicy extends KeepAlivePolicy {

	private final AtomicLong serial = new AtomicLong(0);

	protected void whenWriterIdle(ChannelHandlerContext ctx) {
		Tuple ping = Tuple.by("PING");
		ping.setField("serial", serial.incrementAndGet());
		ctx.channel().writeAndFlush(ping);
	}
	
	public long getSerial() {
		return serial.get();
	}
	
}
