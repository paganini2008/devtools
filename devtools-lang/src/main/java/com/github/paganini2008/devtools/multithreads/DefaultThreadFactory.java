package com.github.paganini2008.devtools.multithreads;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.paganini2008.devtools.StringUtils;

/**
 * DefaultThreadFactory
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class DefaultThreadFactory implements ThreadFactory {

	private static final AtomicInteger threadNumber = new AtomicInteger(1);
	private final String nameFormat;
	private ThreadGroup threadGroup;
	private UncaughtExceptionHandler exceptionHandler;

	public DefaultThreadFactory() {
		this(null);
	}

	public DefaultThreadFactory(String prefix) {
		SecurityManager s = System.getSecurityManager();
		this.threadGroup = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
		this.nameFormat = StringUtils.isNotBlank(prefix) ? prefix + "-thread-" : "thread-";
		this.exceptionHandler = new UncaughtExceptionHandler() {
			public void uncaughtException(Thread t, Throwable ignored) {
				ignored.printStackTrace(System.err);
			}
		};
	}

	public void setThreadGroup(ThreadGroup threadGroup) {
		this.threadGroup = threadGroup;
	}

	public void setExceptionHandler(UncaughtExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	public Thread newThread(Runnable r) {
		Thread t = new Thread(threadGroup, r, nameFormat + threadNumber.getAndIncrement(), 0);
		if (t.isDaemon()) {
			t.setDaemon(false);
		}
		if (t.getPriority() != Thread.NORM_PRIORITY) {
			t.setPriority(Thread.NORM_PRIORITY);
		}
		t.setUncaughtExceptionHandler(exceptionHandler);
		return t;
	}

}
