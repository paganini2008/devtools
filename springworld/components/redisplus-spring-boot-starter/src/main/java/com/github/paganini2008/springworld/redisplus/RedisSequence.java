package com.github.paganini2008.springworld.redisplus;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import com.github.paganini2008.devtools.StringUtils;

/**
 * 
 * RedisSequence
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
public class RedisSequence {

	static {
		byte[] seed = new SecureRandom().generateSeed(8);
		timestampSeed = new BigInteger(seed).longValue();
	}

	private static final long timestampSeed;
	private final static long initalValue = 100000000000000000L;
	private RedisAtomicLong serial;

	@Value("${spring.application.name}")
	private String applicationName;

	@Value("${spring.application.id:}")
	private String clusterId;

	public RedisSequence(RedisTemplate<String, Long> redisTemplate) {
		final String key = "sequence:" + applicationName + (StringUtils.isNotBlank(clusterId) ? ":" + clusterId : "");
		serial = new RedisAtomicLong(key, redisTemplate, System.currentTimeMillis());
	}

	public void set(long value) {
		serial.set(value);
	}

	public long currentValue() {
		return serial.get();
	}

	public long nextValue() {
		while (true) {
			try {
				return serial.getAndIncrement();
			} catch (Exception ignored) {
			}
		}

	}

	public void destroy() {
		serial.expire(1, TimeUnit.SECONDS);
	}

	private static long getSerialNo() {
		return (System.currentTimeMillis() - timestampSeed) << 22;
	}
}
