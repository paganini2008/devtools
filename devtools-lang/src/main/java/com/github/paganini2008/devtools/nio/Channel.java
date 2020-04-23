package com.github.paganini2008.devtools.nio;

import java.io.IOException;

/**
 * 
 * Channel
 *
 * @author Fred Feng
 * @since 1.0
 */
public interface Channel {

	void write(Object message, int batchSize) throws IOException;

	int read() throws IOException;

	void close();

	boolean isActive();

}