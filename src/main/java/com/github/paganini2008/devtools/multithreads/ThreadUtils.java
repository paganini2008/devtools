package com.github.paganini2008.devtools.multithreads;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.github.paganini2008.devtools.RandomUtils;

/**
 * 
 * ThreadUtils
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @created 2012-01
 * @version 1.0
 */
public class ThreadUtils {

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

	public static boolean wait(Object monitor) {
		return wait(monitor, 0);
	}

	public static boolean wait(Object monitor, long ms) {
		if (monitor != null) {
			synchronized (monitor) {
				try {
					monitor.wait(ms);
				} catch (InterruptedException ignored) {
					return false;
				}
			}
		}
		return true;
	}

	public static void notify(Object monitor) {
		if (monitor != null) {
			synchronized (monitor) {
				monitor.notify();
			}
		}
	}

	public static void notifyAll(Object monitor) {
		if (monitor != null) {
			synchronized (monitor) {
				monitor.notifyAll();
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

	public static void schedule(final Runnable r, Date date) {
		if (date.before(new Date())) {
			throw new IllegalArgumentException("Past time: " + date);
		}
		schedule(r, date.getTime() - System.currentTimeMillis());
	}

	public static void schedule(final Runnable r, long delay, TimeUnit timeUnit) {
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
				boolean result = true;
				try {
					result = e.execute();
				} catch (Exception error) {
					result = e.onError(error);
				} finally {
					if (!result) {
						timer.cancel();
						e.onCancellation();
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

	public static Timer scheduleWithFixedDelay(final Executable e, long delay, long interval, TimeUnit timeUnit) {
		return scheduleWithFixedDelay(e, delay, timeUnit, interval, timeUnit);
	}

	public static Timer scheduleWithFixedDelay(final Executable e, long delay, TimeUnit delayTimeUnit, long interval,
			TimeUnit intervalTimeUnit) {
		return scheduleWithFixedDelay(e, convertToMillis(delay, delayTimeUnit), convertToMillis(interval, intervalTimeUnit));
	}

	public static Timer scheduleWithFixedDelay(final Executable e, long delay, long interval) {
		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				boolean result = true;
				try {
					result = e.execute();
				} catch (Exception error) {
					result = e.onError(error);
				} finally {
					if (!result) {
						timer.cancel();
						e.onCancellation();
					}
				}
			}
		}, delay, interval);
		return timer;
	}

	/**
	 * 
	 * SerialExecutable
	 * 
	 * @author Fred Feng
	 * @revised 2019-05
	 */
	public static class SerialExecutable implements Executable {

		private final List<Executable> executables;

		SerialExecutable(Executable... executables) {
			this.executables = Arrays.asList(executables);
		}

		public boolean execute() {
			boolean result = true;
			for (Executable executable : executables) {
				result &= executable.execute();
			}
			return result;
		}

		public static Executable create(Executable... executables) {
			return new SerialExecutable(executables);
		}

	}

	private static long convertToMillis(long interval, TimeUnit timeUnit) {
		return timeUnit != TimeUnit.MILLISECONDS ? TimeUnit.MILLISECONDS.convert(interval, timeUnit) : interval;
	}

	public static ThreadPool newSimplePool(int maxPoolSize) {
		return newSimplePool(maxPoolSize, -1L, Integer.MAX_VALUE);
	}

	public static ThreadPool newSimplePool(int maxPoolSize, long timeout, int queueSize) {
		return new SimpleThreadPool(maxPoolSize, timeout, queueSize);
	}

	public static ThreadPool newCommonPool(int maxPoolSize) {
		return newCommonPool(maxPoolSize, 0L, Integer.MAX_VALUE);
	}

	public static ThreadPool newCommonPool(int maxPoolSize, long timeout, int queueSize) {
		return new GenericThreadPool(maxPoolSize, maxPoolSize, timeout, queueSize, new PooledThreadFactory());
	}

	private ThreadUtils() {
	}

}
