package com.github.paganini2008.springworld.cluster.implementor;

import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;
import com.github.paganini2008.springworld.cluster.KeyPatterns;
import com.github.paganini2008.springworld.cluster.redis.RedisMessagePubSub;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * MulticastHeartbeatTask
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
@Slf4j
public class MulticastHeartbeatTask implements Executable {

	@Value("${spring.application.name}")
	private String applicationName;

	@Autowired
	private RedisMessagePubSub redisMessager;

	@Autowired
	private InstanceId instanceId;

	private Timer timer;

	private String channel;

	@Override
	public boolean execute() {
		redisMessager.sendEphemeralMessage(channel, instanceId.get(), 5, TimeUnit.SECONDS);
		return true;
	}

	public void start() {
		this.channel = String.format(KeyPatterns.CLUSTER_MULTICAST_KEY, applicationName, UUID.randomUUID().toString());
		redisMessager.sendEphemeralMessage(channel, instanceId.get(), 5, TimeUnit.SECONDS);
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
