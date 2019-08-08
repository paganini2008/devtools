package com.github.paganini2008.springworld.amber.redis;

import org.springframework.context.ApplicationEvent;

/**
 * 
 * JobServerEvent
 * 
 * @author Fred Feng
 * @created 2018-03
 */
public class JobServerEvent extends ApplicationEvent {

	private static final long serialVersionUID = 5219353875930477872L;
	private final EventType eventType;
	private final String eventId;

	public JobServerEvent(String eventId, EventType eventType) {
		super(eventId);
		this.eventId = eventId;
		this.eventType = eventType;
	}

	public EventType getEventType() {
		return eventType;
	}

	public String getEventId() {
		return eventId;
	}

	public String toString() {
		return "[JobServerEvent] eventId: " + getEventId() + ", eventType: " + eventType;
	}

}
