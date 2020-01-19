package com.github.paganini2008.springworld.webcrawler.core;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

/**
 * 
 * ResourceCounter
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
public class ResourceCounter {

	private final ConcurrentMap<Long, Info> cache = new ConcurrentHashMap<Long, Info>();

	@Qualifier("bigint")
	@Autowired
	private RedisTemplate<String, Long> redisTemplate;

	public void reset(Long id) {
		Info info = cache.get(id);
		if (info == null) {
			cache.putIfAbsent(id, getInfo(id));
			info = cache.get(id);
		}
		info.counter.set(0);
		info.startTime = System.currentTimeMillis();
		info.running.set(true);
	}

	public long incrementCount(Long id) {
		Info info = cache.get(id);
		if (info == null) {
			cache.putIfAbsent(id, getInfo(id));
			info = cache.get(id);
		}
		return info.counter.incrementAndGet();
	}

	public long get(Long id) {
		Info info = cache.get(id);
		return info != null ? info.counter.get() : 0;
	}

	public long getStartTime(Long id) {
		Info info = cache.get(id);
		return info != null ? info.startTime : 0;
	}

	private Info getInfo(Long id) {
		RedisAtomicLong counter = new RedisAtomicLong("webcrawler:counter:" + id, redisTemplate);
		return new Info(counter);
	}

	private static class Info {

		RedisAtomicLong counter;
		AtomicBoolean running;
		long startTime;

		Info(RedisAtomicLong counter) {
			this.counter = counter;
			this.running = new AtomicBoolean(false);
			this.startTime = System.currentTimeMillis();
		}

	}
}
