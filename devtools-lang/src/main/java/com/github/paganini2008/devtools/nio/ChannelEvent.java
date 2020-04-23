package com.github.paganini2008.devtools.nio;

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

	public ChannelEvent(Channel source, EventType eventType) {
		this(source, eventType, null, null);
	}

	public ChannelEvent(Channel source, EventType eventType, MessagePacket packet, Throwable cause) {
		super(source, null);
		this.eventType = eventType;
		this.packet = packet;
		this.cause = cause;
	}

	private final EventType eventType;
	private final MessagePacket packet;
	private final Throwable cause;

	public MessagePacket getMessagePacket() {
		return packet;
	}

	public EventType getEventType() {
		return eventType;
	}

	public Throwable getCause() {
		return cause;
	}

	public Channel getChannel() {
		return (Channel) getSource();
	}

	public static enum EventType {
		ALL, ACTIVE, INACTIVE, READABLE, FATAL
	}

}
