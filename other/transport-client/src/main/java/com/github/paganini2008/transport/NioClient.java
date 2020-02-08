package com.github.paganini2008.transport;

import java.util.concurrent.TimeUnit;

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
public interface NioClient extends LifeCycle, NioConnection {

	void setThreadCount(int nThreads);

	void watchConnection(int interval, TimeUnit timeUnit);

	void setIdleTimeout(int idleTime);

	void setSerializer(Serializer serializer);

	void send(Tuple tuple);

	void send(Tuple tuple, Partitioner partitioner);

}
