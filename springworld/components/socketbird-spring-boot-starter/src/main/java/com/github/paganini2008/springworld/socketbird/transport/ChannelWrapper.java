package com.github.paganini2008.springworld.socketbird.transport;

import java.net.SocketAddress;

import com.github.paganini2008.springworld.socketbird.Tuple;

/**
 * 
 * ChannelWrapper
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public interface ChannelWrapper {

	boolean isActive();
	
	void disconnect();

	void send(Tuple tuple);

	SocketAddress getRemoteAddr();

	Object getRawObject();

}
