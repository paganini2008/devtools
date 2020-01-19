package com.github.paganini2008.springworld.webcrawler.utils;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

/**
 * 
 * RedisUUID
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
public class RedisUUID {

	static {
		byte[] seed = new SecureRandom().generateSeed(8);
		leastSigBits = new BigInteger(seed).longValue();
	}

	private static final long leastSigBits;
	private final RedisAtomicLong timestamp;

	public RedisUUID(String key, RedisTemplate<String, Long> redisTemplate) {
		this.timestamp = new RedisAtomicLong(key, redisTemplate);
	}

	public UUID createTimeBasedUUID() {
		long timeMillis = (System.currentTimeMillis() * 10000) + 0x01B21DD213814000L;

		if (timeMillis > timestamp.get()) {
			timestamp.getAndSet(timeMillis);
		} else {
			timeMillis = timestamp.incrementAndGet();
		}

		long mostSigBits = timeMillis << 32;
		mostSigBits |= (timeMillis & 0xFFFF00000000L) >> 16;
		mostSigBits |= 0x1000 | ((timeMillis >> 48) & 0x0FFF);
		return new UUID(mostSigBits, leastSigBits);
	}
}
