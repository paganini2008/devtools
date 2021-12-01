/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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

import com.github.paganini2008.devtools.RandomUtils;
import com.github.paganini2008.devtools.Sequence;

/**
 * 
 * ThreadUtils
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public abstract class ThreadUtils {

	public static boolean randomSleep(long to) {
		return randomSleep(0, to);
	}

	public static boolean randomSleep(long from, long to) {
		return sleep(RandomUtils.randomLong(from, to));
	}

	public static boolean sleep(long timeout, TimeUnit timeUnit) {
		return sleep(convertToMillis(timeout, timeUnit));
	}

	public static boolean sleep(long ms) {
		if (ms > 0) {
			try {
				Thread.sleep(ms);
			} catch (InterruptedException ignored) {
				return false;
			}
		}
		return true;
	}

	public static boolean wait(Object monitor, Supplier<Boolean> condition) {
		if (monitor != null) {
			while (true) {
				synchronized (monitor) {
					if (condition.get()) {
						return true;
					} else {
						try {
							monitor.wait();
						} catch (InterruptedException ignored) {
							break;
						}
					}
				}
			}
		}
		return false;
	}

	public static boolean wait(Object monitor, Supplier<Boolean> condition, long timeout) {
		if (monitor != null) {
			final long begin = System.nanoTime();
			long elapsed;
			long m = timeout;
			long n = 0;
			while (true) {
				synchronized (monitor) {
					if (condition.get()) {
						return true;
					} else {
						if (m > 0) {
							try {
								monitor.wait(m, (int) n);
							} catch (InterruptedException ignored) {
								break;
							}
							elapsed = (System.nanoTime() - begin);
							m -= elapsed / 1000000L;
							n = elapsed % 1000000L;
						} else {
							break;
						}
					}
				}
			}
		}
		return false;
	}

	public static void notify(Object monitor) {
		notify(monitor, () -> {
			return true;
		});
	}

	public static void notify(Object monitor, Supplier<Boolean> condition) {
		if (monitor != null) {
			if (condition.get()) {
				synchronized (monitor) {
					monitor.notify();
				}
			}
		}
	}

	public static void notifyAll(Object monitor) {
		notifyAll(monitor, () -> {
			return true;
		});
	}

	public static void notifyAll(Object monitor, Supplier<Boolean> condition) {
		if (monitor != null) {
			if (condition.get()) {
				synchronized (monitor) {
					monitor.notifyAll();
				}
			}
		}
	}

	public static Thread runAsThread(Runnable runnable) {
		return runAsThread(runnable, (t, e) -> {
			e.printStackTrace();
		});
	}

	public static Thread runAsThread(Runnable runnable, Thread.UncaughtExceptionHandler exceptionHandler) {
		Thread t = new Thread(runnable);
		t.setUncaughtExceptionHandler(exceptionHandler);
		t.start();
		return t;
	}

	public static Thread runAsThread(String name, Runnable runnable) {
		Thread t = new Thread(runnable, name);
		t.start();
		return t;
	}

	public static Thread runAsThread(String name, Runnable runnable, Thread.UncaughtExceptionHandler exceptionHandler) {
		Thread t = new Thread(runnable, name);
		t.setUncaughtExceptionHandler(exceptionHandler);
		t.start();
		return t;
	}

	public static Thread runAsThread(ThreadFactory threadFactory, Runnable runnable) {
		Thread t = threadFactory.newThread(runnable);
		t.start();
		return t;
	}

	public static String currentThreadName() {
		return Thread.currentThread().getName();
	}

	public static void schedule(Runnable r, Date date) {
		if (date.before(new Date())) {
			throw new IllegalArgumentException("Past time: " + date);
		}
		schedule(r, date.getTime() - System.currentTimeMillis());
	}

	public static void schedule(Runnable r, long delay, TimeUnit timeUnit) {
		schedule(r, convertToMillis(delay, timeUnit));
	}

	public static void schedule(final Runnable r, long delay) {
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

	public static Timer scheduleAtFixedRate(Executable e, Date firstTime, long interval, TimeUnit timeUnit) {
		return scheduleAtFixedRate(e, firstTime, convertToMillis(interval, timeUnit));
	}

	public static Timer scheduleAtFixedRate(Executable e, Date firstTime, long interval) {
		if (firstTime.before(new Date())) {
			throw new IllegalArgumentException("Past time: " + firstTime);
		}
		return scheduleAtFixedRate(e, firstTime.getTime() - System.currentTimeMillis(), interval);
	}

	public static Timer scheduleAtFixedRate(Executable e, long interval, TimeUnit timeUnit) {
		return scheduleAtFixedRate(e, interval, interval, timeUnit);
	}

	public static Timer scheduleAtFixedRate(Executable e, long delay, long interval, TimeUnit timeUnit) {
		return scheduleAtFixedRate(e, delay, timeUnit, interval, timeUnit);
	}

	public static Timer scheduleAtFixedRate(Executable e, long delay, TimeUnit delayTimeUnit, long interval, TimeUnit intervalTimeUnit) {
		return scheduleAtFixedRate(e, convertToMillis(delay, delayTimeUnit), convertToMillis(interval, intervalTimeUnit));
	}

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
					result = e.onError(error);
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

	public static Timer scheduleWithFixedDelay(Executable e, Date firstTime, long interval, TimeUnit timeUnit) {
		return scheduleWithFixedDelay(e, firstTime, convertToMillis(interval, timeUnit));
	}

	public static Timer scheduleWithFixedDelay(Executable e, Date firstTime, long interval) {
		if (firstTime.before(new Date())) {
			throw new IllegalArgumentException("Past time: " + firstTime);
		}
		return scheduleWithFixedDelay(e, firstTime.getTime() - System.currentTimeMillis(), interval);
	}

	public static Timer scheduleWithFixedDelay(Executable e, long interval, TimeUnit timeUnit) {
		return scheduleWithFixedDelay(e, interval, interval, timeUnit);
	}

	public static Timer scheduleWithFixedDelay(Executable e, long delay, long interval, TimeUnit timeUnit) {
		return scheduleWithFixedDelay(e, delay, timeUnit, interval, timeUnit);
	}

	public static Timer scheduleWithFixedDelay(Executable e, long delay, TimeUnit delayTimeUnit, long interval, TimeUnit intervalTimeUnit) {
		return scheduleWithFixedDelay(e, convertToMillis(delay, delayTimeUnit), convertToMillis(interval, intervalTimeUnit));
	}

	public static Timer scheduleWithFixedDelay(final Executable e, long delay, long interval) {
		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				Throwable thrown = null;
				boolean result = true;
				try {
					result = e.execute();
				} catch (Throwable error) {
					thrown = error;
					result = e.onError(error);
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

	public static <T> ThreadLocal<T> newThreadLocal(final Supplier<T> supplier) {
		return new ThreadLocal<T>() {

			@Override
			protected T initialValue() {
				return supplier.get();
			}

		};
	}

	public static <T> void loop(int nThreads, int loops, Consumer<Integer> consumer) {
		loop(nThreads, Sequence.forEach(loops), consumer);
	}

	public static <T> void loop(int nThreads, Iterable<T> iterable, Consumer<T> consumer) {
		final ThreadPool threadPool = ThreadPoolBuilder.common(nThreads).build();
		for (final T t : iterable) {
			threadPool.apply(() -> {
				consumer.accept(t);
			});
		}
		threadPool.shutdown();
	}

	public static <T> void benchmark(int nThreads, int maxPermits, int loops, Consumer<Integer> consumer) {
		benchmark(nThreads, maxPermits, Sequence.forEach(loops), consumer);
	}

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
			} catch (InterruptedException ignored) {
			}
		}
		threadPool.shutdown();
	}

	public static ThreadPool commonPool() {
		return commonPool(Runtime.getRuntime().availableProcessors() * 2);
	}

	public static ThreadPool commonPool(int maxPoolSize) {
		return commonPool(maxPoolSize, -1L, Integer.MAX_VALUE);
	}

	public static ThreadPool commonPool(int maxPoolSize, long timeout, int queueSize) {
		return ThreadPoolBuilder.common(maxPoolSize).setTimeout(timeout).setQueueSize(queueSize).build();
	}

}
