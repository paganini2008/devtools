package com.github.paganini2008.springworld.decentration.implementor;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;

/**
 * 
 * ContextHeartbeatEvent
 * 
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
public class ContextHeartbeatEvent extends ApplicationContextEvent {

	private static final long serialVersionUID = 4818738773395495135L;

	public ContextHeartbeatEvent(ApplicationContext context, long ticket) {
		super(context);
		this.ticket = ticket;
	}

	private final long ticket;

	public long getTicket() {
		return ticket;
	}

}
