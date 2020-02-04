package com.github.paganini2008.springworld.cluster;

import java.util.Timer;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * ContextClusterHeartbeatThread
 * 
 * @author Fred Feng
 * @revised 2019-08
 * @created 2019-08
 * @version 1.0
 */
@Slf4j
public class ContextClusterHeartbeatThread implements Executable {

	@Value("${spring.application.cluster.lifespanTtl:5}")
	private int lifespanTtl;

	@Autowired
	private ContextClusterConfigProperties configProperties;

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Override
	public boolean execute() {
		String key = configProperties.getApplicationClusterName();
		redisTemplate.expire(key, lifespanTtl, TimeUnit.SECONDS);
		return true;
	}

	private Timer timer;

	public void start() {
		String key = configProperties.getApplicationClusterName();
		redisTemplate.expire(key, lifespanTtl, TimeUnit.SECONDS);
		timer = ThreadUtils.scheduleAtFixedRate(this, 3, 3, TimeUnit.SECONDS);
		log.info("Start ContextClusterHeartbeatThread ok.");
	}

	public void stop() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

}
