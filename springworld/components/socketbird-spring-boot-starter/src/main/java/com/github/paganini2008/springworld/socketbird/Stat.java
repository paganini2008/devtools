package com.github.paganini2008.springworld.socketbird;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.paganini2008.devtools.date.DateUtils;
import com.github.paganini2008.devtools.date.DurationType;
import com.github.paganini2008.devtools.io.FileUtils;
import com.github.paganini2008.devtools.multithreads.AtomicPositiveLong;
import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

/**
 * 
 * Stat
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public class Stat {

	private static final int MAX_RETAIN_SIZE = 60 * 60;
	private final AtomicPositiveLong count = new AtomicPositiveLong(0);
	private TpsCounter tpsCounter;
	private long startTime;
	private final AtomicBoolean finished = new AtomicBoolean(true);
	private Timer timer;

	public Stat() {
		tpsCounter = new TpsCounter();
	}

	public void reset() {
		startTime = System.currentTimeMillis();
		count.set(0);
		finished.set(false);

	}

	private class TpsCounter implements Executable {

		TpsCounter() {
		}

		private Timer timer;
		private int tps;
		private long prev = 0;

		public void start() {
			timer = ThreadUtils.scheduleAtFixedRate(this, 1, 1, TimeUnit.SECONDS);
		}

		@Override
		public boolean execute() {
			long n = count.get();
			if (n > 0) {
				tps = (int) (n - prev);
				prev = n;
			}
			return finished.get();
		}

		public int get() {
			return tps;
		}

	}

	public Map<Long, Integer> getLatestTps() {
		return tpsCounter.getLatest();
	}

	public void setStartedTime(long startedTime) {
		this.startedTime = startedTime;
	}

	public int tps() {
		return tpsCounter.get();
	}

	public long completeUp() {
		return complete.incrementAndGet();
	}

	public long countUp() {
		return count.incrementAndGet();
	}

	public int concurrentsUp() {
		return concurrents.incrementAndGet();
	}

	public int concurrentsDown() {
		return concurrents.decrementAndGet();
	}

	public long complete() {
		return complete.get();
	}

	public long count() {
		return count.get();
	}

	public int concurrents() {
		return concurrents.get();
	}

	public long getTotalMemory() {
		return Runtime.getRuntime().totalMemory();
	}

	public long getFreeMemory() {
		return Runtime.getRuntime().freeMemory();
	}

	public void close() {
		count.set(0);
		tpsCounter.stop();
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("Has run: ")
				.append(startedTime > 0
						? DateUtils.formatDuration(System.currentTimeMillis() - startedTime, DurationType.DAY, "#D:#H:#m:#s")
						: "-");
		str.append(", Count: ").append(count());
		str.append(", Complete/Count: ").append(complete() + "/" + count());
		str.append(", Tps: ").append(tps());
		str.append(", Concurrents: ").append(concurrents());
		str.append(", [JVM]Free/Total: ").append(FileUtils.formatSize(getFreeMemory()) + "/" + FileUtils.formatSize(getTotalMemory()));
		return str.toString();
	}

}
