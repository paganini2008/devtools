package com.github.paganini2008.springworld.socketbird.transport;

import java.net.SocketAddress;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * LoggingChannelStateListener
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
@Slf4j
public class LoggingChannelStateListener implements ChannelStateListener {

	@Override
	public void onServerConnected(SocketAddress address) {
		log.trace("Connection to server: " + address);
	}

	@Override
	public void onServerClosed(SocketAddress address) {
		log.trace("Disconnection from server: " + address);
	}

	@Override
	public void onServerError(SocketAddress address, Throwable e) {
		log.trace(e.getMessage(), e);
	}

	@Override
	public void onClientConnected(SocketAddress address) {
		log.trace("Accept connection: " + address);
	}

	@Override
	public void onClientClosed(SocketAddress address) {
		log.trace("Refuse connection: " + address);
	}

	@Override
	public void onClientError(SocketAddress address, Throwable e) {
		log.trace(e.getMessage(), e);
	}

}
