package com.github.paganini2008.springworld.cluster;

import java.util.Timer;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

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

	private static final int MIN_LIFESPAN_TTL = 5;
	private static final int MAX_LIFESPAN_TTL = 15;

	@Value("${spring.application.cluster.lifespanTtl:5}")
	private int lifespanTtl;

	@Autowired
	private ContextClusterConfigProperties configProperties;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Override
	public boolean execute() {
		String key = configProperties.getApplicationClusterName();
		redisTemplate.expire(key, lifespanTtl, TimeUnit.SECONDS);
		return true;
	}

	private Timer timer;

	public void start() {
		if (lifespanTtl < MIN_LIFESPAN_TTL || lifespanTtl > MAX_LIFESPAN_TTL) {
			throw new IllegalArgumentException("The value range of parameter 'spring.application.cluster.lifespanTtl' is between "
					+ MIN_LIFESPAN_TTL + " and " + MAX_LIFESPAN_TTL);
		}
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
