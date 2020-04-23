package com.github.paganini2008.devtools.nio;

import java.io.IOException;
import java.net.SocketAddress;

/**
 * 
 * EmbedClient
 *
 * @author Fred Feng
 * @since 1.0
 */
public interface EmbedClient {

	void connect(SocketAddress remoteAddress) throws IOException;

	void write(Object object);
	
	void flush();

	boolean isRunning();
	
	boolean isActive();

	void close() throws IOException;

}