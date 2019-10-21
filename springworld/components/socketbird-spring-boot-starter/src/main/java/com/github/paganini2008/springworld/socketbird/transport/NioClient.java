package com.github.paganini2008.springworld.socketbird.transport;

import java.net.SocketAddress;

import com.github.paganini2008.springworld.socketbird.Tuple;
import com.github.paganini2008.springworld.socketbird.utils.Partitioner;

/**
 * 
 * NioClient
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public interface NioClient {

	void open();

	void close();

	void connect(SocketAddress address);

	boolean isConnected(SocketAddress address);

	void send(Tuple tuple);

	void send(Tuple tuple, Partitioner partitioner);
}
