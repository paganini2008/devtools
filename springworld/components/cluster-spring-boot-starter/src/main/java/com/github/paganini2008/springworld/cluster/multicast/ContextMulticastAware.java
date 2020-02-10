package com.github.paganini2008.springworld.cluster.multicast;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.github.paganini2008.springworld.cluster.ContextClusterConfigProperties;
import com.github.paganini2008.springworld.redis.pubsub.RedisMessageSender;
import com.github.paganini2008.springworld.cluster.ClusterId;

/**
 * 
 * ContextMulticastAware
 *
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
public class ContextMulticastAware implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private RedisMessageSender redisMessageSender;

	@Autowired
	private ClusterId clusterId;

	@Autowired
	private ContextClusterConfigProperties configProperties;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		redisMessageSender.sendMessage(ContextMulticastEventNames.STANDBY, clusterId.get() + ":" + configProperties.getWeight());
	}

}
