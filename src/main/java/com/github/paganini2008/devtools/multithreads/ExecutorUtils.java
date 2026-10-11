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

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 
 * ExecutorUtils
 * <p>
 * Utility methods for {@link Executor}s: pool statistics, running tasks in the background and
 * graceful shutdown. All methods tolerate null or non-pool executors.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class ExecutorUtils {

	public static final int processorCount = Runtime.getRuntime().availableProcessors();

	/**
	 * Returns the current pool size of a {@link ThreadPoolExecutor}, or {@code defaultValue} for
	 * any other (or null) executor.
	 */
	public static int getPoolSize(Executor executor, int defaultValue) {
		if (executor instanceof ThreadPoolExecutor) {
			return ((ThreadPoolExecutor) executor).getPoolSize();
		} else {
			return defaultValue;
		}
	}

	/**
	 * Returns the core pool size of a {@link ThreadPoolExecutor}, or {@code defaultValue}.
	 */
	public static int getCorePoolSize(Executor executor, int defaultValue) {
		if (executor instanceof ThreadPoolExecutor) {
			return ((ThreadPoolExecutor) executor).getCorePoolSize();
		} else {
			return defaultValue;
		}
	}

	/**
	 * Returns the largest pool size of a {@link ThreadPoolExecutor}, or {@code defaultValue}.
	 */
	public static int getLargestPoolSize(Executor executor, int defaultValue) {
		if (executor instanceof ThreadPoolExecutor) {
			return ((ThreadPoolExecutor) executor).getLargestPoolSize();
		} else {
			return defaultValue;
		}
	}

	/**
	 * Returns the maximum pool size of a {@link ThreadPoolExecutor}, or {@code defaultValue}.
	 */
	public static int getMaximumPoolSize(Executor executor, int defaultValue) {
		if (executor instanceof ThreadPoolExecutor) {
			return ((ThreadPoolExecutor) executor).getMaximumPoolSize();
		} else {
			return defaultValue;
		}
	}

	/**
	 * Returns the number of active threads of a {@link ThreadPoolExecutor}, or {@code defaultValue}.
	 */
	public static int getActiveCount(Executor executor, int defaultValue) {
		if (executor instanceof ThreadPoolExecutor) {
			return ((ThreadPoolExecutor) executor).getActiveCount();
		} else {
			return defaultValue;
		}
	}

	/**
	 * Returns the completed task count of a {@link ThreadPoolExecutor}, or {@code defaultValue}.
	 */
	public static long getCompletedTaskCount(Executor executor, int defaultValue) {
		if (executor instanceof ThreadPoolExecutor) {
			return ((ThreadPoolExecutor) executor).getCompletedTaskCount();
		} else {
			return defaultValue;
		}
	}

	/**
	 * Returns the queue size of a {@link ThreadPoolExecutor}, or {@code defaultValue}.
	 */
	public static long getQueueSize(Executor executor, int defaultValue) {
		if (executor instanceof ThreadPoolExecutor) {
			return ((ThreadPoolExecutor) executor).getQueue().size();
		} else {
			return defaultValue;
		}
	}

	/**
	 * Calls the callable on the executor and waits for its result; calls it directly in the current
	 * thread if the executor is null.
	 *
	 * @throws Exception
	 *             the callable's exception when called directly, or an
	 *             {@link java.util.concurrent.ExecutionException} wrapping it when run on the executor
	 */
	public static <T> T callInBackground(ExecutorService executor, Callable<T> callable) throws Exception {
		if (executor != null) {
			Future<T> future = executor.submit(callable);
			return future.get();
		} else {
			return callable.call();
		}
	}

	/**
	 * Runs the runnable on the executor, or directly in the current thread if the executor is null.
	 */
	public static void runInBackground(Executor executor, Runnable runnable) {
		if (executor != null) {
			executor.execute(runnable);
		} else {
			runnable.run();
		}
	}

	/**
	 * Returns whether the executor is an {@link ExecutorService} that has terminated.
	 */
	public static boolean isTerminated(Executor executor) {
		if (executor instanceof ExecutorService) {
			if (((ExecutorService) executor).isTerminated()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns whether the executor is an {@link ExecutorService} that has been shut down.
	 */
	public static boolean isShutdown(Executor executor) {
		if (executor instanceof ExecutorService) {
			if (((ExecutorService) executor).isShutdown()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Shuts down the executor if it is an {@link ExecutorService}: previously submitted tasks are
	 * executed but no new tasks are accepted. A background thread then waits up to
	 * {@code timeout} milliseconds for termination and calls {@link ExecutorService#shutdownNow()}
	 * if the executor has not terminated by then. Does nothing for null, non-ExecutorService or
	 * already shut down executors.
	 * 
	 * @param executor
	 *            the executor, may be null
	 * @param timeout
	 *            the maximum milliseconds to wait before forcing shutdown
	 */
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
		if (es.isTerminated()) {
			return;
		}
		Thread t = new Thread(() -> {
			try {
				if (!es.awaitTermination(timeout, TimeUnit.MILLISECONDS)) {
					es.shutdownNow();
				}
			} catch (InterruptedException ex) {
				es.shutdownNow();
				Thread.currentThread().interrupt();
			} catch (RuntimeException ignored) {
			}
		}, "gracefulShutdown");
		t.setDaemon(true);
		t.start();
	}

	/**
	 * Returns an executor running tasks in the calling thread.
	 */
	public static Executor directExecutor() {
		return DirectExecutor.INSTANCE;
	}

	/**
	 * Returns an executor running each task in a new thread.
	 */
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