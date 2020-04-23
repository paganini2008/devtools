package com.github.paganini2008.devtools.nio;

import java.io.IOException;

/**
 * 
 * EmbedServer
 *
 * @author Fred Feng
 * @since 1.0
 */
public interface EmbedServer {

	void start() throws IOException;

	void stop() throws IOException;

	boolean isRunning();

}