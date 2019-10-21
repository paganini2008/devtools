package com.github.paganini2008.springworld.socketbird.transport;

/**
 * 
 * NettyTransportFactory
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public class NettyTransportFactory implements TransportFactory {

	@Override
	public Transport getObject() throws Exception {
		return new NettyTransport();
	}

	@Override
	public Class<?> getObjectType() {
		return NettyTransport.class;
	}

}