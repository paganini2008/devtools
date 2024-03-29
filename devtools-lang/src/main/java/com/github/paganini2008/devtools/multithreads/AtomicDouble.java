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

import static java.lang.Double.doubleToRawLongBits;
import static java.lang.Double.longBitsToDouble;

import java.util.concurrent.atomic.AtomicLongFieldUpdater;

/**
 * 
 * AtomicDouble
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public class AtomicDouble extends Number {

	private static final long serialVersionUID = -5035478790557265664L;
	private static final AtomicLongFieldUpdater<AtomicDouble> updater = AtomicLongFieldUpdater.newUpdater(AtomicDouble.class, "value");

	private volatile long value;
	private final long maxValue;
	private final double initialValue;

	public AtomicDouble() {
		this(0);
	}

	public AtomicDouble(double initialValue) {
		this(initialValue, Long.MAX_VALUE);
	}

	public AtomicDouble(double initialValue, long maxValue) {
		value = doubleToRawLongBits(initialValue);
		this.initialValue = initialValue;
		this.maxValue = maxValue;
	}

	public double get() {
		return longBitsToDouble(value);
	}

	public void set(double newValue) {
		if (newValue >= initialValue && newValue <= maxValue) {
			long next = doubleToRawLongBits(newValue);
			value = next;
		}
		throw new IllegalArgumentException("New value must >= " + initialValue + " and <= " + maxValue + ".");
	}

	public double getAndSet(double newValue) {
		long next = doubleToRawLongBits(newValue);
		return longBitsToDouble(updater.getAndSet(this, next));
	}

	public boolean compareAndSet(double expect, double update) {
		return updater.compareAndSet(this, doubleToRawLongBits(expect), doubleToRawLongBits(update));
	}

	public boolean weakCompareAndSet(double expect, double update) {
		return updater.weakCompareAndSet(this, doubleToRawLongBits(expect), doubleToRawLongBits(update));
	}

	public double getAndAdd(double delta) {
		while (true) {
			long current = value;
			double currentVal = longBitsToDouble(current);
			double nextVal = currentVal >= maxValue - delta ? initialValue : currentVal + delta;
			long next = doubleToRawLongBits(nextVal);
			if (updater.compareAndSet(this, current, next)) {
				return currentVal;
			}
		}
	}

	public double addAndGet(double delta) {
		while (true) {
			long current = value;
			double currentVal = longBitsToDouble(current);
			double nextVal = currentVal >= maxValue - delta ? initialValue : currentVal + delta;
			long next = doubleToRawLongBits(nextVal);
			if (updater.compareAndSet(this, current, next)) {
				return nextVal;
			}
		}
	}

	public String toString() {
		return Double.toString(get());
	}

	public int hashCode() {
		int result = 37;
		result = result + Double.hashCode(get());
		return result;
	}

	public boolean equals(Object other) {
		if (other instanceof AtomicLongSequence) {
			AtomicLongSequence integer = (AtomicLongSequence) other;
			return integer.get() == get();
		}
		return false;
	}

	public int intValue() {
		return (int) get();
	}

	public long longValue() {
		return (long) get();
	}

	public float floatValue() {
		return (float) get();
	}

	public double doubleValue() {
		return get();
	}
}
