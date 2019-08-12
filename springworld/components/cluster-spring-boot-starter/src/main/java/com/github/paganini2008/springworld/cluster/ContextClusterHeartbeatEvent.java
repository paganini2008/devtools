package com.github.paganini2008.springworld.cluster;

import org.springframework.context.ApplicationContext;

/**
 * 
 * ContextClusterHeartbeatEvent
 * 
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
public class ContextClusterHeartbeatEvent extends ContextClusterEvent {

	private static final long serialVersionUID = 4818738773395495135L;

	public ContextClusterHeartbeatEvent(ApplicationContext context, long ticket) {
		super(context);
		this.ticket = ticket;
	}

	private final long ticket;

	public long getTicket() {
		return ticket;
	}

}
