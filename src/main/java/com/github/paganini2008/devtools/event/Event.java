package com.github.paganini2008.devtools.event;

import java.util.EventObject;

/**
 * 
 * Event
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
public abstract class Event extends EventObject {

	private static final long serialVersionUID = 1369049759719737665L;

	public Event(Object source) {
		super(source);
		this.timestamp = System.currentTimeMillis();
	}

	private final long timestamp;

	public long getTimestamp() {
		return timestamp;
	}

}
