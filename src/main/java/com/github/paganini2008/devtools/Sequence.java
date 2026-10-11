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
package com.github.paganini2008.devtools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * 
 * Sequence
 * 
 * Integer and long ranges in the style of {@code range(from, to, step)}: {@code from} is inclusive,
 * {@code to} is exclusive.
 *
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class Sequence {

	/**
	 * Iterates over {@code [0, to)}.
	 */
	public static Iterable<Integer> forEach(int to) {
		return forEach(0, to);
	}

	/**
	 * Iterates over {@code [from, to)}.
	 */
	public static Iterable<Integer> forEach(int from, int to) {
		return forEach(from, to, 1);
	}

	/**
	 * Iterates over {@code [from, to)} with the given step.
	 *
	 * @throws IllegalArgumentException on iteration, if the interval is not positive while {@code from < to}
	 */
	public static Iterable<Integer> forEach(final int from, final int to, final int interval) {
		return new Iterable<Integer>() {
			public Iterator<Integer> iterator() {
				return new IntegerSequence(from, to, interval);
			}
		};
	}

	/**
	 * Returns {@code [0, to)} as a list.
	 *
	 * <pre>
	 * Sequence.range(3)        = [0, 1, 2]
	 * Sequence.range(0, 10, 3) = [0, 3, 6, 9]
	 * Sequence.range(5, 1)     = []
	 * </pre>
	 */
	public static List<Integer> range(int to) {
		return range(0, to);
	}

	/**
	 * Returns {@code [from, to)} as a list.
	 */
	public static List<Integer> range(int from, int to) {
		return range(from, to, 1);
	}

	/**
	 * Returns {@code [from, to)} with the given step as a list.
	 *
	 * @throws IllegalArgumentException if the interval is not positive while {@code from < to}
	 */
	public static List<Integer> range(int from, int to, int interval) {
		List<Integer> range = new ArrayList<Integer>();
		for (Integer i : forEach(from, to, interval)) {
			range.add(i);
		}
		return range;
	}

	/**
	 * Iterates over {@code [0, to)}.
	 */
	public static Iterable<Long> forEach(long to) {
		return forEach(0, to);
	}

	/**
	 * Iterates over {@code [from, to)}.
	 */
	public static Iterable<Long> forEach(long from, long to) {
		return forEach(from, to, 1);
	}

	/**
	 * Iterates over {@code [from, to)} with the given step.
	 *
	 * @throws IllegalArgumentException on iteration, if the interval is not positive while {@code from < to}
	 */
	public static Iterable<Long> forEach(final long from, final long to, final int interval) {
		return new Iterable<Long>() {
			public Iterator<Long> iterator() {
				return new LongSequence(from, to, interval);
			}
		};
	}

	/**
	 * Returns {@code [0, to)} as a list.
	 */
	public static List<Long> range(long to) {
		return range(0, to);
	}

	/**
	 * Returns {@code [from, to)} as a list.
	 */
	public static List<Long> range(long from, long to) {
		return range(from, to, 1);
	}

	/**
	 * Returns {@code [from, to)} with the given step as a list.
	 *
	 * @throws IllegalArgumentException if the interval is not positive while {@code from < to}
	 */
	public static List<Long> range(long from, long to, int interval) {
		List<Long> range = new ArrayList<Long>();
		for (Long l : forEach(from, to, interval)) {
			range.add(l);
		}
		return range;
	}

	static class LongSequence implements Iterator<Long> {

		private final long to;
		private final int interval;
		private long current;
		private boolean exhausted;

		LongSequence(long from, long to, int interval) {
			if (interval <= 0 && from < to) {
				throw new IllegalArgumentException("Interval must be positive: " + interval);
			}
			this.current = from;
			this.to = to;
			this.interval = interval;
		}

		public boolean hasNext() {
			return !exhausted && current < to;
		}

		public Long next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			long result = current;
			try {
				current = Math.addExact(current, interval);
			} catch (ArithmeticException e) {
				exhausted = true;
			}
			return result;
		}

	}

	static class IntegerSequence implements Iterator<Integer> {

		private final LongSequence delegate;

		IntegerSequence(int from, int to, int interval) {
			this.delegate = new LongSequence(from, to, interval);
		}

		public boolean hasNext() {
			return delegate.hasNext();
		}

		public Integer next() {
			return delegate.next().intValue();
		}

	}

}
