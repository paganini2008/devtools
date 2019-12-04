package com.github.paganini2008.springworld.socketbird;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

import com.github.paganini2008.springworld.cluster.implementor.MulticastChannelListener;
import com.github.paganini2008.springworld.socketbird.store.MemoryStoreFactory;
import com.github.paganini2008.springworld.socketbird.store.Store;
import com.github.paganini2008.springworld.socketbird.store.StoreFactory;
import com.github.paganini2008.springworld.socketbird.transport.ChannelContext;
import com.github.paganini2008.springworld.socketbird.transport.ChannelStateListener;
import com.github.paganini2008.springworld.socketbird.transport.LoggingChannelStateListener;
import com.github.paganini2008.springworld.socketbird.transport.LoopProcessor;
import com.github.paganini2008.springworld.socketbird.transport.NettyClient;
import com.github.paganini2008.springworld.socketbird.transport.NettyClientHandler;
import com.github.paganini2008.springworld.socketbird.transport.NettyServer;
import com.github.paganini2008.springworld.socketbird.transport.NettyServerHandler;
import com.github.paganini2008.springworld.socketbird.transport.NioClient;
import com.github.paganini2008.springworld.socketbird.transport.NioServer;
import com.github.paganini2008.springworld.socketbird.utils.Partitioner;
import com.github.paganini2008.springworld.socketbird.utils.RoundRobinPartitioner;

/**
 * 
 * ImportConfiguration
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
@Order(100)
@Import({ HandlerBeanAutoRegistryPostProcessor.class, MessageController.class })
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
		return new MemoryStoreFactory();
	}

	@Bean
	public ChannelContext channelContext() {
		return new ChannelContext();
	}

	@ConditionalOnMissingBean(Partitioner.class)
	@Bean
	public Partitioner partitioner() {
		return new RoundRobinPartitioner();
	}

	@ConditionalOnMissingBean(ChannelStateListener.class)
	@Bean
	public ChannelStateListener channelStateListener() {
		return new LoggingChannelStateListener();
	}

	@Bean("autobinding")
	public MulticastChannelListener multicastChannelListener() {
		return new SocketIoMulticastChannelListener();
	}

	@Configuration
	@ConditionalOnProperty(name = "socketbird.transport", havingValue = "netty", matchIfMissing = true)
	public static class NettyTransportConfiguration {

		@Bean(initMethod = "open", destroyMethod = "close")
		public NioClient nioClient() {
			return new NettyClient();
		}

		@Bean(initMethod = "start", destroyMethod = "stop")
		public NioServer nioServer() {
			return new NettyServer();
		}

		@Bean
		public NettyClientHandler clientHandler() {
			return new NettyClientHandler();
		}

		@Bean
		public NettyServerHandler serverHandler() {
			return new NettyServerHandler();
		}
	}

}
