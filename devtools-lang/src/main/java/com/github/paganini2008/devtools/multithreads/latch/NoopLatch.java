/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

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
package com.github.paganini2008.devtools.multithreads.latch;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 
 * NoopLatch
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public class NoopLatch implements Latch {

	private final long startTime = System.currentTimeMillis();

	@Override
	public long cons() {
		return 0L;
	}
	
	@Override
	public long availablePermits() {
		return Long.MAX_VALUE;
	}

	@Override
	public boolean acquire() {
		return true;
	}

	@Override
	public boolean tryAcquire() {
		return true;
	}

	@Override
	public boolean acquire(long timeout, TimeUnit timeUnit) {
		return true;
	}

	@Override
	public void release() {
	}

	@Override
	public boolean isLocked() {
		return false;
	}

	@Override
	public long join() {
		return System.currentTimeMillis() - startTime;
	}

	@Override
	public <E> void forEach(Iterable<E> iterable, Executor executor, Consumer<E> consumer) {
		throw new UnsupportedOperationException();
	}

}
