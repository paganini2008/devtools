package com.github.paganini2008.springworld.cluster.implementor;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * MessageEntity
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-03
 * @version 1.0
 */
@Getter
@Setter
public class MessageEntity implements Serializable {

	private static final long serialVersionUID = -8864323748615322076L;
	private final long timestamp;
	private String channel;
	private Object message;

	public MessageEntity() {
		this.timestamp = System.currentTimeMillis();
	}

	public MessageEntity(String channel, Object message) {
		this.channel = channel;
		this.message = message;
		this.timestamp = System.currentTimeMillis();
	}

	public static MessageEntity of(String channel, Object message) {
		return new MessageEntity(channel, message);
	}

}
