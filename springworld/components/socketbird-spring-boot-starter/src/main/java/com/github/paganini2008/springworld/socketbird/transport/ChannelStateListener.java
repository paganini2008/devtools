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

	default void onServerConnected(SocketAddress address) {
	}

	default void onServerClosed(SocketAddress address) {
	}

	default void onServerError(SocketAddress address, Throwable cause) {
	}
	
	default void onClientConnected(SocketAddress address) {
	}

	default void onClientClosed(SocketAddress address) {
	}

	default void onClientError(SocketAddress address, Throwable cause) {
	}

}
