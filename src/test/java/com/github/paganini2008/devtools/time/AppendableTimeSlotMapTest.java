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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

/**
 * AppendableTimeSlotMapTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class AppendableTimeSlotMapTest {

	private static final ZoneId UTC = ZoneOffset.UTC;
	private static final Instant BASE = Instant.parse("2021-01-31T10:00:00Z");

	@Test
	public void testAppend() {
		AppendableTimeSlotMap<String> map = new AppendableTimeSlotMap<>(new ConcurrentHashMap<>(), 5, TimeSlot.MINUTE, UTC);
		assertEquals(5, map.getSpan());
		assertSame(TimeSlot.MINUTE, map.getTimeSlot());
		assertEquals(UTC, map.getZoneId());
		map.append(BASE.plusSeconds(10), "a");
		map.append(BASE.plusSeconds(290), "b");
		map.appendAll(BASE.plusSeconds(300), Arrays.asList("c", "d"));
		map.append(BASE.plusSeconds(310), "e", () -> new ArrayList<>());
		map.appendAll(BASE.plusSeconds(1), Arrays.asList("f"), () -> new ArrayList<>());
		assertEquals(2, map.size());
		assertEquals(Arrays.asList("a", "b", "f"), map.get(BASE.plusSeconds(100)));
		assertEquals(Arrays.asList("c", "d", "e"), map.get(BASE.plusSeconds(599)));
		assertEquals(BASE.plusSeconds(300), map.mutate(BASE.plusSeconds(599)));

		Map<LocalDateTime, List<String>> output = map.output(UTC);
		assertEquals(Arrays.asList(LocalDateTime.of(2021, 1, 31, 10, 0), LocalDateTime.of(2021, 1, 31, 10, 5)),
				new ArrayList<>(output.keySet()));
	}

	@Test
	public void testDefaultZone() {
		AppendableTimeSlotMap<String> map = new AppendableTimeSlotMap<>(1, TimeSlot.HOUR);
		assertEquals(ZoneId.systemDefault(), map.getZoneId());
		map.append(BASE, "a");
		assertEquals(TimeSlot.HOUR.locate(BASE, 1).atZone(ZoneId.systemDefault()).toInstant(), map.keySet().iterator().next());
		// the span is ignored by TimeSlot.DAY, so any span keeps working
		AppendableTimeSlotMap<String> daily = new AppendableTimeSlotMap<>(new ConcurrentHashMap<>(), 0, TimeSlot.DAY, UTC);
		daily.append(BASE, "x");
		assertEquals(Instant.parse("2021-01-31T00:00:00Z"), daily.keySet().iterator().next());
	}

	@Test
	public void testConcurrentAppend() throws Exception {
		AppendableTimeSlotMap<Integer> map = new AppendableTimeSlotMap<>(new ConcurrentHashMap<>(), 1, TimeSlot.DAY, UTC);
		int threads = 8, perThread = 500;
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		CountDownLatch start = new CountDownLatch(1);
		CountDownLatch done = new CountDownLatch(threads);
		for (int t = 0; t < threads; t++) {
			executor.execute(() -> {
				try {
					start.await();
					for (int i = 0; i < perThread; i++) {
						map.append(BASE.plusSeconds(i), i);
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} finally {
					done.countDown();
				}
			});
		}
		start.countDown();
		assertTrue(done.await(10, TimeUnit.SECONDS));
		executor.shutdown();
		assertEquals(1, map.size());
		assertEquals(threads * perThread, map.get(BASE).size());
	}

}
