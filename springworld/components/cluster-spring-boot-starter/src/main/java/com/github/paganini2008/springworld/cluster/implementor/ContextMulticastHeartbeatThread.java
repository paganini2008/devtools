package com.github.paganini2008.springworld.cluster.implementor;

import java.util.Timer;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;
import com.github.paganini2008.springworld.redisplus.RedisMessageSender;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * ContextMulticastHeartbeatThread
 * 
 * @author Fred Feng
 * @revised 2019-08
 * @created 2019-08
 * @version 1.0
 */
@Slf4j
public class ContextMulticastHeartbeatThread implements Executable {

	@Value("${spring.application.name}")
	private String applicationName;

	@Value("${spring.application.cluster.namespace:application:cluster:}")
	private String namespace;

	@Autowired
	private RedisMessageSender redisMessageSender;

	@Autowired
	private InstanceId instanceId;

	private Timer timer;

	private String channel;

	@Override
	public boolean execute() {
		redisMessageSender.sendEphemeralMessage(channel, instanceId.get(), 5, TimeUnit.SECONDS);
		return true;
	}

	public void start() {
		this.channel = namespace + applicationName + ":multicast:" + instanceId.get();
		redisMessageSender.sendEphemeralMessage(channel, instanceId.get(), 5, TimeUnit.SECONDS);
		timer = ThreadUtils.scheduleWithFixedDelay(this, 3, 3, TimeUnit.SECONDS);
		log.info("Start ContextMulticastHeartbeatTask ok.");
	}

	public void stop() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}
}
