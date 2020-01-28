package com.github.paganini2008.transport;

import java.net.SocketAddress;

/**
 * 
 * NioClient
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
public interface NioClient extends LifeCycle {

	void connect(SocketAddress address);

	boolean isConnected(SocketAddress address);

	void setSerializer(Serializer serializer);
	
	void send(Tuple tuple);

	void send(Tuple tuple, Partitioner partitioner);

}
