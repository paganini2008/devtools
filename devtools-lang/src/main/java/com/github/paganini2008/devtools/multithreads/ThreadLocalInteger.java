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
package com.github.paganini2008.devtools.multithreads;

/**
 * 
 * ThreadLocalInteger
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public class ThreadLocalInteger extends Number {

	private static final long serialVersionUID = 8299620990228879920L;
	private final ThreadLocal<Integer> threadLocal;

	public ThreadLocalInteger() {
		this(0);
	}

	public ThreadLocalInteger(final int value) {
		threadLocal = new ThreadLocal<Integer>() {
			protected Integer initialValue() {
				return value;
			}
		};
	}

	public int getAndDecrement() {
		return getAndAdd(-1);
	}

	public int getAndIncrement() {
		return getAndAdd(1);
	}

	public int getAndAdd(int delta) {
		int prev = get();
		threadLocal.set(prev + delta);
		return prev;
	}

	public int incrementAndGet() {
		return addAndGet(1);
	}

	public int addAndGet(int delta) {
		int prev = get();
		threadLocal.set(prev + delta);
		return threadLocal.get();
	}

	public int decrementAndGet() {
		return addAndGet(-1);
	}
	
	public void reset() {
		threadLocal.remove();
	}
	
	public void set(int delta) {
		threadLocal.set(delta);
	}

	public int get() {
		return threadLocal.get().intValue();
	}

	@Override
	public int intValue() {
		return get();
	}

	@Override
	public long longValue() {
		return (long) get();
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
