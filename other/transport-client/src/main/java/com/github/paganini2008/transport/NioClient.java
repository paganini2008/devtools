package com.github.paganini2008.transport;

import java.net.SocketAddress;

import com.github.paganini2008.transport.serializer.Serializer;

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
	
	void setIdleTime(int idleTime);

	void connect(SocketAddress address, HandshakeCompletedListener completedListener);

	boolean isConnected(SocketAddress address);

	void setSerializer(Serializer serializer);

	void send(Tuple tuple);

	void send(Tuple tuple, Partitioner partitioner);

}
