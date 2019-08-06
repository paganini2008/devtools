package com.github.paganini2008.springworld.support.redis;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.data.redis.core.RedisTemplate;

/**
 * 
 * RedisSharedMutex
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-06
 * @version 1.0
 */
public class RedisSharedMutex {

	private final String name;
	private final int maxPermits;
	private final RedisAtomicCounter counter;
	private final int timeout;

	public RedisSharedMutex(String name, int maxPermits, int timeout, RedisTemplate<String, Long> redisTemplate) {
		this.name = name;
		this.maxPermits = maxPermits;
		this.counter = new RedisAtomicCounter(name, redisTemplate, 0L);
		this.counter.expire(timeout, TimeUnit.SECONDS);
		this.timeout = timeout;
	}

	private final Lock lock = new ReentrantLock();
	private final Condition condition = lock.newCondition();

	public boolean acquire() {
		while (true) {
			lock.lock();
			try {
				if (counter.get() < maxPermits) {
					counter.incrementAndGet();
					if (System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(timeout, TimeUnit.SECONDS) <= 1000) {
						counter.expire(timeout, TimeUnit.SECONDS);
					}
					return true;
				} else {
					try {
						condition.await(60, TimeUnit.SECONDS);
					} catch (InterruptedException e) {
						break;
					}
				}
			} finally {
				lock.unlock();
			}
		}
		return false;
	}

	public void release() {
		if (!isLocked()) {
			return;
		}
		lock.lock();
		try {
			condition.signalAll();
			counter.decrementAndGet();
		} finally {
			lock.unlock();
		}
	}

	public boolean isLocked() {
		return counter.get() > 0;
	}

	public String getName() {
		return name;
	}

}
