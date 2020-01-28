package com.github.paganini2008.transport;

import java.net.SocketAddress;

/**
 * 
 * ChannelStateListener
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
public interface ChannelStateListener {

	default void onConnected(SocketAddress address) {
	}

	default void onClosed(SocketAddress address) {
	}

	default void onError(SocketAddress address, Throwable cause) {
	}
	
}
