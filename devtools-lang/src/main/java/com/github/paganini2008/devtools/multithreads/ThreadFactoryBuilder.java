package com.github.paganini2008.devtools.multithreads;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;

import com.github.paganini2008.devtools.Assert;

/**
 * 
 * ThreadFactoryBuilder
 * 
 * @author Jimmy Hoff
 * 
 * @version 1.0
 */
public class ThreadFactoryBuilder {

	private String nameFormat;
	private Boolean daemon;
	private Integer priority;
	private UncaughtExceptionHandler uncaughtExceptionHandler;
	private ThreadFactory backingThreadFactory;

	public String getNameFormat() {
		return nameFormat;
	}

	public ThreadFactoryBuilder setNameFormat(String nameFormat) {
		this.nameFormat = nameFormat;
		return this;
	}

	public Boolean getDaemon() {
		return daemon;
	}

	public ThreadFactoryBuilder setDaemon(Boolean daemon) {
		this.daemon = daemon;
		return this;
	}

	public Integer getPriority() {
		return priority;
	}

	public ThreadFactoryBuilder setPriority(Integer priority) {
		if (priority != null) {
			Assert.gt(priority, Thread.MAX_PRIORITY, "Thread priority (%s) must be <= %s",
					new Object[] { priority, Thread.MAX_PRIORITY });
			Assert.lt(priority, Thread.MIN_PRIORITY, "Thread priority (%s) must be >= %s",
					new Object[] { priority, Thread.MIN_PRIORITY });
		}
		this.priority = priority;
		return this;
	}

	public UncaughtExceptionHandler getUncaughtExceptionHandler() {
		return uncaughtExceptionHandler;
	}

	public ThreadFactoryBuilder setUncaughtExceptionHandler(UncaughtExceptionHandler uncaughtExceptionHandler) {
		this.uncaughtExceptionHandler = uncaughtExceptionHandler;
		return this;
	}

	public ThreadFactory getBackingThreadFactory() {
		return backingThreadFactory;
	}

	public ThreadFactoryBuilder setBackingThreadFactory(ThreadFactory backingThreadFactory) {
		this.backingThreadFactory = backingThreadFactory;
		return this;
	}

	public ThreadFactory build() {
		final ThreadFactory threadFactory = backingThreadFactory != null ? backingThreadFactory : new DefaultThreadFactory();
		return new ThreadFactory() {

			final AtomicIntegerSequence count = nameFormat != null ? new AtomicIntegerSequence() : null;

			public Thread newThread(Runnable r) {
				Thread thread = threadFactory.newThread(r);
				if (thread == null) {
					throw new IllegalStateException("Null thread object!");
				}
				if (nameFormat != null) {
					thread.setName(String.format(nameFormat, count.getAndIncrement()));
				}
				if (daemon != null) {
					thread.setDaemon(daemon);
				}
				if (priority != null) {
					thread.setPriority(priority);
				}
				if (uncaughtExceptionHandler != null) {
					thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
				}
				return thread;
			}
		};
	}

}
