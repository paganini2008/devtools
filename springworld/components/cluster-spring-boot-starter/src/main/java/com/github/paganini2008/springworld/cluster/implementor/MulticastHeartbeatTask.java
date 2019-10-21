package com.github.paganini2008.springworld.cluster.implementor;

import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;
import com.github.paganini2008.springworld.cluster.KeyPatterns;

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

	@Autowired
	private RedisMessagePubSub redisMessager;

	@Autowired
	private InstanceId instanceId;

	private Timer timer;

	private final String channel;

	public MulticastHeartbeatTask() {
		this.channel = String.format(KeyPatterns.CLUSTER_MULTICAST_KEY, UUID.randomUUID().toString());
	}

	@Override
	public boolean execute() {
		redisMessager.sendEphemeralMessage(channel, instanceId.get(), 5, TimeUnit.SECONDS);
		return true;
	}

	public void start() {
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
