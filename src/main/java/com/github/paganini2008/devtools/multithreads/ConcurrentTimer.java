/**
* Copyright 2017-2026 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.paganini2008.devtools.multithreads;

import static com.github.paganini2008.devtools.time.DateUtils.convertToMillis;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * 
 * ConcurrentTimer
 * <p>
 * Schedules {@link Executable}s, sharing a single {@link Timer} thread among all executables
 * scheduled with the same delay, interval and scheduling mode. Each executable keeps being
 * executed until its {@link Executable#execute()} (or {@link Executable#onError(Throwable)})
 * returns false; a failing executable never affects the others sharing the same timer.
 *
 * @author Fred Feng
 * @date 2021-08-13
 *
 * @since 2.0.3
 */
public class ConcurrentTimer {

	private final Map<TimerFeature, Timer> timers = new ConcurrentHashMap<TimerFeature, Timer>();
	private final Map<TimerFeature, List<Executable>> executables = new ConcurrentHashMap<TimerFeature, List<Executable>>();

	/**
	 * Schedules with fixed delay, first run at {@code firstTime}.
	 * 
	 * @throws IllegalArgumentException
	 *             if {@code firstTime} is in the past
	 */
	public Timer scheduleWithFixedDelay(Executable e, Date firstTime, long interval, TimeUnit timeUnit) {
		return scheduleWithFixedDelay(e, firstTime, convertToMillis(interval, timeUnit));
	}

	/**
	 * Schedules with fixed delay (milliseconds), first run at {@code firstTime}.
	 * 
	 * @throws IllegalArgumentException
	 *             if {@code firstTime} is in the past
	 */
	public Timer scheduleWithFixedDelay(Executable e, Date firstTime, long interval) {
		if (firstTime.before(new Date())) {
			throw new IllegalArgumentException("Past time: " + firstTime);
		}
		return scheduleWithFixedDelay(e, firstTime.getTime() - System.currentTimeMillis(), interval);
	}

	/**
	 * Schedules with fixed delay; the initial delay equals the interval.
	 */
	public Timer scheduleWithFixedDelay(Executable e, long interval, TimeUnit timeUnit) {
		return scheduleWithFixedDelay(e, interval, interval, timeUnit);
	}

	/**
	 * Schedules with fixed delay.
	 */
	public Timer scheduleWithFixedDelay(Executable e, long delay, long interval, TimeUnit timeUnit) {
		return scheduleWithFixedDelay(e, delay, timeUnit, interval, timeUnit);
	}

	/**
	 * Schedules with fixed delay.
	 */
	public Timer scheduleWithFixedDelay(Executable e, long delay, TimeUnit delayTimeUnit, long interval, TimeUnit intervalTimeUnit) {
		return scheduleWithFixedDelay(e, convertToMillis(delay, delayTimeUnit), convertToMillis(interval, intervalTimeUnit));
	}

	/**
	 * Schedules the executable with fixed delay (milliseconds).
	 * 
	 * @param e
	 *            the executable
	 * @param delay
	 *            the initial delay
	 * @param interval
	 *            the delay between the end of one run and the start of the next
	 * @return the (shared) timer that runs the executable
	 */
	public Timer scheduleWithFixedDelay(Executable e, long delay, long interval) {
		return schedule(e, new TimerFeature(delay, interval, false));
	}

	/**
	 * Schedules at fixed rate, first run at {@code firstTime}.
	 * 
	 * @throws IllegalArgumentException
	 *             if {@code firstTime} is in the past
	 */
	public Timer scheduleAtFixedRate(Executable e, Date firstTime, long interval, TimeUnit timeUnit) {
		return scheduleAtFixedRate(e, firstTime, convertToMillis(interval, timeUnit));
	}

	/**
	 * Schedules at fixed rate (milliseconds), first run at {@code firstTime}.
	 * 
	 * @throws IllegalArgumentException
	 *             if {@code firstTime} is in the past
	 */
	public Timer scheduleAtFixedRate(Executable e, Date firstTime, long interval) {
		if (firstTime.before(new Date())) {
			throw new IllegalArgumentException("Past time: " + firstTime);
		}
		return scheduleAtFixedRate(e, firstTime.getTime() - System.currentTimeMillis(), interval);
	}

	/**
	 * Schedules at fixed rate; the initial delay equals the interval.
	 */
	public Timer scheduleAtFixedRate(Executable e, long interval, TimeUnit timeUnit) {
		return scheduleAtFixedRate(e, interval, interval, timeUnit);
	}

	/**
	 * Schedules at fixed rate.
	 */
	public Timer scheduleAtFixedRate(Executable e, long delay, long interval, TimeUnit timeUnit) {
		return scheduleAtFixedRate(e, delay, timeUnit, interval, timeUnit);
	}

	/**
	 * Schedules at fixed rate.
	 */
	public Timer scheduleAtFixedRate(Executable e, long delay, TimeUnit delayTimeUnit, long interval, TimeUnit intervalTimeUnit) {
		return scheduleAtFixedRate(e, convertToMillis(delay, delayTimeUnit), convertToMillis(interval, intervalTimeUnit));
	}

	/**
	 * Schedules the executable at fixed rate (milliseconds).
	 * 
	 * @param e
	 *            the executable
	 * @param delay
	 *            the initial delay
	 * @param interval
	 *            the period between the starts of successive runs
	 * @return the (shared) timer that runs the executable
	 */
	public Timer scheduleAtFixedRate(Executable e, long delay, long interval) {
		return schedule(e, new TimerFeature(delay, interval, true));
	}

	private synchronized Timer schedule(Executable e, TimerFeature feature) {
		if (e == null) {
			throw new NullPointerException("Null executable");
		}
		List<Executable> list = executables.computeIfAbsent(feature, k -> new CopyOnWriteArrayList<Executable>());
		list.add(e);
		return timers.computeIfAbsent(feature, k -> {
			SerialExecutable serial = new SerialExecutable(k, list);
			return k.fixed ? ThreadUtils.scheduleAtFixedRate(serial, k.delay, k.interval)
					: ThreadUtils.scheduleWithFixedDelay(serial, k.delay, k.interval);
		});
	}

	/**
	 * Cancels all timers and notifies every scheduled executable via
	 * {@link Executable#onCancellation(Throwable)} (with a null cause).
	 */
	public synchronized void cancel() {
		for (Map.Entry<TimerFeature, Timer> entry : timers.entrySet()) {
			entry.getValue().cancel();
			List<Executable> list = executables.get(entry.getKey());
			if (list != null) {
				for (Executable executable : list) {
					executable.onCancellation(null);
				}
			}
		}
		executables.clear();
		timers.clear();
	}

	/**
	 * Releases the timer of a feature once it has no executable left. Returns true if the timer
	 * should keep running.
	 */
	private synchronized boolean keepAlive(TimerFeature feature, List<Executable> list) {
		if (!list.isEmpty()) {
			return true;
		}
		if (executables.get(feature) == list) {
			executables.remove(feature);
			timers.remove(feature);
		}
		return false;
	}

	/**
	 * 
	 * SerialExecutable
	 *
	 * @author Fred Feng
	 *
	 * @since 2.0.3
	 */
	class SerialExecutable implements Executable {

		private final TimerFeature feature;
		private final List<Executable> executables;

		SerialExecutable(TimerFeature feature, List<Executable> executables) {
			this.feature = feature;
			this.executables = executables;
		}

		@Override
		public boolean execute() {
			for (Executable executable : executables) {
				boolean keep;
				Throwable cause = null;
				try {
					keep = executable.execute();
				} catch (Throwable e) {
					cause = e;
					try {
						keep = executable.onError(e);
					} catch (Throwable ignored) {
						keep = false;
					}
				}
				if (!keep) {
					executables.remove(executable);
					try {
						executable.onCancellation(cause);
					} catch (Throwable ignored) {
					}
				}
			}
			return keepAlive(feature, executables);
		}

		@Override
		public boolean onError(Throwable e) {
			return keepAlive(feature, executables);
		}

	}

	/**
	 * 
	 * TimerFeature
	 *
	 * @author Fred Feng
	 *
	 * @since 2.0.3
	 */
	static final class TimerFeature {

		final long delay;
		final long interval;
		final boolean fixed;

		TimerFeature(long delay, long interval, boolean fixed) {
			this.delay = delay;
			this.interval = interval;
			this.fixed = fixed;
		}

		@Override
		public int hashCode() {
			int result = Long.hashCode(delay);
			result = 31 * result + Long.hashCode(interval);
			return 31 * result + (fixed ? 1 : 0);
		}

		@Override
		public boolean equals(Object target) {
			if (this == target) {
				return true;
			}
			if (!(target instanceof TimerFeature)) {
				return false;
			}
			TimerFeature other = (TimerFeature) target;
			return delay == other.delay && interval == other.interval && fixed == other.fixed;
		}

	}

}
