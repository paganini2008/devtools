package com.github.paganini2008.transport;

import java.net.SocketAddress;

/**
 * 
 * HandshakeCompletedListener
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
@FunctionalInterface
public interface HandshakeCompletedListener {

	void operationComplete(SocketAddress address);

}
