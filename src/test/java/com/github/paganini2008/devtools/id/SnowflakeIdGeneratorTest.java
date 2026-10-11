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
package com.github.paganini2008.devtools.id;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * SnowflakeIdGeneratorTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class SnowflakeIdGeneratorTest {

	/**
	 * Generator with a scripted clock: queued values are returned first, then {@code now}.
	 */
	static class FakeClockGenerator extends SnowflakeIdGenerator {

		final Deque<Long> script = new ArrayDeque<Long>();
		long now;
		int calls;

		FakeClockGenerator(long workerId, long datacenterId, long epoch, long now) {
			super(workerId, datacenterId, epoch);
			this.now = now;
		}

		FakeClockGenerator then(long... times) {
			for (long t : times) {
				script.add(t);
			}
			return this;
		}

		@Override
		protected long currentTimeMillis() {
			calls++;
			Long t = script.poll();
			return t != null ? t : now;
		}
	}

	@Test
	void constructor_rejectsOutOfRangeArguments() {
		assertThrows(IllegalArgumentException.class, () -> new SnowflakeIdGenerator(-1, 0));
		assertThrows(IllegalArgumentException.class, () -> new SnowflakeIdGenerator(32, 0));
		assertThrows(IllegalArgumentException.class, () -> new SnowflakeIdGenerator(0, -1));
		assertThrows(IllegalArgumentException.class, () -> new SnowflakeIdGenerator(0, 32));
		assertThrows(IllegalArgumentException.class, () -> new SnowflakeIdGenerator(0, 0, -1L));
		SnowflakeIdGenerator max = new SnowflakeIdGenerator(31, 31);
		assertEquals(31, max.getWorkerId());
		assertEquals(31, max.getDatacenterId());
		assertEquals(SnowflakeIdGenerator.DEFAULT_EPOCH, max.getEpochMillis());
		assertEquals(1577836800000L, SnowflakeIdGenerator.DEFAULT_EPOCH);
		assertEquals("SnowflakeIdGenerator[workerId=31, datacenterId=31, epochMillis=1577836800000]", max.toString());
	}

	@Test
	void nextId_encodesFieldsInStandardLayout() {
		FakeClockGenerator gen = new FakeClockGenerator(3, 7, 1000L, 1000L + 12345L);
		long id = gen.nextId();
		assertEquals((12345L << 22) | (7L << 17) | (3L << 12), id);
		assertEquals(id + 1, gen.generateId().longValue());
		SnowflakeId sid = SnowflakeIdGenerator.parse(id + 1, 1000L);
		assertEquals(id + 1, sid.getId());
		assertEquals(1000L + 12345L, sid.getTimestamp());
		assertEquals(7, sid.getDatacenterId());
		assertEquals(3, sid.getWorkerId());
		assertEquals(1, sid.getSequence());
	}

	@Test
	void parse_roundTripsRealIds() {
		SnowflakeIdGenerator gen = new SnowflakeIdGenerator(17, 29);
		long before = System.currentTimeMillis();
		long id = gen.nextId();
		long after = System.currentTimeMillis();
		assertTrue(id > 0);
		SnowflakeId sid = SnowflakeIdGenerator.parse(id);
		assertEquals(17, sid.getWorkerId());
		assertEquals(29, sid.getDatacenterId());
		assertEquals(0, sid.getSequence());
		assertTrue(sid.getTimestamp() >= before && sid.getTimestamp() <= after);
		assertThrows(IllegalArgumentException.class, () -> SnowflakeIdGenerator.parse(-1L));
	}

	@Test
	void parse_maxValues() {
		SnowflakeId sid = SnowflakeIdGenerator.parse(Long.MAX_VALUE, 0L);
		assertEquals(SnowflakeIdGenerator.MAX_TIMESTAMP, sid.getTimestamp());
		assertEquals(31, sid.getDatacenterId());
		assertEquals(31, sid.getWorkerId());
		assertEquals(4095, sid.getSequence());
	}

	@Test
	void snowflakeId_valueSemantics() {
		SnowflakeId a = new SnowflakeId(1L, 2L, 3L, 4L, 5L);
		assertEquals(a, a);
		assertEquals(a, new SnowflakeId(1L, 2L, 3L, 4L, 5L));
		assertEquals(a.hashCode(), new SnowflakeId(1L, 2L, 3L, 4L, 5L).hashCode());
		assertNotEquals(a, new SnowflakeId(9L, 2L, 3L, 4L, 5L));
		assertNotEquals(a, new SnowflakeId(1L, 9L, 3L, 4L, 5L));
		assertNotEquals(a, new SnowflakeId(1L, 2L, 9L, 4L, 5L));
		assertNotEquals(a, new SnowflakeId(1L, 2L, 3L, 9L, 5L));
		assertNotEquals(a, new SnowflakeId(1L, 2L, 3L, 4L, 9L));
		assertNotEquals(a, "x");
		assertEquals("SnowflakeId[id=1, timestamp=2, datacenterId=3, workerId=4, sequence=5]", a.toString());
	}

	@Test
	void sequenceResetsOnNewMillisecond() {
		FakeClockGenerator gen = new FakeClockGenerator(0, 0, 0L, 1000L);
		gen.nextId();
		gen.nextId();
		gen.now = 1001L;
		long id = gen.nextId();
		assertEquals(0, SnowflakeIdGenerator.parse(id, 0L).getSequence());
		assertEquals(1001L, SnowflakeIdGenerator.parse(id, 0L).getTimestamp());
	}

	@Test
	void sequenceExhaustion_spinsToNextMillisecond() {
		FakeClockGenerator gen = new FakeClockGenerator(1, 1, 0L, 1000L);
		long last = -1;
		for (int i = 0; i <= SnowflakeIdGenerator.MAX_SEQUENCE; i++) {
			long id = gen.nextId();
			assertTrue(id > last);
			last = id;
		}
		SnowflakeId full = SnowflakeIdGenerator.parse(last, 0L);
		assertEquals(4095, full.getSequence());
		assertEquals(1000L, full.getTimestamp());
		// next call: clock still at 1000 twice while spinning, then advances
		gen.then(1000L, 1000L, 1000L, 1001L);
		gen.now = 1001L;
		int callsBefore = gen.calls;
		long id = gen.nextId();
		assertTrue(id > last);
		SnowflakeId next = SnowflakeIdGenerator.parse(id, 0L);
		assertEquals(1001L, next.getTimestamp());
		assertEquals(0, next.getSequence());
		assertEquals(4, gen.calls - callsBefore);
	}

	@Test
	void clockRollbackWithinTolerance_waitsUntilCaughtUp() {
		FakeClockGenerator gen = new FakeClockGenerator(0, 0, 0L, 1000L);
		long first = gen.nextId();
		// clock jumps back 5 ms, then catches up step by step
		gen.then(995L, 996L, 998L, 999L, 1000L);
		long second = gen.nextId();
		assertTrue(second > first);
		SnowflakeId sid = SnowflakeIdGenerator.parse(second, 0L);
		assertEquals(1000L, sid.getTimestamp());
		assertEquals(1, sid.getSequence());
		assertTrue(gen.script.isEmpty());
	}

	@Test
	void clockRollbackBeyondTolerance_throws() {
		FakeClockGenerator gen = new FakeClockGenerator(0, 0, 0L, 1000L);
		gen.nextId();
		gen.then(994L);
		IllegalStateException e = assertThrows(IllegalStateException.class, gen::nextId);
		assertTrue(e.getMessage().contains("Clock moved backwards by 6 ms"), e.getMessage());
		// generator recovers once the clock is back
		long id = gen.nextId();
		assertEquals(1, SnowflakeIdGenerator.parse(id, 0L).getSequence());
	}

	@Test
	void clockRollingFurtherBackWhileWaiting_throws() {
		FakeClockGenerator gen = new FakeClockGenerator(0, 0, 0L, 1000L);
		gen.nextId();
		gen.then(998L, 999L, 990L);
		IllegalStateException e = assertThrows(IllegalStateException.class, gen::nextId);
		assertTrue(e.getMessage().contains("10 ms"), e.getMessage());
	}

	@Test
	void timeOutsideRange_throws() {
		FakeClockGenerator beforeEpoch = new FakeClockGenerator(0, 0, 5000L, 1000L);
		assertThrows(IllegalStateException.class, beforeEpoch::nextId);
		FakeClockGenerator overflow = new FakeClockGenerator(0, 0, 0L, SnowflakeIdGenerator.MAX_TIMESTAMP + 1);
		assertThrows(IllegalStateException.class, overflow::nextId);
		FakeClockGenerator edge = new FakeClockGenerator(31, 31, 0L, SnowflakeIdGenerator.MAX_TIMESTAMP);
		long id = edge.nextId();
		assertTrue(id > 0);
		assertEquals(Long.MAX_VALUE - 4095, id);
	}

	@Test
	@Timeout(value = 60, unit = TimeUnit.SECONDS)
	void concurrentGeneration_isUniqueAndMonotonicPerThread() throws Exception {
		final int threads = 8;
		final int perThread = 100000;
		final SnowflakeIdGenerator gen = new SnowflakeIdGenerator(1, 2);
		final long[][] results = new long[threads][perThread];
		final CountDownLatch start = new CountDownLatch(1);
		final CountDownLatch done = new CountDownLatch(threads);
		final AtomicReference<Throwable> error = new AtomicReference<Throwable>();
		for (int t = 0; t < threads; t++) {
			final long[] out = results[t];
			Thread thread = new Thread(() -> {
				try {
					start.await();
					for (int i = 0; i < perThread; i++) {
						out[i] = gen.nextId();
					}
				} catch (Throwable e) {
					error.compareAndSet(null, e);
				} finally {
					done.countDown();
				}
			});
			thread.start();
		}
		start.countDown();
		assertTrue(done.await(50, TimeUnit.SECONDS));
		if (error.get() != null) {
			throw new AssertionError(error.get());
		}
		long[] all = new long[threads * perThread];
		for (int t = 0; t < threads; t++) {
			long[] out = results[t];
			for (int i = 1; i < perThread; i++) {
				assertTrue(out[i] > out[i - 1], "not monotonic in thread " + t);
			}
			System.arraycopy(out, 0, all, t * perThread, perThread);
		}
		Arrays.sort(all);
		for (int i = 1; i < all.length; i++) {
			assertTrue(all[i] != all[i - 1], "duplicate id " + all[i]);
		}
		SnowflakeId sid = SnowflakeIdGenerator.parse(all[all.length - 1]);
		assertEquals(1, sid.getWorkerId());
		assertEquals(2, sid.getDatacenterId());
	}

}
