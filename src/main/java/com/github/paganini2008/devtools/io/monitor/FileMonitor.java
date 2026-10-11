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
package com.github.paganini2008.devtools.io.monitor;

import java.util.List;
import java.util.Timer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.paganini2008.devtools.logging.Log;
import com.github.paganini2008.devtools.logging.LogFactory;
import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ExecutorUtils;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

/**
 * Periodically checks a group of {@link FileWatcher}s on a background timer thread.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public class FileMonitor implements Executable {

	private static final Log logger = LogFactory.getLog(FileMonitor.class);

	private final List<FileWatcher> watchers = new CopyOnWriteArrayList<FileWatcher>();
	private final long interval;
	private final TimeUnit timeUnit;
	private final AtomicBoolean running = new AtomicBoolean(false);
	private Executor executor;
	private Timer timer;

	/**
	 * @param interval the delay between two checks, must be positive
	 * @param timeUnit the unit of <code>interval</code>, not null
	 * @throws IllegalArgumentException if the interval is not positive or the unit is <code>null</code>
	 */
	public FileMonitor(long interval, TimeUnit timeUnit) {
		if (interval <= 0) {
			throw new IllegalArgumentException("Interval must be greater than zero.");
		}
		if (timeUnit == null) {
			throw new IllegalArgumentException("TimeUnit must not be null.");
		}
		this.interval = interval;
		this.timeUnit = timeUnit;
	}

	/**
	 * Adds watchers, <code>null</code> is ignored.
	 */
	public void addWatchers(FileWatcher... fileWatchers) {
		if (fileWatchers != null) {
			for (FileWatcher fileWatcher : fileWatchers) {
				watchers.add(fileWatcher);
			}
		}
	}

	/**
	 * Removes watchers, <code>null</code> is ignored.
	 */
	public void removeWatchers(FileWatcher... fileWatchers) {
		if (fileWatchers != null) {
			for (FileWatcher fileWatcher : fileWatchers) {
				watchers.remove(fileWatcher);
			}
		}
	}

	/**
	 * Takes the initial snapshot of every watcher and starts checking them periodically.
	 *
	 * @throws IllegalStateException if the monitor is already running
	 */
	public synchronized void start() {
		if (!running.compareAndSet(false, true)) {
			throw new IllegalStateException("FileMonitor is already running.");
		}
		if (timer != null) {
			timer.cancel();
		}
		for (FileWatcher watcher : watchers) {
			watcher.start();
		}
		timer = ThreadUtils.scheduleWithFixedDelay(this, interval, timeUnit);
	}

	/**
	 * Stops checking and shuts down the executor (if any).
	 *
	 * @throws IllegalStateException if the monitor is not running
	 */
	public synchronized void stop() {
		if (!running.compareAndSet(true, false)) {
			throw new IllegalStateException("FileMonitor is not running");
		}
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		onCancellation();
	}

	/**
	 * Sets an executor to check the watchers asynchronously, <code>null</code> to check them on the timer thread. The executor
	 * is shut down when the monitor stops.
	 */
	public void setExecutor(Executor executor) {
		this.executor = executor;
	}

	public boolean isRunning() {
		return running.get();
	}

	/**
	 * Checks all watchers once.
	 *
	 * @return <code>false</code> if the monitor is not running
	 */
	public boolean execute() {
		if (!isRunning()) {
			return false;
		}
		beforeRunning();
		watchers.forEach(fileWatcher -> {
			if (executor != null) {
				executor.execute(() -> {
					fileWatcher.checkAndNotify();
				});
			} else {
				fileWatcher.checkAndNotify();
			}
		});
		afterRunning();
		return isRunning();
	}

	/**
	 * Logs the error and keeps the monitor running unless it has been stopped.
	 */
	public boolean onError(Throwable e) {
		logger.error(e.getMessage(), e);
		return isRunning();
	}

	public void onCancellation(Throwable e) {
		onCancellation();
	}

	public void onCancellation() {
		if (executor != null) {
			ExecutorUtils.gracefulShutdown(executor, 60000);
		}
	}

	/**
	 * Hook invoked before each round of checks.
	 */
	protected void beforeRunning() {
	}

	/**
	 * Hook invoked after each round of checks.
	 */
	protected void afterRunning() {
	}
}
