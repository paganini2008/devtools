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

import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 * AtomicLongSequence
 * <p>
 * A thread-safe cyclic {@code long} sequence within {@code [initialValue, maxValue]}: incrementing
 * past the maximum wraps to the initial value and decrementing below the initial value wraps to the
 * maximum.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 *
 * @since 2.0.1
 */
public class AtomicLongSequence extends Number {

	private static final long serialVersionUID = -2149027152615605579L;

	private final AtomicLong l;

	private volatile long maxValue;
	private final long initialValue;

	public AtomicLongSequence() {
		this(0);
	}

	public AtomicLongSequence(long initialValue) {
		this(initialValue, Long.MAX_VALUE);
	}

	public AtomicLongSequence(long initialValue, long maxValue) {
		if (initialValue < 0) {
			throw new IllegalArgumentException("Initial value must >= 0.");
		}
		if (initialValue >= maxValue) {
			throw new IllegalArgumentException("Maximum value must > initial value.");
		}
		l = new AtomicLong(initialValue);
		this.initialValue = initialValue;
		this.maxValue = maxValue;
	}

	/**
	 * Increments and returns the previous value; wraps to the initial value after the maximum.
	 */
	public long getAndIncrement() {
		while (true) {
			long current = l.get();
			long next = (current >= maxValue ? initialValue : current + 1);
			if (l.compareAndSet(current, next)) {
				return current;
			}
		}
	}

	/**
	 * Decrements and returns the previous value; wraps to the maximum below the initial value.
	 */
	public long getAndDecrement() {
		while (true) {
			long current = l.get();
			long next = (current <= initialValue ? maxValue : current - 1);
			if (l.compareAndSet(current, next)) {
				return current;
			}
		}
	}

	/**
	 * Increments and returns the new value; wraps to the initial value after the maximum.
	 */
	public long incrementAndGet() {
		while (true) {
			long current = l.get();
			long next = (current >= maxValue ? initialValue : current + 1);
			if (l.compareAndSet(current, next)) {
				return next;
			}
		}
	}

	/**
	 * Decrements and returns the new value; wraps to the maximum below the initial value.
	 */
	public long decrementAndGet() {
		while (true) {
			long current = l.get();
			long next = (current <= initialValue ? maxValue : current - 1);
			if (l.compareAndSet(current, next)) {
				return next;
			}
		}
	}

	public long get() {
		return l.get();
	}

	/**
	 * Sets the value.
	 *
	 * @throws IllegalArgumentException
	 *             if the value is outside {@code [initialValue, maxValue]}
	 */
	public void set(long newValue) {
		if (newValue >= initialValue && newValue <= maxValue) {
			l.set(newValue);
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
	public long getAndSet(long newValue) {
		if (newValue >= initialValue && newValue <= maxValue) {
			return l.getAndSet(newValue);
		}
		throw new IllegalArgumentException("New value must >= " + initialValue + " and <= " + maxValue + ".");
	}

	/**
	 * Adds {@code delta} and returns the previous value; the result wraps to the initial value when
	 * it would exceed the maximum and to the maximum when it would fall below the initial value.
	 */
	public long getAndAdd(long delta) {
		while (true) {
			long current = l.get();
			long next = nextValue(current, delta);
			if (l.compareAndSet(current, next)) {
				return current;
			}
		}
	}

	/**
	 * Adds {@code delta} and returns the new value, wrapping like {@link #getAndAdd(long)}.
	 */
	public long addAndGet(long delta) {
		while (true) {
			long current = l.get();
			long next = nextValue(current, delta);
			if (l.compareAndSet(current, next)) {
				return next;
			}
		}
	}

	private long nextValue(long current, long delta) {
		if (delta >= 0) {
			return current >= maxValue - delta ? initialValue : current + delta;
		}
		long sum = current + delta;
		return sum < initialValue ? maxValue : sum;
	}

	/**
	 * Changes the maximum value; the current value is not adjusted.
	 */
	public void setMaxValue(long maxValue) {
		this.maxValue = maxValue;
	}

	/**
	 * Atomically sets the value to {@code update} if it equals {@code expect}.
	 *
	 * @throws IllegalArgumentException
	 *             if {@code update} is outside {@code [initialValue, maxValue]}
	 */
	public boolean compareAndSet(long expect, long update) {
		if (update >= initialValue && update <= maxValue) {
			return l.compareAndSet(expect, update);
		}
		throw new IllegalArgumentException("New value must >= " + initialValue + " and <= " + maxValue + ".");
	}

	public boolean weakCompareAndSet(long expect, long update) {
		if (update >= initialValue && update <= maxValue) {
			return l.weakCompareAndSet(expect, update);
		}
		throw new IllegalArgumentException("New value must >= " + initialValue + " and <= " + maxValue + ".");
	}

	public byte byteValue() {
		return l.byteValue();
	}

	public short shortValue() {
		return l.shortValue();
	}

	public int intValue() {
		return l.intValue();
	}

	public long longValue() {
		return l.longValue();
	}

	public float floatValue() {
		return l.floatValue();
	}

	public double doubleValue() {
		return l.doubleValue();
	}

	public String toString() {
		return l.toString();
	}

	public int hashCode() {
		int prime = 31;
		prime = prime + Long.hashCode(get());
		return prime;
	}

	public boolean equals(Object other) {
		if (other instanceof AtomicLongSequence) {
			AtomicLongSequence integer = (AtomicLongSequence) other;
			return integer.get() == get();
		}
		return false;
	}
}
