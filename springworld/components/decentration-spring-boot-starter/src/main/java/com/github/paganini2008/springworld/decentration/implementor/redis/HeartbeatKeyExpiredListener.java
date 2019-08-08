package com.github.paganini2008.springworld.decentration.implementor.redis;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import com.github.paganini2008.springworld.decentration.implementor.ContextHeartbeatEvent;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * HeartbeatKeyExpiredListener
 * 
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
@Slf4j
@SuppressWarnings("all")
public class HeartbeatKeyExpiredListener implements ApplicationListener<RedisKeyExpiredEvent>, ApplicationContextAware {

	@Autowired
	private ContextHeartbeatAware contextHeartbeatAware;

	@Autowired
	@Qualifier("serial")
	private RedisAtomicLong serialNumber;

	private ApplicationContext context;

	@Value("${spring.application.name}")
	private String applicationName;

	@Override
	public void onApplicationEvent(RedisKeyExpiredEvent event) {
		final String expiredKey = new String(event.getSource());
		final String heartbeatKey = ContextHeartbeatEventListener.HEART_BEAT_KEY + applicationName;
		if (heartbeatKey.equals(expiredKey)) {
			log.info("One of applications named '" + applicationName + "' is shutdown.");
			if (contextHeartbeatAware.getTicket() == serialNumber.get() + 1) {
				serialNumber.incrementAndGet();
				context.publishEvent(new ContextHeartbeatEvent(context, contextHeartbeatAware.getTicket()));
				log.info("Current context is the cluster leader. Context: " + context);
			}
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}

}
