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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.Test;

/**
 * MergeableTimeSlotMapTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class MergeableTimeSlotMapTest {

	private static final ZoneId UTC = ZoneOffset.UTC;
	private static final Instant BASE = Instant.parse("2021-01-31T10:00:00Z");

	static class Counter implements MergeableFunction<Counter> {

		final long count;

		Counter(long count) {
			this.count = count;
		}

		@Override
		public Counter merge(Counter update) {
			return new Counter(count + update.count);
		}
	}

	@Test
	public void testMerge() {
		MergeableTimeSlotMap<Counter> map = new MergeableTimeSlotMap<>(new ConcurrentHashMap<>(), 1, TimeSlot.MINUTE, UTC);
		assertEquals(1, map.getSpan());
		assertSame(TimeSlot.MINUTE, map.getTimeSlot());
		assertEquals(UTC, map.getZoneId());
		map.merge(BASE.plusSeconds(1), new Counter(1));
		map.merge(BASE.plusSeconds(30), new Counter(2));
		Counter c = map.merge(BASE.plusSeconds(61), new Counter(5));
		assertEquals(5, c.count);
		assertEquals(3, map.get(BASE).count);
		assertEquals(2, map.size());

		Map<Instant, Counter> sorted = map.toMap();
		assertEquals(Arrays.asList(BASE, BASE.plusSeconds(60)), new ArrayList<>(sorted.keySet()));
		Map<LocalDateTime, Counter> output = map.output(UTC);
		assertEquals(Arrays.asList(LocalDateTime.of(2021, 1, 31, 10, 0), LocalDateTime.of(2021, 1, 31, 10, 1)),
				new ArrayList<>(output.keySet()));
	}

	@Test
	public void testDefaultZoneAndValidation() {
		MergeableTimeSlotMap<Counter> map = new MergeableTimeSlotMap<>(1, TimeSlot.HOUR);
		assertEquals(ZoneId.systemDefault(), map.getZoneId());
		map.merge(BASE, new Counter(1));
		assertEquals(TimeSlot.HOUR.locate(BASE, 1).atZone(ZoneId.systemDefault()).toInstant(), map.mutate(BASE));
		MergeableTimeSlotMap<Counter> daily = new MergeableTimeSlotMap<>(new ConcurrentHashMap<>(), 0, TimeSlot.DAY, UTC);
		daily.merge(BASE, new Counter(4));
		assertEquals(4, daily.get(Instant.parse("2021-01-31T23:00:00Z")).count);
	}

	@Test
	public void testDailyMap() {
		DailyMergeableTimeSlotMap<Counter> map = new DailyMergeableTimeSlotMap<>(1, TimeSlot.HOUR, 1, UTC);
		for (int i = 0; i < 30; i++) {
			map.merge(BASE.plusSeconds(i * 3600L), new Counter(1));
		}
		// only the latest 24 hourly slots are kept
		assertEquals(24, map.size());
		assertTrue(map.toMap().keySet().iterator().next().equals(BASE.plusSeconds(6 * 3600L)));
		DailyMergeableTimeSlotMap<Counter> map2 = new DailyMergeableTimeSlotMap<>(30, TimeSlot.MINUTE);
		map2.merge(BASE, new Counter(2));
		assertEquals(2, map2.get(BASE).count);
	}

}
