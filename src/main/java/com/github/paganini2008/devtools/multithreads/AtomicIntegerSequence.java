/**
* Copyright 2017-2026 Fred Feng (paganini.fy@gmail.com)

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
 * <p>
 * A thread-safe cyclic {@code int} sequence within {@code [initialValue, maxValue]}: incrementing past
 * the maximum wraps to the initial value and decrementing below the initial value wraps to the
 * maximum.
 * 
 * @author Fred Feng
 * @date 2019-05-29
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

	/**
	 * Increments and returns the previous value; wraps to the initial value after the maximum.
	 */
	public int getAndIncrement() {
		while (true) {
			int current = i.get();
			int next = (current >= maxValue ? initialValue : current + 1);
			if (i.compareAndSet(current, next)) {
				return current;
			}
		}
	}

	/**
	 * Decrements and returns the previous value; wraps to the maximum below the initial value.
	 */
	public int getAndDecrement() {
		while (true) {
			int current = i.get();
			int next = (current <= initialValue ? maxValue : current - 1);
			if (i.compareAndSet(current, next)) {
				return current;
			}
		}
	}

	/**
	 * Increments and returns the new value; wraps to the initial value after the maximum.
	 */
	public int incrementAndGet() {
		while (true) {
			int current = i.get();
			int next = (current >= maxValue ? initialValue : current + 1);
			if (i.compareAndSet(current, next)) {
				return next;
			}
		}
	}

	/**
	 * Decrements and returns the new value; wraps to the maximum below the initial value.
	 */
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

	/**
	 * Sets the value.
	 *
	 * @throws IllegalArgumentException
	 *             if the value is outside {@code [initialValue, maxValue]}
	 */
	public void set(int newValue) {
		if (newValue >= initialValue && newValue <= maxValue) {
			i.set(newValue);
		} else {
			throw new IllegalArgumentException("New value must >= " + initialValue + " and <= " + maxValue + ".");
		}
	}

	/**
	 * Sets the value and returns the previous one.
	 *
	 * @throws IllegalArgumentException
	 *             if the value is outside {@code [initialValue, maxValue]}
	 */
	public int getAndSet(int newValue) {
		if (newValue >= initialValue && newValue <= maxValue) {
			return i.getAndSet(newValue);
		}
		throw new IllegalArgumentException("New value must >= " + initialValue + " and <= " + maxValue + ".");
	}

	/**
	 * Adds {@code delta} and returns the previous value; the result wraps to the initial value when
	 * it would exceed the maximum and to the maximum when it would fall below the initial value.
	 */
	public int getAndAdd(int delta) {
		while (true) {
			int current = i.get();
			long sum = (long) current + delta;
			int next = sum > maxValue ? initialValue : sum < initialValue ? maxValue : (int) sum;
			if (i.compareAndSet(current, next)) {
				return current;
			}
		}
	}

	/**
	 * Adds {@code delta} and returns the new value, wrapping like {@link #getAndAdd(int)}.
	 */
	public int addAndGet(int delta) {
		while (true) {
			int current = i.get();
			long sum = (long) current + delta;
			int next = sum > maxValue ? initialValue : sum < initialValue ? maxValue : (int) sum;
			if (i.compareAndSet(current, next)) {
				return next;
			}
		}
	}

	/**
	 * Atomically sets the value to {@code update} if it equals {@code expect}.
	 *
	 * @throws IllegalArgumentException
	 *             if {@code update} is outside {@code [initialValue, maxValue]}
	 */
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

	/**
	 * Changes the maximum value; the current value is not adjusted.
	 */
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
