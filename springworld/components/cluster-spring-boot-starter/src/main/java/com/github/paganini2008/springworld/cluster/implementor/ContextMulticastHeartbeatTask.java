package com.github.paganini2008.springworld.cluster.implementor;

import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;
import com.github.paganini2008.springworld.cluster.Constants;
import com.github.paganini2008.springworld.redisplus.RedisMessageSender;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * ContextMulticastHeartbeatTask
 * 
 * @author Fred Feng
 * @revised 2019-08
 * @created 2019-08
 * @version 1.0
 */
@Slf4j
public class ContextMulticastHeartbeatTask implements Executable {

	@Value("${spring.application.name}")
	private String applicationName;

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
		this.channel = String.format(Constants.CLUSTER_MULTICAST_KEY, applicationName, UUID.randomUUID().toString());
		redisMessageSender.sendEphemeralMessage(channel, instanceId.get(), 5, TimeUnit.SECONDS);
		timer = ThreadUtils.scheduleWithFixedDelay(this, 3, 3, TimeUnit.SECONDS);
		log.info("Start MulticastHeartbeatTask ok.");
	}

	public void stop() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}
}
