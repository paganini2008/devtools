package com.github.paganini2008.springworld.decentration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * ContextHeartbeatAware
 *
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
@Slf4j
public class ContextHeartbeatAware implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	@Qualifier("serial")
	private RedisAtomicLong serialNumber;

	@Autowired
	@Qualifier("ticket")
	private RedisAtomicLong ticketNumber;

	private long ticket;

	public long getTicket() {
		return ticket;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ticket = ticketNumber.getAndIncrement();
		if (ticket == serialNumber.get()) {
			final ApplicationContext context = event.getApplicationContext();
			context.publishEvent(new ContextHeartbeatEvent(context, ticket));
			serialNumber.incrementAndGet();
			log.info("Current context is the leader. Context: " + context); 
		}
	}

}
