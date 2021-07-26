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

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * AtomicIntegerSequence
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public class AtomicIntegerSequence extends Number {

	private static final long serialVersionUID = 6144822865358352926L;

	private final AtomicInteger i;

	private final int initialValue;
	private volatile int maxValue;

	public AtomicIntegerSequence() {
		this(0);
	}

	public AtomicIntegerSequence(int initialValue) {
		this(initialValue, Integer.MAX_VALUE);
	}

	public AtomicIntegerSequence(int initialValue, int maxValue) {
		if (initialValue < 0) {
			throw new IllegalArgumentException("Initial value must >= 0.");
		}
		if (initialValue >= maxValue) {
			throw new IllegalArgumentException("Maximum value must > initial value.");
		}
		i = new AtomicInteger(initialValue);
		this.initialValue = initialValue;
		this.maxValue = maxValue;
	}

	public int getAndIncrement() {
		while (true) {
			int current = i.get();
			int next = (current >= maxValue ? initialValue : current + 1);
			if (i.compareAndSet(current, next)) {
				return current;
			}
		}
	}

	public int getAndDecrement() {
		while (true) {
			int current = i.get();
			int next = (current <= initialValue ? maxValue : current - 1);
			if (i.compareAndSet(current, next)) {
				return current;
			}
		}
	}

	public int incrementAndGet() {
		while (true) {
			int current = i.get();
			int next = (current >= maxValue ? initialValue : current + 1);
			if (i.compareAndSet(current, next)) {
				return next;
			}
		}
	}

	public int decrementAndGet() {
		while (true) {
			int current = i.get();
			int next = (current <= initialValue ? maxValue : current - 1);
			if (i.compareAndSet(current, next)) {
				return next;
			}
		}
	}

	public int get() {
		return i.get();
	}

	public void set(int newValue) {
		if (newValue >= initialValue && newValue <= maxValue) {
			i.set(newValue);
		} else {
			throw new IllegalArgumentException("New value must >= " + initialValue + " and <= " + maxValue + ".");
		}
	}

	public int getAndSet(int newValue) {
		if (newValue >= initialValue && newValue <= maxValue) {
			return i.getAndSet(newValue);
		}
		throw new IllegalArgumentException("New value must >= " + initialValue + " and <= " + maxValue + ".");
	}

	public int getAndAdd(int delta) {
		while (true) {
			int current = i.get();
			int next = (current > maxValue - delta ? initialValue : current + delta);
			if (i.compareAndSet(current, next)) {
				return current;
			}
		}
	}

	public int addAndGet(int delta) {
		while (true) {
			int current = i.get();
			int next = (current > maxValue - delta ? initialValue : current + delta);
			if (i.compareAndSet(current, next)) {
				return next;
			}
		}
	}

	public boolean compareAndSet(int expect, int update) {
		if (update >= initialValue && update <= maxValue) {
			return i.compareAndSet(expect, update);
		}
		throw new IllegalArgumentException("New value must >= " + initialValue + " and <= " + maxValue + ".");
	}

	public boolean weakCompareAndSet(int expect, int update) {
		if (update >= initialValue && update <= maxValue) {
			return i.weakCompareAndSet(expect, update);
		}
		throw new IllegalArgumentException("New value must >= " + initialValue + " and <= " + maxValue + ".");
	}

	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	public byte byteValue() {
		return i.byteValue();
	}

	public short shortValue() {
		return i.shortValue();
	}

	public int intValue() {
		return i.intValue();
	}

	public long longValue() {
		return i.longValue();
	}

	public float floatValue() {
		return i.floatValue();
	}

	public double doubleValue() {
		return i.doubleValue();
	}

	public String toString() {
		return i.toString();
	}

	public int hashCode() {
		int prime = 31;
		prime = prime + Integer.hashCode(get());
		return prime;
	}

	public boolean equals(Object other) {
		if (other instanceof AtomicIntegerSequence) {
			AtomicIntegerSequence integer = (AtomicIntegerSequence) other;
			return integer.get() == get();
		}
		return false;
	}
}
