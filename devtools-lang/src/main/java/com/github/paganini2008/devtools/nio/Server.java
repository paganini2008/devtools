package com.github.paganini2008.devtools.nio;

import java.io.IOException;

/**
 * 
 * Server
 *
 * @author Fred Feng
 * @since 1.0
 */
public interface Server {

	void start() throws IOException;

	void stop() throws IOException;

	boolean isRunning();

}