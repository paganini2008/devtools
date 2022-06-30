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
package com.github.paganini2008.devtools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 * Sequence
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public abstract class Sequence {

	public static Iterable<Integer> forEach(int to) {
		return forEach(0, to);
	}

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

	public static List<Integer> range(int to) {
		return range(0, to);
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

	public static Iterable<Long> forEach(long to) {
		return forEach(0, to);
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

	public static List<Long> range(long to) {
		return range(0, to);
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
