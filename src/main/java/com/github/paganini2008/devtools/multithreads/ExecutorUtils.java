package com.github.paganini2008.devtools.multithreads;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * ExecutorUtils
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class ExecutorUtils {

	public static ExecutorService commonPool() {
		return commonPool(Runtime.getRuntime().availableProcessors());
	}

	public static ExecutorService commonPool(int nThreads) {
		return new ThreadPoolBuilder(nThreads).build();
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