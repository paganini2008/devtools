package com.github.paganini2008.springworld.cluster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.redis.core.StringRedisTemplate;

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

	@Autowired
	private ClusterId clusterId;

	@Autowired
	private ContextClusterConfigProperties configProperties;

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private ContextClusterHeartbeatThread heartbeatThread;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		final String key = configProperties.getApplicationClusterName();
		if (redisTemplate.hasKey(key) && redisTemplate.getExpire(key) < 0) {
			redisTemplate.delete(key);
		}

		final ApplicationContext context = event.getApplicationContext();
		final String id = clusterId.get();
		redisTemplate.opsForList().leftPush(key, id);
		if (id.equals(redisTemplate.opsForList().index(key, -1))) {
			clusterId.setMaster(true);
			heartbeatThread.start();
			context.publishEvent(new ContextMasterStandbyEvent(context));
			log.info("Master of context cluster '{}' is you. You can also implement ApplicationListener to listen the event type {}",
					configProperties.getApplicationName(), ContextMasterStandbyEvent.class.getName());
		} else {
			context.publishEvent(new ContextSlaveStandbyEvent(context));
			log.info("Slave of context cluster '{}' is you. You can also implement ApplicationListener to listen the event type {}",
					configProperties.getApplicationName(), ContextSlaveStandbyEvent.class.getName());
		}
	}

}
