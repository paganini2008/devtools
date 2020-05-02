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

	public ChannelEvent(Channel source, EventType eventType, MessagePacket messagePacket, Throwable cause) {
		super(source, null);
		this.eventType = eventType;
		this.messagePacket = messagePacket;
		this.cause = cause;
	}

	private final EventType eventType;
	private final MessagePacket messagePacket;
	private final Throwable cause;

	public MessagePacket getMessagePacket() {
		return messagePacket;
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
		ALL, ACTIVE, INACTIVE, READABLE, WRITEABLE, FATAL
	}

}
