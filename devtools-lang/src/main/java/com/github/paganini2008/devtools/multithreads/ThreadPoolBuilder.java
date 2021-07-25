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

import java.util.concurrent.ThreadFactory;

import com.github.paganini2008.devtools.multithreads.latch.CounterLatch;
import com.github.paganini2008.devtools.multithreads.latch.Latch;

/**
 * 
 * ThreadPoolBuilder
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
public class ThreadPoolBuilder {

	private int maxPoolSize;
	private Latch latch;
	private int queueSize;
	private long timeout;
	private ThreadFactory threadFactory;

	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	public ThreadPoolBuilder setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
		return this;
	}

	public Latch getLatch() {
		return latch;
	}

	public ThreadPoolBuilder setLatch(Latch latch) {
		this.latch = latch;
		return this;
	}

	public ThreadPoolBuilder setMaxPermits(int maxPermits) {
		return setLatch(maxPermits > 0 ? new CounterLatch(maxPermits) : CounterLatch.newUnlimitedLatch());
	}

	public int getQueueSize() {
		return queueSize;
	}

	public ThreadPoolBuilder setQueueSize(int queueSize) {
		this.queueSize = queueSize;
		return this;
	}

	public ThreadFactory getThreadFactory() {
		return threadFactory;
	}

	public ThreadPoolBuilder setThreadFactory(ThreadFactory threadFactory) {
		this.threadFactory = threadFactory;
		return this;
	}

	public long getTimeout() {
		return timeout;
	}

	public ThreadPoolBuilder setTimeout(long timeout) {
		this.timeout = timeout;
		return this;
	}

	public ThreadPool build() {
		return new GenericThreadPool(maxPoolSize, latch, timeout, queueSize, threadFactory);
	}

	ThreadPoolBuilder() {
	}

	public static ThreadPoolBuilder common(int maxPoolSize) {
		ThreadPoolBuilder builder = new ThreadPoolBuilder();
		return builder.setMaxPoolSize(maxPoolSize).setLatch(CounterLatch.newUnlimitedLatch()).setQueueSize(Integer.MAX_VALUE)
				.setTimeout(-1L).setThreadFactory(new PooledThreadFactory());
	}

}
