package com.github.paganini2008.springworld.socketbird;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import com.github.paganini2008.springworld.cluster.multicast.ContextMulticastEventHandler;
import com.github.paganini2008.springworld.socketbird.buffer.BufferZone;
import com.github.paganini2008.springworld.socketbird.buffer.RedisBufferZone;
import com.github.paganini2008.springworld.socketbird.transport.ChannelContext;
import com.github.paganini2008.springworld.socketbird.transport.ChannelStateListener;
import com.github.paganini2008.springworld.socketbird.transport.InternalNettyClient;
import com.github.paganini2008.springworld.socketbird.transport.LoggingChannelStateListener;
import com.github.paganini2008.springworld.socketbird.transport.LoopProcessor;
import com.github.paganini2008.springworld.socketbird.transport.NettyClientHandler;
import com.github.paganini2008.springworld.socketbird.transport.NettyServer;
import com.github.paganini2008.springworld.socketbird.transport.NettyServerHandler;
import com.github.paganini2008.springworld.socketbird.transport.NioClient;
import com.github.paganini2008.springworld.socketbird.transport.NioServer;
import com.github.paganini2008.springworld.socketbird.utils.Partitioner;
import com.github.paganini2008.springworld.socketbird.utils.RoundRobinPartitioner;

/**
 * 
 * ImportServerConfiguration
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
@ConditionalOnProperty(prefix = "socketbird", name = "mode", havingValue = "server", matchIfMissing = true)
@Configuration
public class ImportServerConfiguration {

	@ConditionalOnMissingBean(Serializer.class)
	@Bean
	public Serializer serializer() {
		return new KryoSerializer();
	}

	@Bean(destroyMethod = "stop")
	public LoopProcessor loopProcessor() {
		return new LoopProcessor();
	}

	@ConditionalOnMissingBean(BufferZone.class)
	@Bean(initMethod = "configure", destroyMethod = "destroy")
	public BufferZone bufferZone() {
		return new RedisBufferZone();
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

	@Bean
	public ContextMulticastEventHandler autoConnectionEventListener() {
		return new AutoConnectionEventListener();
	}
	
	@Primary
	@Bean
	public ContextInitializer contextInitializer() {
		return new ContextInitializer();
	}

	@Bean
	public HandlerBeanPostProcessor handlerBeanPostProcessor() {
		return new HandlerBeanPostProcessor();
	}

	@Bean("redistemplate-bigint")
	public RedisTemplate<String, Long> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Long> redisTemplate = new RedisTemplate<String, Long>();
		redisTemplate.setKeySerializer(RedisSerializer.string());
		redisTemplate.setValueSerializer(new GenericToStringSerializer<Long>(Long.class));
		redisTemplate.setExposeConnection(true);
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}

	@Bean("counter-bigint")
	public RedisAtomicLong redisAtomicLong(@Qualifier("redistemplate-bigint") RedisTemplate<String, Long> redisTemplate) {
		return new RedisAtomicLong("socketbird:counter", redisTemplate);
	}

	@Bean(initMethod = "start", destroyMethod = "stop")
	public Counter counter(@Qualifier("counter-bigint") RedisAtomicLong redisAtomicLong) {
		return new Counter(redisAtomicLong);
	}

	@Configuration
	@ConditionalOnProperty(name = "socketbird.transport", havingValue = "netty", matchIfMissing = true)
	public static class NettyTransportConfiguration {

		@Bean(initMethod = "open", destroyMethod = "close")
		public NioClient nioClient() {
			return new InternalNettyClient();
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
