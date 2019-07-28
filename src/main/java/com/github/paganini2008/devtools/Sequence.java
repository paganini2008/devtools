package com.github.paganini2008.devtools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.github.paganini2008.devtools.multithreads.Producer;

/**
 * 
 * Sequence
 * 
 * @author Fred Feng
 * @revised 2019-06
 * @created 2018-05
 * @version 1.0
 */
public abstract class Sequence {

	public static Iterable<Integer> forEach(int from, int to) {
		return forEach(from, to, 1);
	}

	public static Iterable<Integer> forEach(final int from, final int to, final int interval) {
		return new Iterable<Integer>() {
			public Iterator<Integer> iterator() {
				return new IntegerSequence(from, to, interval);
			}
		};
	}

	public static List<Integer> range(int from, int to) {
		return range(from, to, 1);
	}

	public static List<Integer> range(int from, int to, int interval) {
		List<Integer> range = new ArrayList<Integer>();
		for (Integer i : forEach(from, to, interval)) {
			range.add(i);
		}
		return range;
	}

	public static Iterable<Long> forEach(long from, long to) {
		return forEach(from, to, 1);
	}

	public static Iterable<Long> forEach(final long from, final long to, final int interval) {
		return new Iterable<Long>() {
			public Iterator<Long> iterator() {
				return new LongSequence(from, to, interval);
			}
		};
	}

	public static List<Long> range(long from, long to) {
		return range(from, to, 1);
	}

	public static List<Long> range(long from, long to, int interval) {
		List<Long> range = new ArrayList<Long>();
		for (Long l : forEach(from, to, interval)) {
			range.add(l);
		}
		return range;
	}

	static class LongSequence implements Iterator<Long> {

		private final AtomicLong counter;
		private final long to;
		private final int interval;

		LongSequence(long from, long to, int interval) {
			this.counter = new AtomicLong(from);
			this.to = to;
			this.interval = interval;
		}

		public boolean hasNext() {
			return counter.get() < to;
		}

		public Long next() {
			return counter.getAndAdd(interval);
		}

	}

	static class IntegerSequence implements Iterator<Integer> {

		private final AtomicInteger counter;
		private final int to;
		private final int interval;

		IntegerSequence(int from, int to, int interval) {
			this.counter = new AtomicInteger(from);
			this.to = to;
			this.interval = interval;
		}

		public boolean hasNext() {
			return counter.get() < to;
		}

		public Integer next() {
			return counter.getAndAdd(interval);
		}

	}
	
}
