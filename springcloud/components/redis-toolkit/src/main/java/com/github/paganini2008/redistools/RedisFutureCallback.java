package com.github.paganini2008.redistools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.allyes.mec.common.JsonUtils;
import com.allyes.mec.common.multithreads.FutureCallback;

/**
 * 
 * RedisFutureCallback
 * 
 * @author Fred Feng
 * @created 2019-06
 * @version 2.0.0
 */
public class RedisFutureCallback implements FutureCallback<Object> {

	private static final Logger logger = LoggerFactory.getLogger(RedisFutureCallback.class);

	private final String channel;

	public RedisFutureCallback(String channel) {
		this.channel = channel;
	}

	@Autowired
	private RedisMessageSender messageSender;

	public void onSuccess(Object result) {
		String message = JsonUtils.parseObject(result);
		messageSender.sendMessage(channel, message);
	}

	public void onFailure(Throwable e) {
		logger.error(e.getMessage(), e);
	}

}
