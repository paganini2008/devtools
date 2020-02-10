package com.github.paganini2008.springworld.transport.transport;

/**
 * 
 * NioServer
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public interface NioServer {

	int start();

	void stop();

	boolean isStarted();

}
