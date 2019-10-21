package com.github.paganini2008.springworld.socketbird;

import java.net.SocketAddress;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.paganini2008.springworld.socketbird.store.RedisStoreFactory;
import com.github.paganini2008.springworld.socketbird.store.Store;
import com.github.paganini2008.springworld.socketbird.store.StoreFactory;
import com.github.paganini2008.springworld.socketbird.transport.ChannelStateListener;
import com.github.paganini2008.springworld.socketbird.transport.LoopProcessor;
import com.github.paganini2008.springworld.socketbird.transport.NettyTransportFactory;
import com.github.paganini2008.springworld.socketbird.transport.NioClient;
import com.github.paganini2008.springworld.socketbird.transport.NioServer;
import com.github.paganini2008.springworld.socketbird.transport.Transport;
import com.github.paganini2008.springworld.socketbird.transport.TransportFactory;

/**
 * 
 * ImportConfiguration
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
@Configuration
public class ImportConfiguration {

	@ConditionalOnMissingBean(Serializer.class)
	@Bean
	public Serializer serializer() {
		return new KryoSerializer();
	}

	@Bean(initMethod = "start", destroyMethod = "stop")
	public LoopProcessor loopProcessor() {
		return new LoopProcessor();
	}

	@ConditionalOnMissingBean(Store.class)
	@Bean
	public StoreFactory storeFactory() {
		return new RedisStoreFactory();
	}

	@ConditionalOnMissingBean(Transport.class)
	@Bean
	public TransportFactory transportFactory() {
		return new NettyTransportFactory();
	}

	@Bean(initMethod = "start", destroyMethod = "stop")
	public NioServer nioServer(Transport transport) {
		return transport.getNioServer();
	}

	@Bean(initMethod = "open", destroyMethod = "close")
	public NioClient nioClient(Transport transport) {
		return transport.getNioClient();
	}

	@Bean
	public ChannelStateListener channelStateListener() {
		return new ChannelStateListener() {

			@Override
			public void onOpen(SocketAddress address) {
			}

			@Override
			public void onClose(SocketAddress address) {
			}

			@Override
			public void onError(SocketAddress address, Throwable cause) {
			}

		};
	}

}
