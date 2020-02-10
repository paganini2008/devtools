package com.github.paganini2008.springworld.transport;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

/**
 * 
 * Counter
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
public final class Counter implements Executable {

	private final RedisAtomicLong counter;
	private final AtomicLong localCounter = new AtomicLong();
	private final AtomicBoolean running = new AtomicBoolean();
	private long increment;
	private long localIncrement;
	private long tps;
	private long localTps;

	public Counter(RedisAtomicLong counter) {
		this.counter = counter;
	}

	public void reset() {
		try {
			counter.set(0);
		} catch (Exception ignored) {
		}
		localCounter.set(0);
	}

	public void incrementCount() {
		try {
			counter.incrementAndGet();
		} catch (Exception ignored) {
		}
		localCounter.incrementAndGet();
	}

	public long get() {
		try {
			return counter.get();
		} catch (Exception ignored) {
			return 0;
		}
	}

	public long getLocal() {
		return localCounter.get();
	}

	public void start() {
		running.set(true);
		ThreadUtils.scheduleAtFixedRate(this, 1, TimeUnit.SECONDS);
	}

	public void stop() {
		running.set(false);
	}

	public long getTps() {
		return tps;
	}

	public long getLocalTps() {
		return localTps;
	}

	@Override
	public boolean execute() {
		if (get() > 0) {
			long current = get();
			tps = current - increment;
			increment = current;
		}
		if (getLocal() > 0) {
			long current = getLocal();
			localTps = current - localIncrement;
			localIncrement = current;
		}
		return running.get();
	}

}
