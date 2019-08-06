package com.github.paganini2008.springworld.support.redis;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import com.github.paganini2008.devtools.multithreads.ThreadUtils;
import com.github.paganini2008.springworld.support.IdGenerator;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * RedisIdGenerator
 * 
 * @author Fred Feng
 * @created 2019-06
 * @version 2.0.0
 */
@Slf4j
public class RedisIdGenerator implements IdGenerator {

	private final static long timestampSeed = 1526227200551L;
	private final static long initalValue = 100000000000000000L;
	private final static String redisCounter = "serial:";
	private RedisAtomicLong serial;

	public RedisIdGenerator(RedisTemplate<String, Long> redisTemplate) {
		this("bigint", redisTemplate);
	}

	public RedisIdGenerator(String name, RedisTemplate<String, Long> redisTemplate) {
		final String key = redisCounter + name;
		serial = new RedisAtomicLong(key, redisTemplate);
		if (serial.get() < initalValue) {
			serial.set(getSerialNo());
		}
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
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				ThreadUtils.sleep(1000L);
			}
		}

	}

	public void destroy() {
		serial.expire(1, TimeUnit.SECONDS);
	}

	private static long getSerialNo() {
		return (System.currentTimeMillis() - timestampSeed) << 22;
	}

	public static void main(String[] args) {
		System.out.println(getSerialNo());
	}

}
