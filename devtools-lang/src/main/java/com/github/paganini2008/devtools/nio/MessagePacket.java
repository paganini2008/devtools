package com.github.paganini2008.devtools.nio;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * MessagePacket
 *
 * @author Fred Feng
 * @since 1.0
 */
public class MessagePacket implements Serializable{

	private static final long serialVersionUID = -4067748468303232269L;
	private final List<Object> messages;
	private long length;

	MessagePacket(List<Object> messages, long length) {
		this.messages = messages;
		this.length = length;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public List<Object> getMessages() {
		return messages;
	}

	public static MessagePacket of(List<Object> messages, long length) {
		return new MessagePacket(messages, length);
	}

}
