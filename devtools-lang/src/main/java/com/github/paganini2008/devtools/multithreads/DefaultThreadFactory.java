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

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.paganini2008.devtools.StringUtils;

/**
 * DefaultThreadFactory
 * 
 * @author Fred Feng
 * @since 2.0.1
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
