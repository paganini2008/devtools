package com.github.paganini2008.springworld.cluster.implementor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import com.github.paganini2008.springworld.cluster.ContextClusterHeartbeatEvent;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * ContextClusterHeartbeatAware
 *
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
@Slf4j
public class ContextClusterAware implements ApplicationListener<ContextRefreshedEvent> {

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
		final ApplicationContext context = event.getApplicationContext();
		if (ticket == serialNumber.get()) {
			context.publishEvent(new ContextClusterHeartbeatEvent(context, ticket));
			log.info("Current context is the cluster leader. Context: " + context);
		}
	}

}