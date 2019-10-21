package com.github.paganini2008.springworld.cluster.implementor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * 
 * ContextClusterMulticastConfiguration
 *
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
@Order(200)
@Configuration
@ConditionalOnProperty(value = "spring.cluster.multicast.enabled", havingValue = "true")
public class ContextClusterMulticastConfiguration {

	@Value("${spring.redis.pubsub.channel:multicastChannel}")
	private String channel;

	@Bean("cluster-message-listener")
	public RedisMessageListener redisMessageListener() {
		return new RedisMessageListener();
	}

	@Bean("cluster-ephemeral-message-listener")
	public RedisEphemeralMessageListener redisEphemeralMessageListener() {
		return new RedisEphemeralMessageListener();
	}

	@Bean("cluster-message-pubsub")
	public RedisMessagePubSub redisMessagePubSub() {
		return new RedisMessagePubSub();
	}

	@Bean("cluster-message-event-publisher")
	public RedisMessageEventPublisher redisMessageEventPublisher() {
		return new RedisMessageEventPublisher();
	}

	@DependsOn("cluster-message-event-publisher")
	@Bean
	public MessageListenerAdapter messageListenerAdapter(RedisMessageListenerContainer messageListenerContainer) {
		MessageListenerAdapter adapter = new MessageListenerAdapter(redisMessageEventPublisher(), "publish");
		messageListenerContainer.addMessageListener(adapter, new ChannelTopic(channel));
		return adapter;
	}

	@Bean
	public ContextClusterMulticastAware multicastAware() {
		return new ContextClusterMulticastAware();
	}

	@Bean
	public ContextClusterMulticastChannelGroup multicastChannelGroup() {
		return new ContextClusterMulticastChannelGroup();
	}

	@Bean
	@ConditionalOnMissingBean(LoadBalance.class)
	public LoadBalance loadBalance() {
		return new LoadBalanceSelector.RoundrobinLoadBalance();
	}

	@Bean(name = "multicastHeartbeatTask", initMethod = "start", destroyMethod = "stop")
	public MulticastHeartbeatTask heartbeatTask() {
		return new MulticastHeartbeatTask();
	}

}
