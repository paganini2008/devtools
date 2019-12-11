package com.github.paganini2008.springworld.redisplus;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * 
 * RedisMessageConfig
 * 
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
@Configuration
@ConditionalOnBean(RedisConnectionFactory.class)
public class RedisMessageConfig {

	@Value("${spring.redis.pubsub.channel:pubsub}")
	private String channel;

	@Bean
	@ConditionalOnMissingBean(StringRedisTemplate.class)
	public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		return new StringRedisTemplate(redisConnectionFactory);
	}

	@Bean
	@ConditionalOnMissingBean(RedisMessageListenerContainer.class)
	public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory) {
		RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
		redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
		return redisMessageListenerContainer;
	}

	@Bean
	@ConditionalOnMissingBean(KeyExpirationEventMessageListener.class)
	public KeyExpirationEventMessageListener keyExpirationEventMessageListener(
			RedisMessageListenerContainer redisMessageListenerContainer) {
		KeyExpirationEventMessageListener listener = new KeyExpirationEventMessageListener(redisMessageListenerContainer);
		listener.setKeyspaceNotificationsConfigParameter("Ex");
		return listener;
	}

	@Bean("redis-message-event-publisher")
	public RedisMessageEventPublisher redisMessageEventPublisher() {
		return new RedisMessageEventPublisher();
	}

	@DependsOn("redis-message-event-publisher")
	@Bean
	public MessageListenerAdapter messageListenerAdapter(RedisMessageListenerContainer messageListenerContainer) {
		MessageListenerAdapter adapter = new MessageListenerAdapter(redisMessageEventPublisher(), "publish");
		messageListenerContainer.addMessageListener(adapter, new ChannelTopic(channel));
		return adapter;
	}

	@Bean("redis-message-listener")
	public RedisMessageListener redisMessageListener() {
		return new RedisMessageListener();
	}

	@Bean("redis-ephemeral-message-listener")
	public RedisEphemeralMessageListener redisEphemeralMessageListener() {
		return new RedisEphemeralMessageListener();
	}

	@Bean("redis-message-sender")
	public RedisMessageSender redisMessageSender() {
		return new RedisMessageSender();
	}

}
