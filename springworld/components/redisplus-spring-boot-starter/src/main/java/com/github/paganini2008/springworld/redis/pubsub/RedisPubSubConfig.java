package com.github.paganini2008.springworld.redis.pubsub;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * RedisPubSubConfig
 * 
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
@Configuration
@ConditionalOnBean(RedisConnectionFactory.class)
public class RedisPubSubConfig {

	@Value("${spring.redis.pubsub.channel:pubsub}")
	private String channel;

	@Bean
	@ConditionalOnMissingBean(RedisSerializer.class)
	public RedisSerializer<Object> redisSerializer() {
		Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		jackson2JsonRedisSerializer.setObjectMapper(om);
		return jackson2JsonRedisSerializer;
	}

	@Bean
	@ConditionalOnMissingBean(RedisTemplate.class)
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory,
			RedisSerializer<Object> redisSerializer) {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		StringRedisSerializer stringSerializer = new StringRedisSerializer();
		redisTemplate.setKeySerializer(stringSerializer);
		redisTemplate.setValueSerializer(redisSerializer);
		redisTemplate.setHashKeySerializer(stringSerializer);
		redisTemplate.setHashValueSerializer(redisSerializer);
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}

	@Bean
	@ConditionalOnMissingBean(StringRedisTemplate.class)
	public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		return new StringRedisTemplate(redisConnectionFactory);
	}

	@Bean
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
	public MessageListenerAdapter messageListenerAdapter(RedisSerializer<Object> redisSerializer) {
		MessageListenerAdapter adapter = new MessageListenerAdapter(redisMessageEventPublisher(), "publish");
		adapter.setSerializer(redisSerializer);
		adapter.afterPropertiesSet();
		return adapter;
	}

	@Bean
	public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory,
			MessageListenerAdapter messageListenerAdapter) {
		RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
		redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
		redisMessageListenerContainer.addMessageListener(messageListenerAdapter, new ChannelTopic(channel));
		return redisMessageListenerContainer;
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

	@Bean
	public RedisMessageHandlerBeanProcessor redisMessageHandlerBeanProcessor() {
		return new RedisMessageHandlerBeanProcessor();
	}

}
