package com.github.paganini2008.devtools.io.monitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.github.paganini2008.devtools.logging.Log;
import com.github.paganini2008.devtools.logging.LogFactory;
import com.github.paganini2008.devtools.multithreads.ExecutorUtils;



/**
 * 文件变化监视器，拥有一到多个文件变化观察者
 * 
 * @author yan.feng
 * @version 1.0
 */
public class FileMonitor extends TimerTask {

	private static final Log logger = LogFactory.getLog(FileMonitor.class);

	private static int nextSerialNumber = 0;

	private static synchronized int serialNumber() {
		return nextSerialNumber++;
	}

	private final List<FileWatcher> watchers = new CopyOnWriteArrayList<FileWatcher>();
	private Timer timer;
	private long interval = 3000L;
	private TimeUnit timeUnit = TimeUnit.MILLISECONDS;
	private volatile boolean running = false;
	private boolean blocking = false;

	private final AtomicLong count = new AtomicLong(0);
	private volatile long art = 0;

	private ExecutorService threadPool;

	public FileMonitor(FileWatcher... fileWatchers) {
		this.threadPool = getThreadPool();
		if (fileWatchers != null) {
			this.watchers.addAll(Arrays.asList(fileWatchers));
		}
	}

	protected ExecutorService getThreadPool() {
		return Executors.newCachedThreadPool();
	}

	public long getCompletedCount() {
		return count.get();
	}

	private void countUp() {
		long l = count.incrementAndGet();
		if (l < 0) {
			count.set(0);
		}
	}

	public List<FileWatcher> getWatchers() {
		return watchers;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}

	public void setBlocking(boolean blocking) {
		this.blocking = blocking;
	}

	public synchronized void start() {
		if (running) {
			throw new IllegalStateException("FileMonitor is already running.");
		}
		running = true;
		for (FileWatcher watcher : watchers) {
			watcher.start();
		}
		timer = new Timer(getClass().getSimpleName() + "-" + serialNumber());
		long period = TimeUnit.MILLISECONDS.convert(interval, timeUnit);
		timer.scheduleAtFixedRate(this, period, period);
		logger.info("FileMonitor is started.");
	}

	public synchronized void stop() {
		if (running == false) {
			throw new IllegalStateException("FileMonitor is not running");
		}
		running = false;
		if (timer != null) {
			try {
				timer.cancel();
			} catch (Exception e) {
			}
			timer = null;
		}
		for (FileWatcher watcher : watchers) {
			watcher.stop();
		}
		logger.info("FileMonitor is stopped.");
	}

	public boolean isRunning() {
		return running;
	}

	private void pendingOnFuture(List<Future<Long>> futures) {
		long sum = 0;
		for (Future<Long> future : futures) {
			try {
				sum += future.get();
			} catch (Exception ignored) {
			}
		}
		this.art = sum / futures.size();
		completeRunning();
	}

	public void run() {
		logger.debug("[FileMonitor] Check and notify ...");
		prepareRunning();
		final List<Future<Long>> futures = new ArrayList<Future<Long>>();
		for (final FileWatcher watcher : watchers) {
			futures.add(threadPool.submit(new Callable<Long>() {
				public Long call() throws Exception {
					long start = System.currentTimeMillis();
					watcher.checkAndNotify();
					return System.currentTimeMillis() - start;
				}
			}));
		}
		if (blocking) {
			pendingOnFuture(futures);
		} else {
			threadPool.execute(new Runnable() {
				public void run() {
					pendingOnFuture(futures);
				}
			});
		}
		countUp();
		logger.debug("[FileMonitor] Completed: " + getCompletedCount() + ", Art: " + art);
		logger.debug("[FileMonitor] Check and notify completed.");
	}

	protected void prepareRunning() {
	}

	protected void completeRunning() {
	}

	public void releaseOtherResources() {
		if (threadPool != null) {
			ExecutorUtils.gracefulShutdown(threadPool, 60000L);
		}
	}
}
