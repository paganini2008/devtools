package com.github.paganini2008.springworld.redis.pubsub;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * RedisMessageEntity
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-03
 * @version 1.0
 */
@Getter
@Setter
public class RedisMessageEntity implements Serializable {

	private static final long serialVersionUID = -8864323748615322076L;
	private final long timestamp;
	private String channel;
	private Object message;

	public RedisMessageEntity() {
		this.timestamp = System.currentTimeMillis();
	}

	public RedisMessageEntity(String channel, Object message) {
		this();
		this.channel = channel;
		this.message = message;
	}

	public static RedisMessageEntity of(String channel, Object message) {
		return new RedisMessageEntity(channel, message);
	}

}
