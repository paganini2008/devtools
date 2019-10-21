package com.github.paganini2008.springworld.cluster.implementor;

import java.util.Timer;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;
import com.github.paganini2008.springworld.cluster.KeyPatterns;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * MasterSlavesHeartbeatTask
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
@Slf4j
public class MasterSlavesHeartbeatTask implements Executable {

	@Value("${spring.application.name}")
	private String applicationName;

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Override
	public boolean execute() {
		String key = String.format(KeyPatterns.CLUSTER_KEY, applicationName);
		redisTemplate.expire(key, 5, TimeUnit.SECONDS);
		return true;
	}

	private Timer worker;

	public void start() {
		worker = ThreadUtils.scheduleAtFixedRate(this, 3, 3, TimeUnit.SECONDS);
		log.info("Start MasterSlavesHeartbeatTask ok.");
	}

	public void stop() {
		if (worker != null) {
			worker.cancel();
			worker = null;
		}
	}

}
