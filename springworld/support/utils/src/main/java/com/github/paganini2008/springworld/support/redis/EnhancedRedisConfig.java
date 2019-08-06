package com.github.paganini2008.springworld.support.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * 
 * EnhancedRedisConfig
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
@ConditionalOnBean(RedisConnectionFactory.class)
public class EnhancedRedisConfig {

	@Value("${redis.pubsub.channel:defaultChannel}")
	private String channel;

	@Bean
	public RedisMessageListener redisMessageListener() {
		return new RedisMessageListener();
	}

	@Bean("redisMessageEventPublisher")
	public RedisMessageEventPublisher redisMessageEventPublisher() {
		return new RedisMessageEventPublisher();
	}

	@DependsOn("redisMessageEventPublisher")
	@Bean
	public MessageListenerAdapter getMessageListenerAdapter(RedisMessageEventPublisher listenerDelegate) {
		return new MessageListenerAdapter(listenerDelegate, "publish");
	}

	@Bean
	public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory,
			MessageListenerAdapter messageListenerAdapter) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(redisConnectionFactory);
		container.addMessageListener(messageListenerAdapter, new ChannelTopic(channel));
		return container;
	}

	@Bean
	public KeyExpirationEventMessageListener keyExpirationEventMessageListener(
			RedisMessageListenerContainer redisMessageListenerContainer) {
		KeyExpirationEventMessageListener listener = new KeyExpirationEventMessageListener(redisMessageListenerContainer);
		listener.setKeyspaceNotificationsConfigParameter("Ex");
		return listener;
	}

	@Bean
	public RedisKeyExpiredEventListener redisKeyExpiredListener() {
		return new RedisKeyExpiredEventListener();
	}

	@Bean
	public RedisMessageSender redisMessageSender() {
		return new RedisMessageSender();
	}

}
