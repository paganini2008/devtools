package com.github.paganini2008.springworld.support.redis;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

/**
 * 
 * RedisAtomicCounter
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-03
 * @version 1.0
 */
public class RedisAtomicCounter {

	public static final String redisCounterNamePrefix = "redisCounter:";
	private final RedisAtomicLong counter;
	private final String name;
	private final long initialValue;

	public RedisAtomicCounter(String name, RedisConnectionFactory factory, long initialValue) {
		this.counter = new RedisAtomicLong(redisCounterNamePrefix + name, factory, initialValue);
		this.name = name;
		this.initialValue = initialValue;
	}

	public RedisAtomicCounter(String name, RedisTemplate<String, Long> redisTemplate, long initialValue) {
		this.counter = new RedisAtomicLong(redisCounterNamePrefix + name, redisTemplate, initialValue);
		this.name = name;
		this.initialValue = initialValue;
	}

	public void expire(long timeout, TimeUnit unit) {
		counter.expire(timeout, unit);
	}

	public void expire(Date future) {
		counter.expireAt(future);
	}

	public long get() {
		long value;
		try {
			value = counter.get();
		} catch (RuntimeException e) {
			counter.set(initialValue);
			value = counter.get();
		}
		return value;
	}

	public void set(long delta) {
		counter.set(delta);
	}

	public long incrementAndGet() {
		return counter.incrementAndGet();
	}

	public long decrementAndGet() {
		return counter.decrementAndGet();
	}

	public long getAndIncrement() {
		return counter.getAndIncrement();
	}

	public long getAndDecrement() {
		return counter.getAndDecrement();
	}

	public long getAndAdd(long delta) {
		return counter.getAndAdd(delta);
	}

	public long addAndGet(long delta) {
		return counter.addAndGet(delta);
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return getName() + ": " + String.valueOf(counter.get());
	}

}
