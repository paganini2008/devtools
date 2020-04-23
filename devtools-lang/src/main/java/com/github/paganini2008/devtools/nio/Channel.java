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

	int write(Object message, int batchSize) throws IOException;
	
	int flush() throws IOException;

	int read(int bufferSize) throws IOException;

	void close();

	boolean isActive();

}