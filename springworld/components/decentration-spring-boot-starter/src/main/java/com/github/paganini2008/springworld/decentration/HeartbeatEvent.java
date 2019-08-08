package com.github.paganini2008.springworld.decentration;

import org.springframework.context.ApplicationEvent;

/**
 * 
 * HeartbeatEvent
 * 
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
public class HeartbeatEvent extends ApplicationEvent {

	private static final long serialVersionUID = 4818738773395495135L;

	public HeartbeatEvent(long ticket) {
		super(ticket);
	}

	public long getTicket() {
		return (Long) getSource();
	}

}
