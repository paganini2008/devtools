package com.github.paganini2008.springworld.cluster.multicast;

import java.util.Timer;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;
import com.github.paganini2008.springworld.cluster.ClusterId;
import com.github.paganini2008.springworld.cluster.ContextClusterConfigProperties;
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

	static final int DEFAULT_LIFESPAN_TTL = 5;

	@Autowired
	private ContextClusterConfigProperties configProperties;

	@Autowired
	private RedisMessageSender redisMessageSender;

	@Autowired
	private ClusterId clusterId;

	private Timer timer;

	private String channel;

	@Override
	public boolean execute() {
		redisMessageSender.sendEphemeralMessage(channel, clusterId.get(), DEFAULT_LIFESPAN_TTL, TimeUnit.SECONDS);
		return true;
	}

	public void start() {
		this.channel = configProperties.getApplicationClusterName() + ":" + clusterId.get();
		redisMessageSender.sendEphemeralMessage(channel, clusterId.get(), DEFAULT_LIFESPAN_TTL, TimeUnit.SECONDS);
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
