package com.github.paganini2008.devtools.event;

import java.util.EventObject;

/**
 * 
 * Event
 * 
 * @author Jimmy Hoff
 * @since 1.0
 */
public abstract class Event<T> extends EventObject {

	private static final long serialVersionUID = 1L;

	public Event(Object source, T argument) {
		super(source);
		this.timestamp = System.currentTimeMillis();
		this.argument = argument;
	}

	private final long timestamp;
	private T argument;

	public T getArgument() {
		return argument;
	}

	public void setArgument(T argument) {
		this.argument = argument;
	}

	public long getTimestamp() {
		return timestamp;
	}

}
