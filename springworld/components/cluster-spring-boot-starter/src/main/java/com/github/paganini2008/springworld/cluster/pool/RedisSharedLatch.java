package com.github.paganini2008.springworld.cluster.pool;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;
import com.github.paganini2008.springworld.cluster.ContextMasterStandbyEvent;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * RedisSharedLatch
 *
 * @author Fred Feng
 * @created 2020-01
 * @revised 2020-02
 * @version 1.0
 */
@Slf4j
public class RedisSharedLatch implements ClusterLatch {

	public static final String LATCH_KEY_PREFIX = "latch:";
	private final int maxPermits;
	private final long startTime;

	public RedisSharedLatch(String name, int maxPermits, int lifespanInSeconds, RedisConnectionFactory redisConnectionFactory) {
		this.counter = new RedisAtomicLong(LATCH_KEY_PREFIX + name, redisConnectionFactory);
		this.maxPermits = maxPermits;
		this.startTime = System.currentTimeMillis();
		this.lifespan = new Lifespan(lifespanInSeconds);
	}

	private final Lifespan lifespan;
	private final Lock lock = new ReentrantLock();
	private final Condition condition = lock.newCondition();
	private RedisAtomicLong counter;

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

	/**
	 * 
	 * Lifespan
	 *
	 * @author Fred Feng
	 * @created 2020-01
	 * @revised 2020-02
	 * @version 1.0
	 */
	class Lifespan implements Executable {

		private final int timeout;

		Lifespan(int timeout) {
			this.timeout = timeout;
		}

		void start() {
			counter.expire(timeout, TimeUnit.SECONDS);
			ThreadUtils.scheduleAtFixedRate(this, 3, TimeUnit.SECONDS);
		}

		@Override
		public boolean execute() {
			return counter.expire(timeout, TimeUnit.SECONDS);
		}

	}

	@Override
	public void onApplicationEvent(ContextMasterStandbyEvent event) {
		lifespan.start();
		log.info("RedisSharedLatch works.");
	}

}
