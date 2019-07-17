package com.github.paganini2008.devtools.multithreads;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * AtomicPositiveInteger
 * 
 * @author Fred Feng
 * @created 2014-03
 * @version 1.0
 */
public class AtomicPositiveInteger extends Number {

	private static final long serialVersionUID = 6144822865358352926L;

	private final AtomicInteger i;

	private final int initialValue;
	private final int maxValue;

	public AtomicPositiveInteger() {
		this(0);
	}

	public AtomicPositiveInteger(int initialValue) {
		this(initialValue, Integer.MAX_VALUE);
	}

	public AtomicPositiveInteger(int initialValue, int maxValue) {
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
		int result = 37;
		result = result + Integer.hashCode(get());
		return result;
	}

	public boolean equals(Object other) {
		if (other instanceof AtomicPositiveInteger) {
			AtomicPositiveInteger integer = (AtomicPositiveInteger) other;
			return integer.get() == get();
		}
		return false;
	}
}
