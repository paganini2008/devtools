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
package com.github.paganini2008.devtools.time;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

/**
 * TimeWindowMapTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class TimeWindowMapTest {

	private static final Instant BASE = Instant.parse("2021-01-31T10:00:00Z");

	private final List<Instant> checkPoints = Collections.synchronizedList(new ArrayList<>());
	private final List<List<Integer>> batches = Collections.synchronizedList(new ArrayList<>());

	private TimeWindowMap<Integer> newMap(int batchSize) {
		return new TimeWindowMap<>(1, TimeSlot.MINUTE, batchSize, (ins, values) -> {
			checkPoints.add(ins);
			batches.add(values);
		}, ZoneOffset.UTC);
	}

	@Test
	public void testBatch() {
		TimeWindowMap<Integer> map = newMap(3);
		assertEquals(1, map.getMaxSize());
		map.offer(BASE, 1);
		map.offer(BASE.plusSeconds(1), 2);
		assertTrue(batches.isEmpty());
		List<Integer> values = map.offer(BASE.plusSeconds(2).toEpochMilli(), 3);
		assertEquals(Arrays.asList(Arrays.asList(1, 2, 3)), batches);
		assertEquals(Arrays.asList(BASE), checkPoints);
		assertTrue(values.isEmpty());
		assertEquals(1, map.size());
	}

	@Test
	public void testWindowSwitchAndFlush() {
		TimeWindowMap<Integer> map = newMap(100);
		map.offer(BASE, 1);
		map.offer(BASE.plusSeconds(30), 2);
		// a new time window evicts the previous one
		map.offer(BASE.plusSeconds(60), 3);
		assertEquals(Arrays.asList(Arrays.asList(1, 2)), batches);
		assertEquals(Arrays.asList(BASE), checkPoints);
		assertEquals(1, map.size());

		// a late payload of an evicted window is delivered immediately
		List<Integer> late = map.offer(BASE.plusSeconds(10), 4);
		assertEquals(Arrays.asList(4), late);
		assertEquals(Arrays.asList(1, 2), batches.get(0));
		assertEquals(Arrays.asList(4), batches.get(1));
		assertEquals(BASE, checkPoints.get(1));

		map.flush();
		assertEquals(Arrays.asList(3), batches.get(2));
		assertEquals(BASE.plusSeconds(60), checkPoints.get(2));
	}

	@Test
	public void testClearAndEmptyFlush() {
		TimeWindowMap<Integer> map = newMap(100);
		map.flush();
		assertTrue(batches.isEmpty());
		map.offer(BASE, 1);
		assertEquals(1, map.size());
		map.clear();
		assertEquals(0, map.size());
		map.flush();
		map.offer(BASE.plusSeconds(120), 2);
		map.flush();
		assertEquals(Arrays.asList(Arrays.asList(2)), batches);
	}

	@Test
	public void testDefaultZoneAndValidation() {
		TimeWindowMap<Integer> map = new TimeWindowMap<>(1, TimeSlot.HOUR, 1, (ins, values) -> batches.add(values));
		map.offer(BASE, 7);
		assertEquals(Arrays.asList(Arrays.asList(7)), batches);
		assertThrows(IllegalArgumentException.class, () -> map.offer(null, 1));
	}

	@Test
	public void testConcurrentOffer() throws Exception {
		AtomicInteger total = new AtomicInteger();
		Map<Integer, Boolean> seen = new ConcurrentHashMap<>();
		TimeWindowMap<Integer> map = new TimeWindowMap<>(1, TimeSlot.DAY, 10, (ins, values) -> {
			total.addAndGet(values.size());
			for (Integer v : values) {
				seen.put(v, Boolean.TRUE);
			}
		}, ZoneOffset.UTC);
		int threads = 4, perThread = 1000;
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		CountDownLatch done = new CountDownLatch(threads);
		for (int t = 0; t < threads; t++) {
			final int base = t * perThread;
			executor.execute(() -> {
				try {
					for (int i = 0; i < perThread; i++) {
						map.offer(BASE, base + i);
					}
				} finally {
					done.countDown();
				}
			});
		}
		assertTrue(done.await(10, TimeUnit.SECONDS));
		executor.shutdown();
		map.flush();
		assertEquals(threads * perThread, total.get());
		assertEquals(threads * perThread, seen.size());
	}

}
