package com.github.paganini2008.springworld.socketbird.transport;

/**
 * 
 * Transport
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public interface Transport {

	NioServer getNioServer();
	
	NioClient getNioClient();

}
