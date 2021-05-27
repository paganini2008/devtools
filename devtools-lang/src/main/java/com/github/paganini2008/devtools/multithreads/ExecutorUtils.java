package com.github.paganini2008.devtools.multithreads;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 
 * ExecutorUtils
 * 
 * @author Fred Feng
 * @version 1.0
 */
public abstract class ExecutorUtils {

	public static final int processorCount = Runtime.getRuntime().availableProcessors();

	public static <T> T callInBackground(ExecutorService executor, Callable<T> callable) throws Exception {
		if (executor != null) {
			Future<T> future = executor.submit(callable);
			return future.get();
		} else {
			return callable.call();
		}
	}

	public static void runInBackground(Executor executor, Runnable runnable) {
		if (executor != null) {
			executor.execute(runnable);
		} else {
			runnable.run();
		}
	}

	public static boolean isTerminated(Executor executor) {
		if (executor instanceof ExecutorService) {
			if (((ExecutorService) executor).isTerminated()) {
				return true;
			}
		}
		return false;
	}

	public static boolean isShutdown(Executor executor) {
		if (executor instanceof ExecutorService) {
			if (((ExecutorService) executor).isShutdown()) {
				return true;
			}
		}
		return false;
	}

	public static void gracefulShutdown(Executor executor, final long timeout) {
		if (!(executor instanceof ExecutorService) || isShutdown(executor)) {
			return;
		}
		final ExecutorService es = (ExecutorService) executor;
		try {
			es.shutdown();
		} catch (RuntimeException ex) {
			return;
		}
		if (!isShutdown(es)) {
			ThreadUtils.runAsThread(new Runnable() {
				public void run() {
					try {
						es.shutdownNow();
					} catch (RuntimeException ex) {
						return;
					}
					try {
						es.awaitTermination(timeout, TimeUnit.MILLISECONDS);
					} catch (InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
				}
			});
		}
	}

	public static Executor directExecutor() {
		return DirectExecutor.INSTANCE;
	}

	public static Executor newThreadExecutor() {
		return NewThreadExecutor.INSTANCE;
	}

	static class DirectExecutor implements Executor {

		final static DirectExecutor INSTANCE = new DirectExecutor();

		public void execute(Runnable r) {
			r.run();
		}

	}

	static class NewThreadExecutor implements Executor {

		final static NewThreadExecutor INSTANCE = new NewThreadExecutor();

		public void execute(Runnable r) {
			ThreadUtils.runAsThread(r);
		}
	}
}