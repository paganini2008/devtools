package com.github.paganini2008.springworld.logsink;

import com.github.paganini2008.springworld.socketbird.Tuple;

/**
 * 
 * NioClient
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
public interface NioClient {

	void connect(String host, int port) throws Exception;

	void close();

	void send(Tuple tuple);

}
