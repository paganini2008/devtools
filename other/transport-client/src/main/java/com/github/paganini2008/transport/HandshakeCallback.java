package com.github.paganini2008.transport;

import java.net.SocketAddress;

/**
 * 
 * HandshakeCallback
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
@FunctionalInterface
public interface HandshakeCallback {

	void operationComplete(SocketAddress address);

}
