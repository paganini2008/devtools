package com.github.paganini2008.springworld.socketbird.transport;

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

	void start();

	void stop();

	boolean isStarted();

}
