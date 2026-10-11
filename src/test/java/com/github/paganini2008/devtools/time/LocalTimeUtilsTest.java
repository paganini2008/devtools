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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;

/**
 * LocalTimeUtilsTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class LocalTimeUtilsTest {

	private static final ZoneId UTC = ZoneOffset.UTC;
	private static final LocalTime T = LocalTime.of(10, 20, 30);
	private static final Instant INS = Instant.parse("2021-01-31T10:20:30Z");

	@Test
	public void testToLocalTime() {
		assertEquals(T, LocalTimeUtils.toLocalTime(INS, UTC));
		assertEquals(T.plusHours(8), LocalTimeUtils.toLocalTime(INS, ZoneId.of("Asia/Shanghai")));
		assertEquals(T, LocalTimeUtils.toLocalTime(INS.toEpochMilli(), UTC));
		assertEquals(T, LocalTimeUtils.toLocalTime(Date.from(INS), UTC));
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		c.setTime(Date.from(INS));
		assertEquals(T, LocalTimeUtils.toLocalTime(c, UTC));
		assertEquals(T, LocalTimeUtils.toLocalTime(LocalDateTime.of(2021, 1, 1, 10, 20, 30), null));
		assertEquals(INS.atZone(ZoneId.systemDefault()).toLocalTime(), LocalTimeUtils.toLocalTime(INS, null));

		assertNull(LocalTimeUtils.toLocalTime((Long) null, UTC));
		assertNull(LocalTimeUtils.toLocalTime((Instant) null, UTC));
		assertNull(LocalTimeUtils.toLocalTime((Date) null, UTC));
		assertNull(LocalTimeUtils.toLocalTime((Calendar) null, UTC));
		assertEquals(T, LocalTimeUtils.toLocalTime((LocalDateTime) null, T));
		assertEquals(T, LocalTimeUtils.toLocalTime((Long) null, UTC, T));
		assertEquals(T, LocalTimeUtils.toLocalTime((Date) null, UTC, T));
		assertEquals(T, LocalTimeUtils.toLocalTime((Calendar) null, UTC, T));
		assertEquals(T, LocalTimeUtils.toLocalTime(Instant.MAX, ZoneOffset.ofHours(18), T));
	}

	@Test
	public void testParse() {
		assertEquals(T, LocalTimeUtils.parseLocalTime("10:20:30"));
		assertEquals(T, LocalTimeUtils.parseLocalTime("10:20:30", (DateTimeFormatter) null));
		assertEquals(T, LocalTimeUtils.parseLocalTime("10.20.30", "HH.mm.ss"));
		assertEquals(T, LocalTimeUtils.parseLocalTime("10.20.30", "HH.mm.ss"));
		assertNull(LocalTimeUtils.parseLocalTime("25:00"));
		assertNull(LocalTimeUtils.parseLocalTime(null));
		assertEquals(T, LocalTimeUtils.parseLocalTime("bad", "HH:mm", T));
		assertThrows(IllegalArgumentException.class, () -> LocalTimeUtils.parseLocalTime("10:00", (String) null));
	}

	@Test
	public void testOf() {
		assertEquals(LocalTime.of(10, 20), LocalTimeUtils.of(10, 20));
		assertEquals(T, LocalTimeUtils.of(10, 20, 30));
		assertThrows(IllegalArgumentException.class, () -> LocalTimeUtils.of(24, 0));
		assertThrows(IllegalArgumentException.class, () -> LocalTimeUtils.of(10, 0, 60));
	}

	@Test
	public void testCopy() {
		assertSame(T, LocalTimeUtils.copy(T, UTC));
		assertEquals(T, LocalTimeUtils.copy(T, null));
		assertNotNull(LocalTimeUtils.copy(null, UTC));
	}

	private static <T> List<T> toList(Iterator<T> it) {
		List<T> list = new ArrayList<>();
		while (it.hasNext()) {
			list.add(it.next());
		}
		return list;
	}

	@Test
	public void testIterator() {
		List<LocalTime> expected = Arrays.asList(LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0));
		assertEquals(expected, toList(LocalTimeUtils.toIterator(LocalTime.of(10, 0), LocalTime.of(11, 15), UTC, 30, ChronoUnit.MINUTES)));
		assertEquals(expected, toList(LocalTimeUtils.toIterator("10:00", "11:15", null, UTC, 30, ChronoUnit.MINUTES)));
		assertEquals(expected, toList(LocalTimeUtils.toIterator(Date.from(Instant.parse("2021-01-01T10:00:00Z")),
				Date.from(Instant.parse("2021-01-01T11:15:00Z")), UTC, 30, ChronoUnit.MINUTES)));

		// times wrap around midnight as before; a step cycling back to the start stops instead of looping forever
		assertEquals(hours(22, 1, 4, 7, 10, 13, 16, 19),
				toList(LocalTimeUtils.toIterator(LocalTime.of(22, 0), LocalTime.of(23, 59), UTC, 3, ChronoUnit.HOURS)));
		// an iteration that ended after wrapping keeps its original result
		assertEquals(hours(20, 1, 6, 11, 16, 21, 2, 7, 12, 17),
				toList(LocalTimeUtils.toIterator(LocalTime.of(20, 0), LocalTime.of(22, 0), UTC, 5, ChronoUnit.HOURS)));
		assertEquals(hours(10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0),
				toList(LocalTimeUtils.toIterator(LocalTime.of(10, 0), LocalTime.of(11, 0), UTC, -1, ChronoUnit.HOURS)));
		assertEquals(hours(10), toList(LocalTimeUtils.toIterator(LocalTime.of(10, 0), LocalTime.of(11, 0), UTC, 0, ChronoUnit.HOURS)));

		Iterator<LocalTime> it = LocalTimeUtils.toIterator(T, T, UTC, 1, ChronoUnit.HOURS);
		assertFalse(it.hasNext());
	}

	private static List<LocalTime> hours(int... hours) {
		List<LocalTime> list = new ArrayList<>();
		for (int h : hours) {
			list.add(LocalTime.of(h, 0));
		}
		return list;
	}

}
