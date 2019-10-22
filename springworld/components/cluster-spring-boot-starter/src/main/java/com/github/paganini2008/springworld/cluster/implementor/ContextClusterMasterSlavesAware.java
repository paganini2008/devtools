package com.github.paganini2008.springworld.cluster.implementor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.github.paganini2008.springworld.cluster.ContextClusterMasterStandbyEvent;
import com.github.paganini2008.springworld.cluster.ContextClusterSlaveStandbyEvent;
import com.github.paganini2008.springworld.cluster.Constants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * ContextClusterMasterSlavesAware
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
@Slf4j
public class ContextClusterMasterSlavesAware implements ApplicationListener<ContextRefreshedEvent> {

	@Value("${spring.application.name}")
	private String applicationName;

	@Autowired
	private InstanceId instanceId;

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private MasterSlavesHeartbeatTask heartbeatTask;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		final ApplicationContext context = event.getApplicationContext();
		String key = String.format(Constants.CLUSTER_KEY, applicationName);
		redisTemplate.opsForList().leftPush(key, instanceId.get());
		if (instanceId.get().equals(redisTemplate.opsForList().index(key, -1))) {
			heartbeatTask.start();
			context.publishEvent(new ContextClusterMasterStandbyEvent(context));
			log.info("Master of context cluster '{}' is you. You can also implement ApplicationListener to listen the event type {}",
					applicationName, ContextClusterMasterStandbyEvent.class.getName());
		} else {
			context.publishEvent(new ContextClusterSlaveStandbyEvent(context));
			log.info("Slave of context cluster '{}' is you. You can also implement ApplicationListener to listen the event type {}",
					applicationName, ContextClusterSlaveStandbyEvent.class.getName());
		}
	}

}
