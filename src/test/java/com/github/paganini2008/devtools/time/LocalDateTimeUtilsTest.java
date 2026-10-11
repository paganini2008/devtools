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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
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
 * LocalDateTimeUtilsTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class LocalDateTimeUtilsTest {

	private static final ZoneId UTC = ZoneOffset.UTC;
	private static final LocalDateTime T = LocalDateTime.of(2021, 1, 31, 10, 20, 30);

	private static Date date(LocalDateTime ldt) {
		return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
	}

	private static void assertNear(LocalDateTime expected, LocalDateTime actual) {
		long diff = Math.abs(ChronoUnit.MILLIS.between(expected, actual));
		assertTrue(diff < 60000, "Expected about " + expected + " but was " + actual);
	}

	@Test
	public void testAdd() {
		assertEquals(T.plusSeconds(5), LocalDateTimeUtils.addSeconds(T, 5));
		assertEquals(T.plusSeconds(5), LocalDateTimeUtils.addSeconds(date(T), 5));
		assertEquals(T.plusMinutes(5), LocalDateTimeUtils.addMinutes(T, 5));
		assertEquals(T.plusMinutes(5), LocalDateTimeUtils.addMinutes(date(T), 5));
		assertEquals(T.plusHours(5), LocalDateTimeUtils.addHours(T, 5));
		assertEquals(T.plusHours(5), LocalDateTimeUtils.addHours(date(T), 5));
		assertEquals(T.plusDays(5), LocalDateTimeUtils.addDays(T, 5));
		assertEquals(T.plusDays(5), LocalDateTimeUtils.addDays(date(T), 5));
		assertEquals(LocalDateTime.of(2021, 2, 28, 10, 20, 30), LocalDateTimeUtils.addMonths(T, 1));
		assertEquals(LocalDateTime.of(2021, 2, 28, 10, 20, 30), LocalDateTimeUtils.addMonths(date(T), 1));
		assertEquals(T.plusYears(2), LocalDateTimeUtils.addYears(T, 2));
		assertEquals(T.plusYears(2), LocalDateTimeUtils.addYears(date(T), 2));
		assertEquals(T.plusWeeks(2), LocalDateTimeUtils.addWeeks(T, 2));
		assertEquals(T.plusWeeks(2), LocalDateTimeUtils.addWeeks(date(T), 2));
	}

	@Test
	public void testAddRelativeToNow() {
		assertNear(LocalDateTime.now().plusSeconds(1), LocalDateTimeUtils.addSeconds(1));
		assertNear(LocalDateTime.now().plusMinutes(1), LocalDateTimeUtils.addMinutes(1));
		assertNear(LocalDateTime.now().plusHours(1), LocalDateTimeUtils.addHours(1));
		assertNear(LocalDateTime.now().plusDays(1), LocalDateTimeUtils.addDays(1));
		assertNear(LocalDateTime.now().plusMonths(1), LocalDateTimeUtils.addMonths(1));
		assertNear(LocalDateTime.now().plusYears(1), LocalDateTimeUtils.addYears(1));
		assertNear(LocalDateTime.now().plusWeeks(1), LocalDateTimeUtils.addWeeks(1));
		assertNear(LocalDateTime.now().plusDays(2), LocalDateTimeUtils.addAmount(null, 2, ChronoUnit.DAYS));
	}

	@Test
	public void testGet() {
		assertEquals(2021, LocalDateTimeUtils.getYear(T));
		assertEquals(1, LocalDateTimeUtils.getMonth(T));
		assertEquals(31, LocalDateTimeUtils.getDayOfMonth(T));
		assertEquals(7, LocalDateTimeUtils.getDayOfWeek(T));
		assertEquals(31, LocalDateTimeUtils.getDayOfYear(T));
		assertEquals(10, LocalDateTimeUtils.getHourOfDay(T));
		assertEquals(20, LocalDateTimeUtils.getMinute(T));
		assertEquals(30, LocalDateTimeUtils.getSecond(T));
		int year = LocalDateTimeUtils.getField(null, ChronoField.YEAR);
		assertTrue(year >= 2020);
	}

	@Test
	public void testSet() {
		assertEquals(T.withYear(2000), LocalDateTimeUtils.setYear(T, 2000));
		assertEquals(T.withMonth(4).withDayOfMonth(30), LocalDateTimeUtils.setMonth(T, 4));
		assertEquals(T.withDayOfMonth(2), LocalDateTimeUtils.setDayOfMonth(T, 2));
		assertEquals(T.withDayOfMonth(25), LocalDateTimeUtils.setDayOfWeek(T, 1));
		assertEquals(T.withDayOfYear(40), LocalDateTimeUtils.setDayOfYear(T, 40));
		assertEquals(T.withHour(1), LocalDateTimeUtils.setHourOfDay(T, 1));
		assertEquals(T.withMinute(1), LocalDateTimeUtils.setMinute(T, 1));
		assertEquals(T.withSecond(1), LocalDateTimeUtils.setSecond(T, 1));

		assertEquals(2000, LocalDateTimeUtils.setYear(2000).getYear());
		assertEquals(2, LocalDateTimeUtils.setMonth(2).getMonthValue());
		assertEquals(1, LocalDateTimeUtils.setDayOfMonth(1).getDayOfMonth());
		assertEquals(3, LocalDateTimeUtils.setDayOfWeek(3).getDayOfWeek().getValue());
		assertEquals(10, LocalDateTimeUtils.setDayOfYear(10).getDayOfYear());
		assertEquals(5, LocalDateTimeUtils.setHourOfDay(5).getHour());
		assertEquals(6, LocalDateTimeUtils.setMinute(6).getMinute());
		assertEquals(7, LocalDateTimeUtils.setSecond(7).getSecond());
		assertEquals(8, LocalDateTimeUtils.setField(null, ChronoField.HOUR_OF_DAY, 8).getHour());
	}

	@Test
	public void testToLocalDateTime() {
		Instant ins = Instant.parse("2021-01-31T10:20:30Z");
		assertEquals(T, LocalDateTimeUtils.toLocalDateTime(ins, UTC));
		assertEquals(T.plusHours(8), LocalDateTimeUtils.toLocalDateTime(ins, ZoneId.of("Asia/Shanghai")));
		assertEquals(T, LocalDateTimeUtils.toLocalDateTime(ins.toEpochMilli(), UTC));
		assertEquals(T, LocalDateTimeUtils.toLocalDateTime(Date.from(ins), UTC));
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		c.setTime(Date.from(ins));
		assertEquals(T, LocalDateTimeUtils.toLocalDateTime(c, UTC));
		assertEquals(ins.atZone(ZoneId.systemDefault()).toLocalDateTime(), LocalDateTimeUtils.toLocalDateTime(ins, null));
		assertEquals(LocalDateTime.of(2021, 1, 31, 0, 0), LocalDateTimeUtils.toLocalDateTime(LocalDate.of(2021, 1, 31)));

		assertNull(LocalDateTimeUtils.toLocalDateTime((Long) null, UTC));
		assertNull(LocalDateTimeUtils.toLocalDateTime((Instant) null, UTC));
		assertNull(LocalDateTimeUtils.toLocalDateTime((Date) null, UTC));
		assertNull(LocalDateTimeUtils.toLocalDateTime((Calendar) null, UTC));
		assertNull(LocalDateTimeUtils.toLocalDateTime((LocalDate) null));
		assertEquals(T, LocalDateTimeUtils.toLocalDateTime((Long) null, UTC, T));
		assertEquals(T, LocalDateTimeUtils.toLocalDateTime((Date) null, UTC, T));
		assertEquals(T, LocalDateTimeUtils.toLocalDateTime((Calendar) null, UTC, T));
		assertEquals(T, LocalDateTimeUtils.toLocalDateTime(Instant.MAX, ZoneOffset.ofHours(18), T));
	}

	@Test
	public void testFormatAndParse() {
		assertEquals("2021-01-31T10:20:30", LocalDateTimeUtils.format(T));
		assertEquals("", LocalDateTimeUtils.format(null));
		assertEquals("-", LocalDateTimeUtils.format(null, DateTimeFormatter.ISO_LOCAL_DATE_TIME, "-"));
		assertEquals("31 Jan 2021 10:20:30", LocalDateTimeUtils.format(T, "dd MMM yyyy HH:mm:ss"));
		assertEquals("", LocalDateTimeUtils.format(null, "dd MMM yyyy HH:mm:ss"));

		assertEquals(T, LocalDateTimeUtils.parseLocalDateTime("2021-01-31T10:20:30"));
		assertEquals(T, LocalDateTimeUtils.parseLocalDateTime("2021-01-31T10:20:30", (DateTimeFormatter) null));
		assertEquals(T, LocalDateTimeUtils.parseLocalDateTime("31 Jan 2021 10:20:30", "dd MMM yyyy HH:mm:ss"));
		assertNull(LocalDateTimeUtils.parseLocalDateTime("2021-01-31"));
		assertNull(LocalDateTimeUtils.parseLocalDateTime(" "));
		assertEquals(T, LocalDateTimeUtils.parseLocalDateTime("x", "yyyy", T));
		assertThrows(IllegalArgumentException.class, () -> LocalDateTimeUtils.parseLocalDateTime("x", ""));
	}

	@Test
	public void testOf() {
		assertEquals(T, LocalDateTimeUtils.of(YearMonth.of(2021, 1), 31, 10, 20, 30));
		assertThrows(IllegalArgumentException.class, () -> LocalDateTimeUtils.of(YearMonth.of(2021, 2), 30, 10, 20, 30));
		assertThrows(IllegalArgumentException.class, () -> LocalDateTimeUtils.of(YearMonth.of(2021, 1), 1, 24, 0, 0));
		assertEquals(YearMonth.now().atDay(1).atTime(1, 2, 3), LocalDateTimeUtils.of((YearMonth) null, 1, 1, 2, 3));
		assertEquals(T, LocalDateTimeUtils.of(Year.of(2021), Month.JANUARY, 31, 10, 20, 30));
		assertEquals(T, LocalDateTimeUtils.of(LocalDate.of(2021, 1, 31), 10, 20, 30));
		assertEquals(LocalDate.now().atTime(1, 2, 3), LocalDateTimeUtils.of((LocalDate) null, 1, 2, 3));
		assertEquals(T, LocalDateTimeUtils.of(date(LocalDateTime.of(2021, 1, 31, 0, 0)), 10, 20, 30));
		assertEquals(LocalDateTime.of(2021, 1, 31, 0, 0), LocalDateTimeUtils.of(2021, 1, 31));
		assertEquals(T, LocalDateTimeUtils.of(2021, 1, 31, 10, 20, 30));
		assertThrows(IllegalArgumentException.class, () -> LocalDateTimeUtils.of(2021, 1, 31, 10, 60, 30));
		assertEquals(LocalDateTime.of(1965, 7, 1, 8, 0, 0), LocalDateTimeUtils.of(1965, 7, 1, 8, 0, 0));
		assertEquals(LocalDateTime.of(1900, 2, 28, 10, 20, 30), LocalDateTimeUtils.of(1900, 2, 28, 10, 20, 30));
		assertEquals(LocalDateTime.of(1900, 1, 1, 0, 0), LocalDateTimeUtils.of(Year.of(1900), Month.JANUARY, 1, 0, 0, 0));
	}

	@Test
	public void testCopyAndUntil() {
		assertSame(T, LocalDateTimeUtils.copy(T, UTC));
		assertEquals(T, LocalDateTimeUtils.copy(T, null));
		assertNear(LocalDateTime.now(UTC), LocalDateTimeUtils.copy(null, UTC));
		assertEquals(90000L, LocalDateTimeUtils.until(T, T.plusSeconds(90)));
		long remaining = LocalDateTimeUtils.until(LocalDateTime.now().plusHours(1));
		assertTrue(remaining > 3500000L && remaining <= 3600000L);
		assertThrows(IllegalArgumentException.class, () -> LocalDateTimeUtils.until(T, null));
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
		LocalDateTime start = LocalDateTime.of(2021, 3, 14, 0, 0);
		List<LocalDateTime> expected = Arrays.asList(start, start.plusHours(1), start.plusHours(2), start.plusHours(3));
		// The values must not be shifted by any time zone/DST conversion
		assertEquals(expected, toList(LocalDateTimeUtils.toIterator(start, start.plusHours(4), ZoneId.of("America/New_York"), 1,
				ChronoUnit.HOURS)));
		assertEquals(expected, toList(LocalDateTimeUtils.toIterator("2021-03-14T00:00:00", "2021-03-14T04:00:00", null, UTC, 1,
				ChronoUnit.HOURS)));
		assertEquals(expected, toList(LocalDateTimeUtils.toIterator(Date.from(start.toInstant(ZoneOffset.UTC)),
				Date.from(start.plusHours(4).toInstant(ZoneOffset.UTC)), UTC, 1, ChronoUnit.HOURS)));
		Iterator<LocalDateTime> it = LocalDateTimeUtils.toIterator(T, T, UTC, 1, ChronoUnit.HOURS);
		assertFalse(it.hasNext());
		assertFalse(LocalDateTimeUtils.toIterator(T, T, UTC, -1, ChronoUnit.HOURS).hasNext());
		assertThrows(IllegalArgumentException.class, () -> LocalDateTimeUtils.toIterator(T, T.plusHours(1), UTC, 0, ChronoUnit.HOURS));
	}

}
