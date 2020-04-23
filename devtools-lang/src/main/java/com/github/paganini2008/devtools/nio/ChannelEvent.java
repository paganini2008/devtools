package com.github.paganini2008.devtools.nio;

import java.util.List;

import com.github.paganini2008.devtools.event.Event;

/**
 * 
 * ChannelEvent
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class ChannelEvent extends Event<Object> {

	private static final long serialVersionUID = 5631405024269581391L;

	public ChannelEvent(Reactor source, Channel channel, EventType eventType, List<Object> messages, Throwable cause) {
		super(source, null);
		this.channel = channel;
		this.eventType = eventType;
		this.messages = messages;
		this.cause = cause;
	}

	private final Channel channel;
	private final EventType eventType;
	private final List<Object> messages;
	private final Throwable cause;

	public List<Object> getMessages() {
		return messages;
	}

	public EventType getEventType() {
		return eventType;
	}

	public Throwable getCause() {
		return cause;
	}

	public Channel getChannel() {
		return channel;
	}

	public static enum EventType {
		ALL, ACTIVE, INACTIVE, READABLE, FATAL
	}

}
