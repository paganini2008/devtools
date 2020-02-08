package com.github.paganini2008.transport;

import java.net.SocketAddress;

/**
 * 
 * NioConnection
 *
 * @author Fred Feng
 * @created 2020-01
 * @revised 2020-02
 * @version 1.0
 */
public interface NioConnection {

	void connect(SocketAddress remoteAddress, HandshakeCallback handshakeCallback);

	boolean isConnected(SocketAddress remoteAddress);

}
