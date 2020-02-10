package com.github.paganini2008.springworld.transport.transport;

import com.github.paganini2008.transport.NioClient;
import com.github.paganini2008.transport.netty.NettyClient;

/**
 * 
 * NettyTransport
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public class NettyTransport implements Transport {

	public NioClient getNioClient() {
		return new NettyClient();
	}

	public NioServer getNioServer() {
		return new NettyServer();
	}

}
