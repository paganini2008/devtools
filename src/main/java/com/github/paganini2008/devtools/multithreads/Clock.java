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

import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongConsumer;

import com.github.paganini2008.devtools.event.Event;
import com.github.paganini2008.devtools.event.EventBus;
import com.github.paganini2008.devtools.event.EventSubscriber;
import com.github.paganini2008.devtools.time.DateUtils;

/**
 * 
 * Clock
 * <p>
 * A coarse-grained scheduler driven by a clock that ticks every second. Each tick is published
 * on an {@link EventBus}, and every task whose scheduled time has been reached runs on the
 * executor of the bus. Precision is therefore about one second.
 *
 * @author Fred Feng
 * @date 2020-02-24
 * @since 2.0.1
 */
public final class Clock implements Executable {

	private static final long DEFAULT_TICK_INTERVAL = 1000L;
	private static final long REMAING_TIME_ADJUSTMENT = 10;
	private final AtomicBoolean running = new AtomicBoolean();
	private final AtomicLong sequence = new AtomicLong(0);
	private final ConcurrentMap<String, ClockTask> tasks = new ConcurrentHashMap<String, ClockTask>();
	private final EventBus<ClockEvent, Long> eventBus;
	private final Timer ticker;

	public Clock() {
		this(ExecutorUtils.processorCount * 2);
	}

	public Clock(int nThreads) {
		this(Executors.newFixedThreadPool(nThreads), true);
	}

	/**
	 * Creates and starts a clock.
	 * 
	 * @param executor
	 *            the executor running the tasks
	 * @param autoShutdownExecutor
	 *            whether {@link #stop()} shuts down the executor
	 */
	public Clock(Executor executor, boolean autoShutdownExecutor) {
		this(executor, autoShutdownExecutor, DEFAULT_TICK_INTERVAL);
	}

	Clock(Executor executor, boolean autoShutdownExecutor, long tickInterval) {
		eventBus = new EventBus<ClockEvent, Long>(executor, true, autoShutdownExecutor);
		running.set(true);
		ticker = ThreadUtils.scheduleAtFixedRate(this, tickInterval, TimeUnit.MILLISECONDS);
	}

	/**
	 * Runs the task once after the given delay.
	 * 
	 * @param task
	 *            the task
	 * @param delay
	 *            the delay
	 * @param timeUnit
	 *            the unit of {@code delay}
	 */
	public void schedule(final ClockTask task, final long delay, final TimeUnit timeUnit) {
		scheduleOnce(task, System.currentTimeMillis() + DateUtils.convertToMillis(delay, timeUnit), null);
	}

	/**
	 * Runs the task repeatedly with a fixed delay between the end of one run and the start of
	 * the next, until the task is cancelled, throws, or the clock is stopped.
	 * 
	 * @param task
	 *            the task
	 * @param delay
	 *            the initial delay
	 * @param period
	 *            the delay between runs
	 * @param timeUnit
	 *            the unit of {@code delay} and {@code period}
	 */
	public void schedule(final ClockTask task, final long delay, final long period, final TimeUnit timeUnit) {
		scheduleOnce(task, System.currentTimeMillis() + DateUtils.convertToMillis(delay, timeUnit),
				startTime -> doRepeat(task, period, timeUnit));
	}

	/**
	 * Runs the task repeatedly at a fixed rate, until the task is cancelled, throws, or the clock
	 * is stopped.
	 * 
	 * @param task
	 *            the task
	 * @param delay
	 *            the initial delay
	 * @param period
	 *            the period between the starts of successive runs
	 * @param timeUnit
	 *            the unit of {@code delay} and {@code period}
	 */
	public void scheduleAtFixedRate(final ClockTask task, final long delay, final long period, final TimeUnit timeUnit) {
		final long periodMillis = DateUtils.convertToMillis(period, timeUnit);
		scheduleOnce(task, System.currentTimeMillis() + DateUtils.convertToMillis(delay, timeUnit),
				startTime -> doRepeatAtFixedRate(task, remaining(startTime, periodMillis), periodMillis));
	}

	private static long remaining(long startTime, long periodMillis) {
		long remaining = periodMillis - (System.currentTimeMillis() - startTime);
		return remaining <= 0 ? REMAING_TIME_ADJUSTMENT : remaining;
	}

	void doRepeat(final ClockTask task, final long delay, final TimeUnit timeUnit) {
		scheduleOnce(task, System.currentTimeMillis() + DateUtils.convertToMillis(delay, timeUnit),
				startTime -> doRepeat(task, delay, timeUnit));
	}

	void doRepeatAtFixedRate(final ClockTask task, final long remaining, final long delay) {
		scheduleOnce(task, System.currentTimeMillis() + remaining,
				startTime -> doRepeatAtFixedRate(task, remaining(startTime, delay), delay));
	}

	private void scheduleOnce(final ClockTask task, final long due, final LongConsumer next) {
		if (!isRunning()) {
			return;
		}
		final String key = task.getTaskId() + ":" + sequence.incrementAndGet();
		tasks.put(key, task);
		eventBus.subscribe(new EventSubscriber<ClockEvent, Long>() {
			public void onEventFired(ClockEvent event) {
				if (event.getArgument().longValue() < due) {
					return;
				}
				final ClockTask target = tasks.remove(key);
				if (target == null) {
					return;
				}
				try {
					long startTime = System.currentTimeMillis();
					target.run();
					if (next != null && !target.isCancelled()) {
						next.accept(startTime);
					}
				} finally {
					eventBus.unsubscribe(this);
				}
			}
		});
	}

	/**
	 * Publishes a clock tick; called by the internal ticker.
	 * 
	 * @return true while the clock is running
	 */
	public boolean execute() {
		if (isRunning()) {
			eventBus.publish(new ClockEvent(this, System.currentTimeMillis()));
			return true;
		}
		return false;
	}

	public boolean isRunning() {
		return running.get();
	}

	/**
	 * Returns the number of scheduled runs that are still pending.
	 * 
	 * @return the number of pending runs
	 */
	public int getTaskCount() {
		return tasks.size();
	}

	/**
	 * Stops the clock, discards pending tasks and waits for running tasks to complete. Must not
	 * be called from within a task.
	 */
	public void stop() {
		if (running.compareAndSet(true, false)) {
			ticker.cancel();
			tasks.clear();
			eventBus.close();
		}
	}

	/**
	 * 
	 * ClockEvent
	 * 
	 * @author Fred Feng
	 *
	 * @since 2.0.1
	 */
	static class ClockEvent extends Event<Long> implements Cloneable {

		ClockEvent(Object source, long timestamp) {
			super(source, timestamp);
		}

		public Clock getSource() {
			return (Clock) super.getSource();
		}

		public ClockEvent clone() {
			try {
				return (ClockEvent) super.clone();
			} catch (CloneNotSupportedException e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
		}

		private static final long serialVersionUID = 1L;

	}
}
