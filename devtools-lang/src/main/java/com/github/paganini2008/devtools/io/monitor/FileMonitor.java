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
 * @author Jimmy Hoff
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
