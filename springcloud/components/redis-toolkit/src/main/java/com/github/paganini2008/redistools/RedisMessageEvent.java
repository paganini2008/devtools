package com.allyes.mec.common.redis;

import org.springframework.context.ApplicationEvent;

/**
 * 
 * RedisMessageEvent
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
public class RedisMessageEvent extends ApplicationEvent {

	private static final long serialVersionUID = -440558170671797251L;

	public RedisMessageEvent(String channel, String message) {
		super(message);
		this.channel = channel;
	}

	private final String channel;

	public String getChannel() {
		return channel;
	}

	public String getMessage() {
		return (String) super.getSource();
	}

}
