package com.github.paganini2008.springworld.decentration;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * HeartbeatAware
 * 
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
@Slf4j
public class HeartbeatAware implements ApplicationContextAware {

	public void setApplicationContext(ApplicationContext context) throws BeansException {
		RedisAtomicLong ticket = context.getBean("ticket", RedisAtomicLong.class);
		RedisAtomicLong serial = context.getBean("serial", RedisAtomicLong.class);
		final long ticketValue = ticket.getAndIncrement();
		if (ticketValue == serial.get()) {
			context.publishEvent(new HeartbeatEvent(ticketValue));
			serial.incrementAndGet();
			log.info("Current ticket is {}.", ticketValue);
		}
	}

}
