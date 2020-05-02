package com.github.paganini2008.devtools.nio;

import java.io.IOException;

/**
 * 
 * NioChannel
 *
 * @author Fred Feng
 * @since 1.0
 */
public interface Channel {

	long write(Object message, int batchSize) throws IOException;

	long flush() throws IOException;

	long read();

	void close();

	boolean isActive();

}