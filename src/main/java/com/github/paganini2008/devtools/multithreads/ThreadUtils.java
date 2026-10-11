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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.RandomUtils;
import com.github.paganini2008.devtools.Sequence;

/**
 * 
 * ThreadUtils
 * <p>
 * Thread related utilities: sleeping, monitor based waiting and notification, starting threads,
 * {@link java.util.Timer} based scheduling and simple parallel loops.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class ThreadUtils {

	/**
	 * Sleeps a random number of milliseconds in {@code [0, to)}.
	 *
	 * @return false if interrupted (the interrupt status is preserved)
	 */
	public static boolean randomSleep(long to) {
		return randomSleep(0, to);
	}

	/**
	 * Sleeps a random number of milliseconds in {@code [from, to)}.
	 *
	 * @return false if interrupted (the interrupt status is preserved)
	 */
	public static boolean randomSleep(long from, long to) {
		return sleep(RandomUtils.randomLong(from, to));
	}

	/**
	 * Sleeps for the given time.
	 *
	 * @return false if interrupted (the interrupt status is preserved)
	 */
	public static boolean sleep(long timeout, TimeUnit timeUnit) {
		return sleep(convertToMillis(timeout, timeUnit));
	}

	/**
	 * Sleeps for the given milliseconds; non-positive values return immediately.
	 *
	 * @return false if interrupted (the interrupt status is preserved)
	 */
	public static boolean sleep(long ms) {
		if (ms > 0) {
			try {
				Thread.sleep(ms);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return false;
			}
		}
		return true;
	}

	/**
	 * Waits on the monitor until the condition becomes true (a null result counts as false). The
	 * condition is evaluated while holding the monitor; callers must notify the monitor after changing
	 * the state it depends on.
	 *
	 * @return true when the condition holds, false if the monitor is null or the thread was
	 *         interrupted (the interrupt status is preserved)
	 * @throws IllegalArgumentException
	 *             if the condition is null
	 */
	public static boolean wait(Object monitor, Supplier<Boolean> condition) {
		if (monitor != null) {
			Assert.isNull(condition, "NonNull condition");
			while (true) {
				synchronized (monitor) {
					if (Boolean.TRUE.equals(condition.get())) {
						return true;
					} else {
						try {
							monitor.wait();
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
							break;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Like {@link #wait(Object, Supplier)} but waits at most {@code timeout} milliseconds.
	 *
	 * @return true when the condition holds, false on timeout, interruption or a null monitor
	 */
	public static boolean wait(Object monitor, Supplier<Boolean> condition, long timeout) {
		if (monitor != null) {
			Assert.isNull(condition, "NonNull condition");
			long nanos = TimeUnit.MILLISECONDS.toNanos(timeout);
			final long deadline = System.nanoTime() + nanos;
			synchronized (monitor) {
				while (true) {
					if (Boolean.TRUE.equals(condition.get())) {
						return true;
					}
					if (nanos <= 0) {
						break;
					}
					try {
						TimeUnit.NANOSECONDS.timedWait(monitor, nanos);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						break;
					}
					nanos = deadline - System.nanoTime();
				}
			}
		}
		return false;
	}

	/**
	 * Wakes up one thread waiting on the monitor; does nothing for a null monitor.
	 */
	public static void notify(Object monitor) {
		notify(monitor, () -> {
			return true;
		});
	}

	/**
	 * Wakes up one thread waiting on the monitor if the condition is true.
	 *
	 * @throws IllegalArgumentException
	 *             if the condition is null
	 */
	public static void notify(Object monitor, Supplier<Boolean> condition) {
		if (monitor != null) {
			Assert.isNull(condition, "NonNull condition");
			if (Boolean.TRUE.equals(condition.get())) {
				synchronized (monitor) {
					monitor.notify();
				}
			}
		}
	}

	/**
	 * Wakes up all threads waiting on the monitor; does nothing for a null monitor.
	 */
	public static void notifyAll(Object monitor) {
		notifyAll(monitor, () -> {
			return true;
		});
	}

	/**
	 * Wakes up all threads waiting on the monitor if the condition is true.
	 *
	 * @throws IllegalArgumentException
	 *             if the condition is null
	 */
	public static void notifyAll(Object monitor, Supplier<Boolean> condition) {
		if (monitor != null) {
			Assert.isNull(condition, "NonNull condition");
			if (Boolean.TRUE.equals(condition.get())) {
				synchronized (monitor) {
					monitor.notifyAll();
				}
			}
		}
	}

	/**
	 * Double-checked update: runs the operation while holding the monitor if the condition is true
	 * both before and after acquiring it (a null result counts as false). A null monitor means the
	 * {@code ThreadUtils} class lock.
	 *
	 * @throws IllegalArgumentException
	 *             if the condition or operation is null
	 */
	public static void forUpdate(Object monitor, Supplier<Boolean> condition, Runnable operation) {
		if (monitor == null) {
			monitor = ThreadUtils.class;
		}
		Assert.isNull(condition, "NonNull condition");
		Assert.isNull(operation, "NonNull operation");
		if (Boolean.TRUE.equals(condition.get())) {
			synchronized (monitor) {
				if (Boolean.TRUE.equals(condition.get())) {
					operation.run();
				}
			}
		}
	}

	/**
	 * Starts a new thread running the runnable; uncaught exceptions are printed.
	 */
	public static Thread runAsThread(Runnable runnable) {
		return runAsThread(runnable, (t, e) -> {
			e.printStackTrace();
		});
	}

	/**
	 * Starts a new thread running the runnable with the given uncaught exception handler.
	 */
	public static Thread runAsThread(Runnable runnable, Thread.UncaughtExceptionHandler exceptionHandler) {
		Thread t = new Thread(runnable);
		t.setUncaughtExceptionHandler(exceptionHandler);
		t.start();
		return t;
	}

	/**
	 * Starts a new named thread running the runnable.
	 */
	public static Thread runAsThread(String name, Runnable runnable) {
		Thread t = new Thread(runnable, name);
		t.start();
		return t;
	}

	/**
	 * Starts a new named thread with the given uncaught exception handler.
	 */
	public static Thread runAsThread(String name, Runnable runnable, Thread.UncaughtExceptionHandler exceptionHandler) {
		Thread t = new Thread(runnable, name);
		t.setUncaughtExceptionHandler(exceptionHandler);
		t.start();
		return t;
	}

	/**
	 * Creates a thread with the factory and starts it.
	 */
	public static Thread runAsThread(ThreadFactory threadFactory, Runnable runnable) {
		Thread t = threadFactory.newThread(runnable);
		t.start();
		return t;
	}

	/**
	 * Returns the name of the current thread.
	 */
	public static String currentThreadName() {
		return Thread.currentThread().getName();
	}

	/**
	 * Runs the runnable once at the given time on a new (non-daemon) timer thread.
	 *
	 * @throws IllegalArgumentException
	 *             if the date is in the past
	 */
	public static void schedule(Runnable r, Date date) {
		if (date.before(new Date())) {
			throw new IllegalArgumentException("Past time: " + date);
		}
		schedule(r, date.getTime() - System.currentTimeMillis());
	}

	/**
	 * Runs the runnable once after the given delay on a new (non-daemon) timer thread.
	 */
	public static void schedule(Runnable r, long delay, TimeUnit timeUnit) {
		schedule(r, convertToMillis(delay, timeUnit));
	}

	/**
	 * Runs the runnable once after {@code delay} milliseconds (negative means now) on a new
	 * (non-daemon) timer thread, which ends after the run.
	 */
	public static void schedule(final Runnable r, long delay) {
		if (delay < 0) {
			delay = 0;
		}
		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				try {
					r.run();
				} finally {
					timer.cancel();
				}
			}
		}, delay);
	}

	/**
	 * Schedules at fixed rate, first run at {@code firstTime}.
	 *
	 * @throws IllegalArgumentException
	 *             if {@code firstTime} is in the past
	 */
	public static Timer scheduleAtFixedRate(Executable e, Date firstTime, long interval, TimeUnit timeUnit) {
		return scheduleAtFixedRate(e, firstTime, convertToMillis(interval, timeUnit));
	}

	/**
	 * Schedules at fixed rate (milliseconds), first run at {@code firstTime}.
	 *
	 * @throws IllegalArgumentException
	 *             if {@code firstTime} is in the past
	 */
	public static Timer scheduleAtFixedRate(Executable e, Date firstTime, long interval) {
		if (firstTime.before(new Date())) {
			throw new IllegalArgumentException("Past time: " + firstTime);
		}
		return scheduleAtFixedRate(e, firstTime.getTime() - System.currentTimeMillis(), interval);
	}

	/**
	 * Schedules at fixed rate; the initial delay equals the interval.
	 */
	public static Timer scheduleAtFixedRate(Executable e, long interval, TimeUnit timeUnit) {
		return scheduleAtFixedRate(e, interval, interval, timeUnit);
	}

	/**
	 * Schedules at fixed rate.
	 */
	public static Timer scheduleAtFixedRate(Executable e, long delay, long interval, TimeUnit timeUnit) {
		return scheduleAtFixedRate(e, delay, timeUnit, interval, timeUnit);
	}

	/**
	 * Schedules at fixed rate.
	 */
	public static Timer scheduleAtFixedRate(Executable e, long delay, TimeUnit delayTimeUnit, long interval, TimeUnit intervalTimeUnit) {
		return scheduleAtFixedRate(e, convertToMillis(delay, delayTimeUnit), convertToMillis(interval, intervalTimeUnit));
	}

	/**
	 * Runs the executable at a fixed rate on a new {@link Timer} until it returns false (or
	 * {@link Executable#onError(Throwable)} returns false), then cancels the timer and calls
	 * {@link Executable#onCancellation(Throwable)}.
	 * <p>
	 * The timer thread is <b>not</b> a daemon thread: callers must make sure the task eventually
	 * returns false or cancel the returned timer, otherwise it keeps the JVM alive.
	 *
	 * @param delay
	 *            initial delay in milliseconds (negative means now)
	 * @param interval
	 *            period in milliseconds
	 * @return the timer, which can be used to cancel the task
	 */
	public static Timer scheduleAtFixedRate(final Executable e, long delay, long interval) {
		if (delay < 0) {
			delay = 0;
		}
		final Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				Throwable thrown = null;
				boolean result = true;
				try {
					result = e.execute();
				} catch (Throwable error) {
					thrown = error;
					result = onError(e, error);
				} finally {
					if (!result) {
						timer.cancel();
						e.onCancellation(thrown);
					}
				}
			}
		}, delay, interval);
		return timer;
	}

	/**
	 * Schedules with fixed delay, first run at {@code firstTime}.
	 *
	 * @throws IllegalArgumentException
	 *             if {@code firstTime} is in the past
	 */
	public static Timer scheduleWithFixedDelay(Executable e, Date firstTime, long interval, TimeUnit timeUnit) {
		return scheduleWithFixedDelay(e, firstTime, convertToMillis(interval, timeUnit));
	}

	/**
	 * Schedules with fixed delay (milliseconds), first run at {@code firstTime}.
	 *
	 * @throws IllegalArgumentException
	 *             if {@code firstTime} is in the past
	 */
	public static Timer scheduleWithFixedDelay(Executable e, Date firstTime, long interval) {
		if (firstTime.before(new Date())) {
			throw new IllegalArgumentException("Past time: " + firstTime);
		}
		return scheduleWithFixedDelay(e, firstTime.getTime() - System.currentTimeMillis(), interval);
	}

	/**
	 * Schedules with fixed delay; the initial delay equals the interval.
	 */
	public static Timer scheduleWithFixedDelay(Executable e, long interval, TimeUnit timeUnit) {
		return scheduleWithFixedDelay(e, interval, interval, timeUnit);
	}

	/**
	 * Schedules with fixed delay.
	 */
	public static Timer scheduleWithFixedDelay(Executable e, long delay, long interval, TimeUnit timeUnit) {
		return scheduleWithFixedDelay(e, delay, timeUnit, interval, timeUnit);
	}

	/**
	 * Schedules with fixed delay.
	 */
	public static Timer scheduleWithFixedDelay(Executable e, long delay, TimeUnit delayTimeUnit, long interval, TimeUnit intervalTimeUnit) {
		return scheduleWithFixedDelay(e, convertToMillis(delay, delayTimeUnit), convertToMillis(interval, intervalTimeUnit));
	}

	/**
	 * Like {@link #scheduleAtFixedRate(Executable, long, long)} but with a fixed delay between the end
	 * of one run and the start of the next. The timer thread is not a daemon thread.
	 */
	public static Timer scheduleWithFixedDelay(final Executable e, long delay, long interval) {
		if (delay < 0) {
			delay = 0;
		}
		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				Throwable thrown = null;
				boolean result = true;
				try {
					result = e.execute();
				} catch (Throwable error) {
					thrown = error;
					result = onError(e, error);
				} finally {
					if (!result) {
						timer.cancel();
						e.onCancellation(thrown);
					}
				}
			}
		}, delay, interval);
		return timer;
	}

	/**
	 * Creates a {@link ThreadLocal} whose initial value comes from the supplier.
	 */
	private static boolean onError(Executable e, Throwable error) {
		try {
			return e.onError(error);
		} catch (Throwable ignored) {
			// A failing error handler stops the schedule instead of killing the timer thread
			return false;
		}
	}

	public static <T> ThreadLocal<T> newThreadLocal(final Supplier<T> supplier) {
		return new ThreadLocal<T>() {

			@Override
			protected T initialValue() {
				return supplier.get();
			}

		};
	}

	/**
	 * Runs the consumer for {@code 0..loops-1} on a temporary pool of {@code nThreads} threads and
	 * waits until all iterations have completed.
	 */
	public static <T> void loop(int nThreads, int loops, Consumer<Integer> consumer) {
		loop(nThreads, Sequence.forEach(loops), consumer);
	}

	/**
	 * Runs the consumer for every element on a temporary pool of {@code nThreads} threads and waits
	 * until all iterations have completed.
	 */
	public static <T> void loop(int nThreads, Iterable<T> iterable, Consumer<T> consumer) {
		final ThreadPool threadPool = ThreadPoolBuilder.common(nThreads).build();
		for (final T t : iterable) {
			threadPool.apply(() -> {
				consumer.accept(t);
			});
		}
		threadPool.shutdown();
	}

	/**
	 * Load test helper: {@code nThreads} producer threads each submit {@code 0..loops-1} to a pool
	 * limited to {@code maxPermits} concurrent tasks; returns when everything has run.
	 */
	public static <T> void benchmark(int nThreads, int maxPermits, int loops, Consumer<Integer> consumer) {
		benchmark(nThreads, maxPermits, Sequence.forEach(loops), consumer);
	}

	/**
	 * Load test helper: {@code nThreads} producer threads each submit every element to a pool limited
	 * to {@code maxPermits} concurrent tasks; returns when everything has run.
	 */
	public static <T> void benchmark(int nThreads, int maxPermits, Iterable<T> iterable, Consumer<T> consumer) {
		final ThreadPool threadPool = ThreadPoolBuilder.common(nThreads).setMaxPermits(maxPermits).build();
		List<Thread> runners = new ArrayList<Thread>();
		for (int i = 0; i < nThreads; i++) {
			runners.add(runAsThread(() -> {
				for (final T t : iterable) {
					threadPool.apply(() -> {
						consumer.accept(t);
					});
				}
			}));
		}
		for (Thread thread : runners) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
		}
		threadPool.shutdown();
	}

	/**
	 * Creates a pool with {@code 2 * processors} threads, blocking submission and an unbounded wait
	 * queue.
	 */
	public static ThreadPool commonPool() {
		return commonPool(Runtime.getRuntime().availableProcessors() * 2);
	}

	/**
	 * Creates a pool with the given size, blocking submission and an unbounded wait queue.
	 */
	public static ThreadPool commonPool(int maxPoolSize) {
		return commonPool(maxPoolSize, -1L, Integer.MAX_VALUE);
	}

	/**
	 * Creates a pool; see {@link GenericThreadPool} for the timeout semantics.
	 */
	public static ThreadPool commonPool(int maxPoolSize, long timeout, int queueSize) {
		return ThreadPoolBuilder.common(maxPoolSize).setTimeout(timeout).setQueueSize(queueSize).build();
	}

}
