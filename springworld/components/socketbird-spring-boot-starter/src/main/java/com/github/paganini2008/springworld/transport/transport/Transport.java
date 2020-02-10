package com.github.paganini2008.springworld.transport.transport;

import com.github.paganini2008.transport.NioClient;

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
