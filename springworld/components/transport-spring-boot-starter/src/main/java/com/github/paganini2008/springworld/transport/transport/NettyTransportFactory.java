package com.github.paganini2008.springworld.transport.transport;

import org.springframework.context.annotation.Configuration;

/**
 * 
 * NettyTransportFactory
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
@Configuration
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
