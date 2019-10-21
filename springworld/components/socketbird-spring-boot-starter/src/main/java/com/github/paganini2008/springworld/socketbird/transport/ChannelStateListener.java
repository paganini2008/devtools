package com.github.paganini2008.springworld.socketbird.transport;

import java.net.SocketAddress;

/**
 * 
 * ChannelStateListener
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public interface ChannelStateListener {

	void onOpen(SocketAddress address);

	void onClose(SocketAddress address);

	void onError(SocketAddress address, Throwable cause);

}
