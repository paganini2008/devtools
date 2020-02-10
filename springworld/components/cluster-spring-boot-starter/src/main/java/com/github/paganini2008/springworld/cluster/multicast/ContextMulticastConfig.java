package com.github.paganini2008.springworld.cluster.multicast;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.github.paganini2008.springworld.cluster.ContextClusterConfig;
import com.github.paganini2008.springworld.redis.pubsub.RedisMessageHandler;

/**
 * 
 * ContextMulticastConfig
 *
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
@Order(Ordered.LOWEST_PRECEDENCE - 200)
@Configuration
@ConditionalOnBean(ContextClusterConfig.class)
@ConditionalOnProperty(value = "spring.application.cluster.multicast.enabled", havingValue = "true")
public class ContextMulticastConfig {

	@Bean
	public ContextMulticastAware multicastAware() {
		return new ContextMulticastAware();
	}

	@Bean
	public RedisMessageHandler standbyEventProcessor() {
		return new StandbyEventProcessor();
	}

	@Bean
	public RedisMessageHandler breakdownEventProcessor() {
		return new BreakdownEventProcessor();
	}

	@Bean
	public RedisMessageHandler multicastEventProcessor() {
		return new MulticastEventProcessor();
	}

	@Bean
	public ContextMulticastGroup multicastGroup() {
		return new ContextMulticastGroup();
	}

	@Bean
	@ConditionalOnMissingBean(LoadBalance.class)
	public LoadBalance loadBalance() {
		return new LoadBalanceSelector.RoundrobinLoadBalance();
	}

	@Bean(name = "multicastHeartbeatThread", initMethod = "start", destroyMethod = "stop")
	public ContextMulticastHeartbeatThread heartbeatThread() {
		return new ContextMulticastHeartbeatThread();
	}

	@Bean
	public ContextMulticastEventListener multicastEventListener() {
		return new ContextMulticastEventListener();
	}

	@Bean
	public ContextMulticastEventHandler loggingContextMulticastEventHandler() {
		return new LoggingContextMulticastEventHandler();
	}

}
