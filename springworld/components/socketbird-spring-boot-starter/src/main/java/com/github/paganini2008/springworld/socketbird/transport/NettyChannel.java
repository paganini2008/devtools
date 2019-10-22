package com.github.paganini2008.springworld.socketbird.transport;

import java.net.SocketAddress;

import com.github.paganini2008.springworld.socketbird.Tuple;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * NettyChannel
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
@Slf4j
public class NettyChannel implements ChannelWrapper {

	private final Channel instance;

	public NettyChannel(Channel instance) {
		this.instance = instance;
	}

	public boolean isActive() {
		return instance.isActive();
	}

	public void send(Tuple tuple) {
		try {
			instance.writeAndFlush(tuple);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public void disconnect() {
		log.info("NettyChannel will be disconnected");
		instance.close();
	}

	public SocketAddress getRemoteAddr() {
		return instance.remoteAddress();
	}

	public Object getRawObject() {
		return instance;
	}
	
	public String toString() {
		return instance.toString();
	}

}
