package com.github.paganini2008.springworld.cluster;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.data.redis.core.StringRedisTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * ContextMasterBreakdownListener
 * 
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
@Slf4j
@SuppressWarnings("all")
public class ContextMasterBreakdownListener implements ApplicationListener<RedisKeyExpiredEvent>, ApplicationContextAware {

	@Autowired
	private ClusterId clusterId;

	@Autowired
	private ContextClusterConfigProperties configProperties;

	@Autowired
	private ContextClusterHeartbeatThread heartbeatThread;

	@Autowired
	private StringRedisTemplate redisTemplate;

	private ApplicationContext context;

	@Override
	public void onApplicationEvent(RedisKeyExpiredEvent event) {
		final String expiredKey = new String(event.getSource());
		final String heartbeatKey = configProperties.getApplicationClusterName();
		if (heartbeatKey.equals(expiredKey)) {
			log.info("The master of application '" + configProperties.getApplicationName() + "' is breakdown.");
			final String key = heartbeatKey;
			redisTemplate.opsForList().leftPush(key, clusterId.get());
			if (clusterId.get().equals(redisTemplate.opsForList().index(key, -1))) {
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

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}

}
