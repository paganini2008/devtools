package com.github.paganini2008.devtools.nio.examples;

import java.io.IOException;
import java.net.SocketAddress;

import com.github.paganini2008.devtools.nio.ChannelHandler;
import com.github.paganini2008.devtools.nio.Transformer;

/**
 * 
 * IoConnector
 *
 * @author Fred Feng
 * @since 1.0
 */
public interface IoConnector {

	void setTransformer(Transformer transformer);

	void addHandler(ChannelHandler channelHandler);

	void connect(SocketAddress remoteAddress) throws IOException;

	void write(Object object);

	void flush();

	boolean isActive();

	void close();

}