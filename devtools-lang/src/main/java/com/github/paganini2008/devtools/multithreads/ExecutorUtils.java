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