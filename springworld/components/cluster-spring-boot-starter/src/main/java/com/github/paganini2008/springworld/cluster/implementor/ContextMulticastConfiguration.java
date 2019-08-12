package com.github.paganini2008.springworld.cluster.implementor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * 
 * ContextMulticastConfiguration
 *
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
@Configuration
@ConditionalOnBean(ContextClusterConfiguration.class)
@ConditionalOnProperty(value = "spring.cluster.configuration.multicast.enabled", havingValue = "true")
public class ContextMulticastConfiguration {

	@Value("${spring.redis.pubsub.channel:defaultChannel}")
	private String channel;

	@Bean
	public RedisMessageListener redisMessageListener() {
		return new RedisMessageListener();
	}

	@Bean
	public RedisEphemeralMessageListener redisEphemeralMessageListener() {
		return new RedisEphemeralMessageListener();
	}

	@Bean
	public RedisMessagePubSub redisMessagePubSub() {
		return new RedisMessagePubSub();
	}

	@Bean("redisMessageEventPublisher")
	public RedisMessageEventPublisher redisMessageEventPublisher() {
		return new RedisMessageEventPublisher();
	}

	@DependsOn("redisMessageEventPublisher")
	@Bean
	public MessageListenerAdapter messageListenerAdapter(RedisMessageEventPublisher listenerDelegate) {
		return new MessageListenerAdapter(listenerDelegate, "publish");
	}

	@Bean
	public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory,
			MessageListenerAdapter messageListenerAdapter) {
		RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
		redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
		redisMessageListenerContainer.addMessageListener(messageListenerAdapter, new ChannelTopic(channel));
		return redisMessageListenerContainer;
	}

	@Bean
	public ContextMulticastAware contextMulticastAware() {
		return new ContextMulticastAware();
	}

	@Bean
	public ContextMulticastChannelGroup contextMulticastChannelGroup() {
		return new ContextMulticastChannelGroup();
	}

	@Bean
	@ConditionalOnMissingBean(LoadBalance.class)
	public LoadBalance loadBalance() {
		return new LoadBalanceSelector.RoundrobinLoadBalance();
	}

}
