package com.github.paganini2008.springworld.cluster.implementor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.github.paganini2008.springworld.cluster.ContextMasterStandbyEvent;
import com.github.paganini2008.springworld.cluster.ContextSlaveStandbyEvent;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * ContextClusterAware
 * 
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
@Slf4j
public class ContextClusterAware implements ApplicationListener<ContextRefreshedEvent> {

	@Value("${spring.application.name}")
	private String applicationName;

	@Value("${spring.application.cluster.namespace:application:cluster:}")
	private String namespace;

	@Autowired
	private InstanceId instanceId;

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private ContextClusterHeartbeatThread heartbeatTask;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		final ApplicationContext context = event.getApplicationContext();
		final String key = namespace + applicationName;
		redisTemplate.opsForList().leftPush(key, instanceId.get());
		if (instanceId.get().equals(redisTemplate.opsForList().index(key, -1))) {
			instanceId.setMaster(true);
			heartbeatTask.start();
			context.publishEvent(new ContextMasterStandbyEvent(context));
			log.info("Master of context cluster '{}' is you. You can also implement ApplicationListener to listen the event type {}",
					applicationName, ContextMasterStandbyEvent.class.getName());
		} else {
			context.publishEvent(new ContextSlaveStandbyEvent(context));
			log.info("Slave of context cluster '{}' is you. You can also implement ApplicationListener to listen the event type {}",
					applicationName, ContextSlaveStandbyEvent.class.getName());
		}
	}

}
