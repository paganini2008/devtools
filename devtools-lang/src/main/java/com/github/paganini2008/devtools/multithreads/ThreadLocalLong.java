/**
* Copyright 2018-2021 Fred Feng (paganini.fy@gmail.com)

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

/**
 * 
 * ThreadLocalLong
 *
 * @author Fred Feng
 * @version 1.0
 */
public class ThreadLocalLong extends Number {

	private static final long serialVersionUID = 1107286858663072809L;
	private final ThreadLocal<Long> threadLocal;

	public ThreadLocalLong() {
		this(0);
	}

	public ThreadLocalLong(final long value) {
		threadLocal = new ThreadLocal<Long>() {
			protected Long initialValue() {
				return value;
			}
		};
	}

	public long getAndDecrement() {
		return getAndAdd(-1);
	}

	public long getAndIncrement() {
		return getAndAdd(1);
	}

	public long getAndAdd(long delta) {
		long prev = get();
		threadLocal.set(prev + delta);
		return prev;
	}

	public long decrementAndGet() {
		return addAndGet(-1);
	}

	public long incrementAndGet() {
		return addAndGet(1);
	}

	public long addAndGet(long delta) {
		long prev = get();
		threadLocal.set(prev + delta);
		return threadLocal.get();
	}
	
	public void reset() {
		threadLocal.remove();
	}

	public void set(long delta) {
		threadLocal.set(delta);
	}

	public long get() {
		return threadLocal.get().longValue();
	}

	@Override
	public int intValue() {
		return (int) get();
	}

	@Override
	public long longValue() {
		return get();
	}

	@Override
	public float floatValue() {
		return (float) get();
	}

	@Override
	public double doubleValue() {
		return (double) get();
	}

	@Override
	public String toString() {
		return String.valueOf(get());
	}

}
