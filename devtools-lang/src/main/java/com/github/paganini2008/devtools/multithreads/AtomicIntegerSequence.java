package com.github.paganini2008.devtools.multithreads;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * AtomicIntegerSequence
 * 
 * @author Fred Feng
 *
 * @since 1.0
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
