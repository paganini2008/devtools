package com.github.paganini2008.devtools.event;

import java.util.EventObject;

/**
 * 
 * Event
 * 
 * @author Fred Feng
 * 
 * @version 1.0
 */
public abstract class Event<T> extends EventObject {

	private static final long serialVersionUID = 1369049759719737665L;

	public Event(Object source, T argument) {
		super(source);
		this.timestamp = System.currentTimeMillis();
		this.argument = argument;
	}

	private final long timestamp;
	private final T argument;

	public T getArgument() {
		return argument;
	}

	public long getTimestamp() {
		return timestamp;
	}

}
