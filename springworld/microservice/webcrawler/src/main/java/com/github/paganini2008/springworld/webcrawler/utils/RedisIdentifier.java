package com.github.paganini2008.springworld.webcrawler.utils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

/**
 * 
 * RedisIdentifier
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
public class RedisIdentifier implements LongIdentifier {

	private final RedisAtomicLong counter;

	public RedisIdentifier(String name, RedisTemplate<String, Long> redisTemplate) {
		this.counter = new RedisAtomicLong(name, redisTemplate);
	}

	@Override
	public void setValue(long initialValue) {
		counter.set(initialValue);
	}
	
	@Override
	public long currentValue() {
		return counter.get();
	}

	@Override
	public long nextValue() {
		return counter.incrementAndGet();
	}

	@Override
	public long nextValue(int delta) {
		return counter.addAndGet(delta);
	}

}
