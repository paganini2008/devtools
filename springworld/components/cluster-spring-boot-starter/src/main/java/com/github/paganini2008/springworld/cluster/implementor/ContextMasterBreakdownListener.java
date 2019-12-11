package com.github.paganini2008.springworld.cluster.implementor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.github.paganini2008.springworld.cluster.Constants;
import com.github.paganini2008.springworld.cluster.ContextMasterStandbyEvent;
import com.github.paganini2008.springworld.cluster.ContextSlaveStandbyEvent;

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
	private InstanceId instanceId;

	@Autowired
	private ContextClusterHeartbeatTask heartbeatTask;

	@Autowired
	private StringRedisTemplate redisTemplate;

	private ApplicationContext context;

	@Value("${spring.application.name}")
	private String applicationName;

	@Override
	public void onApplicationEvent(RedisKeyExpiredEvent event) {
		final String expiredKey = new String(event.getSource());
		final String heartbeatKey = String.format(Constants.CLUSTER_KEY, applicationName);
		if (heartbeatKey.equals(expiredKey)) {
			log.info("The master of application '" + applicationName + "' is offline.");
			final String key = String.format(Constants.CLUSTER_KEY, applicationName);
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

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}

}
