package com.github.paganini2008.springworld.socketbird;

import org.apache.mina.core.session.IoSession;
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
import com.github.paganini2008.springworld.socketbird.transport.MinaChannelEventListener;
import com.github.paganini2008.springworld.socketbird.transport.MinaServer;
import com.github.paganini2008.springworld.socketbird.transport.MinaServerHandler;
import com.github.paganini2008.springworld.socketbird.transport.NettyChannelEventListener;
import com.github.paganini2008.springworld.socketbird.transport.NettyServerKeepAlivePolicy;
import com.github.paganini2008.springworld.socketbird.transport.NettyServer;
import com.github.paganini2008.springworld.socketbird.transport.NettyServerHandler;
import com.github.paganini2008.springworld.socketbird.transport.NioServer;
import com.github.paganini2008.transport.ChannelEventListener;
import com.github.paganini2008.transport.NioClient;
import com.github.paganini2008.transport.Partitioner;
import com.github.paganini2008.transport.RoundRobinPartitioner;
import com.github.paganini2008.transport.mina.MinaClient;
import com.github.paganini2008.transport.mina.MinaSerializationCodecFactory;
import com.github.paganini2008.transport.netty.KeepAlivePolicy;
import com.github.paganini2008.transport.netty.NettyClient;
import com.github.paganini2008.transport.netty.NettySerializationCodecFactory;
import com.github.paganini2008.transport.serializer.KryoSerializer;
import com.github.paganini2008.transport.serializer.Serializer;

import io.netty.channel.Channel;

/**
 * 
 * TransportServerConfiguration
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
@ConditionalOnProperty(prefix = "socketbird", name = "mode", havingValue = "server", matchIfMissing = true)
@Configuration
public class TransportServerConfiguration {

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

	@ConditionalOnMissingBean(Partitioner.class)
	@Bean
	public Partitioner partitioner() {
		return new RoundRobinPartitioner();
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
	@ConditionalOnProperty(name = "socketbird.transport.nioserver", havingValue = "netty", matchIfMissing = true)
	public static class NettyTransportConfiguration {

		@Bean(initMethod = "open", destroyMethod = "close")
		public NioClient nioClient(Serializer serializer) {
			NettyClient nioClient = new NettyClient();
			nioClient.setSerializer(serializer);
			return nioClient;
		}

		@Bean(initMethod = "start", destroyMethod = "stop")
		public NioServer nioServer() {
			return new NettyServer();
		}

		@ConditionalOnMissingBean(KeepAlivePolicy.class)
		@Bean
		public KeepAlivePolicy idlePolicy() {
			return new NettyServerKeepAlivePolicy();
		}

		@Bean
		public NettySerializationCodecFactory codecFactory(Serializer serializer) {
			return new NettySerializationCodecFactory(serializer);
		}

		@Bean
		public NettyServerHandler serverHandler() {
			return new NettyServerHandler();
		}

		@ConditionalOnMissingBean(ChannelEventListener.class)
		@Bean
		public ChannelEventListener<Channel> channelEventListener() {
			return new NettyChannelEventListener();
		}
	}

	@Configuration
	@ConditionalOnProperty(name = "socketbird.transport.nioserver", havingValue = "mina")
	public static class MinaTransportConfiguration {

		@Bean(initMethod = "open", destroyMethod = "close")
		public NioClient nioClient(Serializer serializer) {
			MinaClient nioClient = new MinaClient();
			nioClient.setSerializer(serializer);
			return nioClient;
		}

		@Bean(initMethod = "start", destroyMethod = "stop")
		public NioServer nioServer() {
			return new MinaServer();
		}

		@Bean
		public MinaSerializationCodecFactory codecFactory(Serializer serializer) {
			return new MinaSerializationCodecFactory(serializer);
		}

		@Bean
		public MinaServerHandler serverHandler() {
			return new MinaServerHandler();
		}

		@ConditionalOnMissingBean(ChannelEventListener.class)
		@Bean
		public ChannelEventListener<IoSession> channelEventListener() {
			return new MinaChannelEventListener();
		}
	}

}
