package com.github.paganini2008.springworld.redis.pubsub;

import org.springframework.context.ApplicationEvent;

/**
 * 
 * RedisMessageEvent
 *
 * @author Fred Feng
 * @revised 2019-08
 * @created 2019-08
 * @version 1.0
 */
public class RedisMessageEvent extends ApplicationEvent {

	private static final long serialVersionUID = 5563838735572037403L;

	public RedisMessageEvent(String channel, Object message) {
		super(message);
		this.channel = channel;
	}

	private final String channel;

	public String getChannel() {
		return channel;
	}

	public Object getMessage() {
		return getSource();
	}

}
