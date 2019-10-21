package com.github.paganini2008.springworld.cluster.implementor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.github.paganini2008.springworld.cluster.redis.RedisMessagePubSub;

/**
 * 
 * ContextClusterMulticastAware
 *
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
public class ContextClusterMulticastAware implements ApplicationListener<ContextRefreshedEvent> {

	@Value("${spring.application.name}")
	private String applicationName;

	@Autowired
	private RedisMessagePubSub redisMessager;
	
	@Autowired
	private InstanceId instanceId;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		redisMessager.subcribeChannel("standby", new StandbyMessageHandler());
		redisMessager.subcribeChannel(instanceId.get(), new MulticastMessageHandler());
		redisMessager.subcribeEphemeralChannel(new DeactiveMessageHandler());

		redisMessager.sendMessage("standby", instanceId.get());

	}

}
