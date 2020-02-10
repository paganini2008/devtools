package com.github.paganini2008.springworld.redis.concurrents;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import com.github.paganini2008.devtools.multithreads.ThreadUtils;

/**
 * 
 * RedisSharedLatch
 *
 * @author Fred Feng
 * @created 2020-01
 * @revised 2020-02
 * @version 1.0
 */
public class RedisSharedLatch implements SharedLatch {

	private static final String LATCH_KEY_PREFIX = "latch:";

	public RedisSharedLatch(String name, int maxPermits, RedisConnectionFactory redisConnectionFactory, int expiration) {
		this.counter = new RedisAtomicLong(LATCH_KEY_PREFIX + name, redisConnectionFactory);
		this.counter.expire(expiration, TimeUnit.SECONDS);
		this.expiration = expiration;
		this.maxPermits = maxPermits;
		this.startTime = System.currentTimeMillis();
	}

	private final int maxPermits;
	private final int expiration;
	private final long startTime;
	private final Lock lock = new ReentrantLock();
	private final Condition condition = lock.newCondition();
	protected RedisAtomicLong counter;

	public boolean acquire() {
		while (true) {
			lock.lock();
			try {
				if (counter.get() < maxPermits) {
					counter.incrementAndGet();
					return true;
				} else {
					try {
						condition.await();
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

	public boolean acquire(long timeout, TimeUnit timeUnit) {
		final long begin = System.nanoTime();
		long elapsed;
		long nanosTimeout = TimeUnit.NANOSECONDS.convert(timeout, timeUnit);
		while (true) {
			lock.lock();
			try {
				if (counter.get() < maxPermits) {
					counter.incrementAndGet();
					return true;
				} else {
					if (nanosTimeout > 0) {
						try {
							condition.awaitNanos(nanosTimeout);
						} catch (InterruptedException e) {
							break;
						}
						elapsed = (System.nanoTime() - begin);
						nanosTimeout -= elapsed;
					} else {
						break;
					}
				}
			} finally {
				lock.unlock();
			}
		}
		return false;
	}

	public boolean tryAcquire() {
		if (counter.get() < maxPermits) {
			counter.incrementAndGet();
			return true;
		}
		return false;
	}

	public long availablePermits() {
		return maxPermits - counter.get();
	}

	public void release() {
		if (!isLocked()) {
			return;
		}
		lock.lock();
		condition.signalAll();
		counter.decrementAndGet();
		lock.unlock();
	}

	public boolean isLocked() {
		return counter.get() > 0;
	}

	public long join() {
		while (isLocked()) {
			ThreadUtils.randomSleep(1000L);
		}
		return System.currentTimeMillis() - startTime;
	}

	public String getKey() {
		return counter.getKey();
	}

	public void keepAlive(Lifespan lifespan, int checkInterval) {
		if (lifespan != null) {
			lifespan.watch(counter.getKey(), expiration, checkInterval, TimeUnit.SECONDS);
		}
	}

}
