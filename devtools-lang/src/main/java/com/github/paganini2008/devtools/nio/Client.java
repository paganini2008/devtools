package com.github.paganini2008.devtools.nio;

import java.io.IOException;
import java.net.SocketAddress;

/**
 * 
 * Client
 *
 * @author Fred Feng
 * @since 1.0
 */
public interface Client {

	void connect(SocketAddress remoteAddress) throws IOException;

	void write(Object object);

	boolean isRunning();
	
	boolean isActive();

	void close() throws IOException;

}