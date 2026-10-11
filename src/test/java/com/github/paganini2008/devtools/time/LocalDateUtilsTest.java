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

import java.time.DayOfWeek;
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
import java.util.function.UnaryOperator;

import org.junit.jupiter.api.Test;

/**
 * LocalDateUtilsTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class LocalDateUtilsTest {

	private static final ZoneId UTC = ZoneOffset.UTC;
	private static final LocalDate D = LocalDate.of(2021, 1, 31);

	/** Date at local midnight (system time zone), so that conversions with the default zone are exact */
	private static Date date(LocalDate ld) {
		return Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	private static void assertAroundNow(UnaryOperator<LocalDate> expected, LocalDate actual) {
		LocalDate before = expected.apply(LocalDate.now());
		LocalDate after = expected.apply(LocalDate.now());
		assertTrue(actual.equals(before) || actual.equals(after), "Unexpected: " + actual);
	}

	@Test
	public void testAdd() {
		assertEquals(LocalDate.of(2023, 1, 31), LocalDateUtils.addYears(D, 2));
		assertEquals(LocalDate.of(2023, 1, 31), LocalDateUtils.addYears(date(D), 2));
		assertEquals(LocalDate.of(2021, 2, 28), LocalDateUtils.addMonths(D, 1));
		assertEquals(LocalDate.of(2021, 2, 28), LocalDateUtils.addMonths(date(D), 1));
		assertEquals(LocalDate.of(2021, 2, 1), LocalDateUtils.addDays(D, 1));
		assertEquals(LocalDate.of(2021, 2, 1), LocalDateUtils.addDays(date(D), 1));
		assertEquals(LocalDate.of(2021, 2, 14), LocalDateUtils.addWeeks(D, 2));
		assertEquals(LocalDate.of(2021, 2, 14), LocalDateUtils.addWeeks(date(D), 2));
		assertEquals(LocalDate.of(2020, 12, 31), LocalDateUtils.addAmount(D, -1, ChronoUnit.MONTHS));
	}

	@Test
	public void testAddRelativeToToday() {
		assertAroundNow(d -> d.plusYears(1), LocalDateUtils.addYears(1));
		assertAroundNow(d -> d.plusMonths(1), LocalDateUtils.addMonths(1));
		assertAroundNow(d -> d.plusDays(1), LocalDateUtils.addDays(1));
		assertAroundNow(d -> d.plusWeeks(1), LocalDateUtils.addWeeks(1));
		assertAroundNow(d -> d.plusDays(3), LocalDateUtils.addAmount(null, 3, ChronoUnit.DAYS));
		assertAroundNow(d -> d.plusDays(3), LocalDateUtils.addDays((Date) null, 3));
	}

	@Test
	public void testSet() {
		assertEquals(LocalDate.of(2000, 1, 31), LocalDateUtils.setYear(D, 2000));
		assertEquals(LocalDate.of(2000, 1, 31), LocalDateUtils.setYear(date(D), 2000));
		assertEquals(LocalDate.of(2021, 4, 30), LocalDateUtils.setMonth(D, 4));
		assertEquals(LocalDate.of(2021, 4, 30), LocalDateUtils.setMonth(date(D), 4));
		assertEquals(LocalDate.of(2021, 1, 5), LocalDateUtils.setDayOfMonth(D, 5));
		assertEquals(LocalDate.of(2021, 1, 5), LocalDateUtils.setDayOfMonth(date(D), 5));
		// 2021-01-31 is a Sunday, Monday of that ISO week is 2021-01-25
		assertEquals(LocalDate.of(2021, 1, 25), LocalDateUtils.setDayOfWeek(D, 1));
		assertEquals(LocalDate.of(2021, 1, 25), LocalDateUtils.setDayOfWeek(date(D), 1));
		assertEquals(LocalDate.of(2021, 2, 1), LocalDateUtils.setDayOfYear(D, 32));
		assertEquals(LocalDate.of(2021, 2, 1), LocalDateUtils.setDayOfYear(date(D), 32));
		assertEquals(LocalDate.of(2021, 1, 2), LocalDateUtils.setField(D, ChronoField.DAY_OF_MONTH, 2));
	}

	@Test
	public void testSetRelativeToToday() {
		assertAroundNow(d -> d.withYear(2000), LocalDateUtils.setYear(2000));
		assertAroundNow(d -> d.withMonth(2), LocalDateUtils.setMonth(2));
		assertAroundNow(d -> d.withDayOfMonth(1), LocalDateUtils.setDayOfMonth(1));
		assertAroundNow(d -> d.withDayOfYear(10), LocalDateUtils.setDayOfYear(10));
		assertAroundNow(d -> d.with(ChronoField.DAY_OF_WEEK, 3), LocalDateUtils.setDayOfWeek(3));
		assertEquals(DayOfWeek.WEDNESDAY, LocalDateUtils.setDayOfWeek(3).getDayOfWeek());
		assertAroundNow(d -> d.withDayOfMonth(1), LocalDateUtils.setField(null, ChronoField.DAY_OF_MONTH, 1));
	}

	@Test
	public void testToLocalDate() {
		Instant ins = Instant.parse("2021-01-31T23:30:00Z");
		assertEquals(D, LocalDateUtils.toLocalDate(ins, UTC));
		assertEquals(LocalDate.of(2021, 2, 1), LocalDateUtils.toLocalDate(ins, ZoneId.of("Asia/Shanghai")));
		assertEquals(D, LocalDateUtils.toLocalDate(ins.toEpochMilli(), UTC));
		assertEquals(D, LocalDateUtils.toLocalDate(Date.from(ins), UTC));
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		c.setTime(Date.from(ins));
		assertEquals(D, LocalDateUtils.toLocalDate(c, UTC));
		assertEquals(D, LocalDateUtils.toLocalDate(D.atTime(10, 0), null));
		assertEquals(ins.atZone(ZoneId.systemDefault()).toLocalDate(), LocalDateUtils.toLocalDate(ins, null));

		assertNull(LocalDateUtils.toLocalDate((Long) null, UTC));
		assertNull(LocalDateUtils.toLocalDate((Instant) null, UTC));
		assertNull(LocalDateUtils.toLocalDate((Date) null, UTC));
		assertNull(LocalDateUtils.toLocalDate((Calendar) null, UTC));
		assertEquals(D, LocalDateUtils.toLocalDate((LocalDateTime) null, D));
		assertEquals(D, LocalDateUtils.toLocalDate((Long) null, UTC, D));
		assertEquals(D, LocalDateUtils.toLocalDate((Date) null, UTC, D));
		assertEquals(D, LocalDateUtils.toLocalDate((Calendar) null, UTC, D));
		// out of the supported range
		assertEquals(D, LocalDateUtils.toLocalDate(Instant.MAX, ZoneOffset.ofHours(18), D));
	}

	@Test
	public void testFormatAndParse() {
		assertEquals("2021-01-31", LocalDateUtils.format(D));
		assertEquals("20210131", LocalDateUtils.format(D, DateTimeFormatter.BASIC_ISO_DATE));
		assertEquals("", LocalDateUtils.format(null));
		assertEquals("N/A", LocalDateUtils.format(null, DateTimeFormatter.BASIC_ISO_DATE, "N/A"));
		assertEquals("31 Jan 2021", LocalDateUtils.format(D, "dd MMM yyyy"));
		assertEquals("31 Jan 2021", LocalDateUtils.format(D, "dd MMM yyyy"));
		assertEquals("", LocalDateUtils.format(null, "dd MMM yyyy"));
		assertThrows(IllegalArgumentException.class, () -> LocalDateUtils.format(D, " "));

		assertEquals(D, LocalDateUtils.parseLocalDate("2021-01-31"));
		assertEquals(D, LocalDateUtils.parseLocalDate("2021-01-31", (DateTimeFormatter) null));
		assertEquals(D, LocalDateUtils.parseLocalDate("31 Jan 2021", "dd MMM yyyy"));
		assertNull(LocalDateUtils.parseLocalDate("bad"));
		assertNull(LocalDateUtils.parseLocalDate(null));
		assertNull(LocalDateUtils.parseLocalDate("", "yyyy"));
		assertEquals(D, LocalDateUtils.parseLocalDate("xx", "yyyy-MM-dd", D));
	}

	@Test
	public void testOf() {
		assertEquals(LocalDate.of(2020, 12, 31), LocalDateUtils.of(Year.of(2020), 366));
		assertThrows(IllegalArgumentException.class, () -> LocalDateUtils.of(Year.of(2021), 366));
		assertEquals(LocalDate.of(2021, 2, 28), LocalDateUtils.of(YearMonth.of(2021, 2), 28));
		assertThrows(IllegalArgumentException.class, () -> LocalDateUtils.of(YearMonth.of(2021, 2), 29));
		assertEquals(LocalDate.of(2021, 3, 1), LocalDateUtils.of(Year.of(2021), Month.MARCH, 1));
		assertEquals(D, LocalDateUtils.of(2021, 1, 31));
		assertThrows(IllegalArgumentException.class, () -> LocalDateUtils.of(2021, 2, 30));
		// dates before 1970 (e.g. birthdays) must keep working
		assertEquals(LocalDate.of(1969, 1, 1), LocalDateUtils.of(1969, 1, 1));
		assertEquals(LocalDate.of(1965, 7, 1), LocalDateUtils.of(1965, 7, 1));
		assertEquals(LocalDate.of(1900, 2, 28), LocalDateUtils.of(1900, 2, 28));
		assertThrows(IllegalArgumentException.class, () -> LocalDateUtils.of(1900, 2, 29));
		assertEquals(LocalDate.of(1965, 12, 31), LocalDateUtils.of(Year.of(1965), 365));
		assertEquals(LocalDate.of(1900, 3, 1), LocalDateUtils.of(Year.of(1900), Month.MARCH, 1));
		assertThrows(IllegalArgumentException.class, () -> LocalDateUtils.of(2021, 13, 1));
	}

	@Test
	public void testCopy() {
		assertSame(D, LocalDateUtils.copy(D, UTC));
		assertEquals(D, LocalDateUtils.copy(D, null));
		LocalDate before = LocalDate.now(UTC);
		LocalDate now = LocalDateUtils.copy(null, UTC);
		assertFalse(now.isBefore(before));
		assertFalse(now.isAfter(LocalDate.now(UTC)));
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
		List<LocalDate> expected = Arrays.asList(LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 3), LocalDate.of(2021, 1, 5));
		assertEquals(expected,
				toList(LocalDateUtils.toIterator(LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 6), UTC, 2, ChronoUnit.DAYS)));
		assertEquals(expected, toList(LocalDateUtils.toIterator("2021-01-01", "2021-01-06", null, UTC, 2, ChronoUnit.DAYS)));
		assertEquals(expected, toList(LocalDateUtils.toIterator(date(LocalDate.of(2021, 1, 1)), date(LocalDate.of(2021, 1, 6)), null, 2,
				ChronoUnit.DAYS)));
		// end is exclusive, an empty range yields nothing
		Iterator<LocalDate> it = LocalDateUtils.toIterator(D, D, UTC, 1, ChronoUnit.DAYS);
		assertFalse(it.hasNext());
		// an empty range never loops, whatever the interval
		assertFalse(LocalDateUtils.toIterator(D, D, UTC, 0, ChronoUnit.DAYS).hasNext());
		assertFalse(LocalDateUtils.toIterator(D.plusDays(1), D, UTC, -1, ChronoUnit.DAYS).hasNext());
		// a non-positive interval on a non-empty range would never end
		assertThrows(IllegalArgumentException.class, () -> LocalDateUtils.toIterator(D, D.plusDays(1), UTC, 0, ChronoUnit.DAYS));
		assertThrows(IllegalArgumentException.class, () -> LocalDateUtils.toIterator(D, D.plusDays(1), UTC, -1, ChronoUnit.DAYS));
		assertEquals(Arrays.asList(LocalDate.of(1965, 1, 1), LocalDate.of(1965, 1, 2)),
				toList(LocalDateUtils.toIterator(LocalDate.of(1965, 1, 1), LocalDate.of(1965, 1, 3), UTC, 1, ChronoUnit.DAYS)));
	}

}
