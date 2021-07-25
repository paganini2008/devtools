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
package com.github.paganini2008.devtools.io.monitor;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ExecutorUtils;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

/**
 * 
 * FileMonitor
 * 
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public class FileMonitor implements Executable {

	private final List<FileWatcher> watchers = new CopyOnWriteArrayList<FileWatcher>();
	private final long interval;
	private final TimeUnit timeUnit;
	private final AtomicBoolean running = new AtomicBoolean(false);
	private Executor executor;

	public FileMonitor(long interval, TimeUnit timeUnit) {
		this.interval = interval;
		this.timeUnit = timeUnit;
	}

	public void addWatchers(FileWatcher... fileWatchers) {
		if (fileWatchers != null) {
			for (FileWatcher fileWatcher : fileWatchers) {
				watchers.add(fileWatcher);
			}
		}
	}

	public void removeWatchers(FileWatcher... fileWatchers) {
		if (fileWatchers != null) {
			for (FileWatcher fileWatcher : fileWatchers) {
				watchers.remove(fileWatcher);
			}
		}
	}

	public void start() {
		if (isRunning()) {
			throw new IllegalStateException("FileMonitor is already running.");
		}
		running.set(true);
		for (FileWatcher watcher : watchers) {
			watcher.start();
		}
		ThreadUtils.scheduleWithFixedDelay(this, interval, timeUnit);
	}

	public void stop() {
		if (!isRunning()) {
			throw new IllegalStateException("FileMonitor is not running");
		}
		running.set(false);
	}

	public void setExecutor(Executor executor) {
		this.executor = executor;
	}

	public boolean isRunning() {
		return running.get();
	}

	public boolean execute() {
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

	public void onCancellation() {
		if (executor != null) {
			ExecutorUtils.gracefulShutdown(executor, 60000);
		}
	}

	protected void beforeRunning() {
	}

	protected void afterRunning() {
	}
}
