package com.github.paganini2008.devtools.multithreads;

import java.util.concurrent.atomic.AtomicLong;

/**
 * AtomicPositiveLong
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class AtomicPositiveLong extends Number {

	private static final long serialVersionUID = -2149027152615605579L;

	private final AtomicLong l;

	private final long maxValue;
	private final long initialValue;

	public AtomicPositiveLong() {
		this(0);
	}

	public AtomicPositiveLong(long initialValue) {
		this(initialValue, Long.MAX_VALUE);
	}

	public AtomicPositiveLong(long initialValue, long maxValue) {
		l = new AtomicLong(initialValue);
		this.initialValue = initialValue;
		this.maxValue = maxValue;
	}

	public final long getAndIncrement() {
		while (true) {
			long current = l.get();
			long next = (current >= maxValue ? initialValue : current + 1);
			if (l.compareAndSet(current, next)) {
				return current;
			}
		}
	}

	public final long getAndDecrement() {
		while (true) {
			long current = l.get();
			long next = (current <= initialValue ? maxValue : current - 1);
			if (l.compareAndSet(current, next)) {
				return current;
			}
		}
	}

	public final long incrementAndGet() {
		while (true) {
			long current = l.get();
			long next = (current >= maxValue ? initialValue : current + 1);
			if (l.compareAndSet(current, next)) {
				return next;
			}
		}
	}

	public final long decrementAndGet() {
		while (true) {
			long current = l.get();
			long next = (current <= initialValue ? maxValue : current - 1);
			if (l.compareAndSet(current, next)) {
				return next;
			}
		}
	}

	public final long get() {
		return l.get();
	}

	public final void set(long newValue) {
		if (newValue >= initialValue && newValue <= maxValue) {
			l.set(newValue);
		} else {
			throw new IllegalArgumentException("New value must >= " + initialValue + " and <= " + maxValue + ".");
		}
	}

	public final long getAndSet(long newValue) {
		if (newValue >= initialValue && newValue <= maxValue) {
			return l.getAndSet(newValue);
		}
		throw new IllegalArgumentException("New value must >= " + initialValue + " and <= " + maxValue + ".");
	}

	public final long getAndAdd(long delta) {
		while (true) {
			long current = l.get();
			long next = (current >= maxValue - delta ? initialValue : current + delta);
			if (l.compareAndSet(current, next)) {
				return current;
			}
		}
	}

	public final long addAndGet(long delta) {
		while (true) {
			long current = l.get();
			long next = (current >= maxValue - delta ? initialValue : current + delta);
			if (l.compareAndSet(current, next)) {
				return next;
			}
		}
	}

	public final boolean compareAndSet(long expect, long update) {
		if (update >= initialValue && update <= maxValue) {
			return l.compareAndSet(expect, update);
		}
		throw new IllegalArgumentException("New value must >= " + initialValue + " and <= " + maxValue + ".");
	}

	public final boolean weakCompareAndSet(long expect, long update) {
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
		int result = 37;
		result = result + Long.hashCode(get());
		return result;
	}

	public boolean equals(Object other) {
		if (other instanceof AtomicPositiveLong) {
			AtomicPositiveLong integer = (AtomicPositiveLong) other;
			return integer.get() == get();
		}
		return false;
	}
}
