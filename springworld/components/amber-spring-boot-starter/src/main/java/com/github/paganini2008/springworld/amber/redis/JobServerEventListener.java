package com.github.paganini2008.springworld.amber.redis;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import com.github.paganini2008.springworld.amber.utils.JsonUtils;

/**
 * 
 * JobServerEventListener
 * 
 * @author Fred Feng
 * @created 2018-03
 */
@Configuration
@ConditionalOnBean(RedisDispatcher.class)
public class JobServerEventListener implements ApplicationContextAware {

	private ApplicationContext ctx;

	@Value("${amber.redis.channel}")
	private String channel;

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.ctx = applicationContext;
	}

	public void publish(String json) {
		JobServerEvent serverEvent = JsonUtils.readObject(json, JobServerEvent.class);
		ctx.publishEvent(serverEvent);
	}

	@Bean
	public MessageListenerAdapter getMessageListenerAdapter(JobServerEventListener eventListener) {
		return new MessageListenerAdapter(eventListener, "publish");
	}

	@Bean
	public RedisMessageListenerContainer getRedisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory,
			MessageListenerAdapter messageListenerAdapter) {
		RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
		redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
		redisMessageListenerContainer.addMessageListener(messageListenerAdapter, new ChannelTopic(channel));
		return redisMessageListenerContainer;
	}

	@Bean
	public KeyExpirationEventMessageListener getKeyExpirationEventMessageListener(
			RedisMessageListenerContainer redisMessageListenerContainer) {
		KeyExpirationEventMessageListener listener = new KeyExpirationEventMessageListener(redisMessageListenerContainer);
		listener.setKeyspaceNotificationsConfigParameter("Ex");
		return listener;
	}

	@Bean
	public RedisKeyExpiredListener getRedisKeyExpiredListener() {
		return new RedisKeyExpiredListener();
	}

}
