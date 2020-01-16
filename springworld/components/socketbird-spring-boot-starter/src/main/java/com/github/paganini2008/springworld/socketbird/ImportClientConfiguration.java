package com.github.paganini2008.springworld.socketbird;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.paganini2008.springworld.socketbird.transport.NettyClient;
import com.github.paganini2008.springworld.socketbird.transport.NioClient;

/**
 * 
 * ImportClientConfiguration
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
@ConditionalOnProperty(prefix = "socketbird", name = "mode", havingValue = "client")
@Configuration
public class ImportClientConfiguration {
	
	@ConditionalOnMissingBean(Serializer.class)
	@Bean
	public Serializer serializer() {
		return new KryoSerializer();
	}

	@Configuration
	@ConditionalOnProperty(name = "socketbird.transport", havingValue = "netty", matchIfMissing = true)
	public static class NettyTransportConfiguration {

		@Bean(initMethod = "open", destroyMethod = "close")
		public NioClient nioClient() {
			return new NettyClient();
		}

	}
	
}
