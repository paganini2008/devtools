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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

/**
 * RandomDateUtilsTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class RandomDateUtilsTest {

	private static final Year Y2020 = Year.of(2020);
	private static final Year Y2021 = Year.of(2021);

	@RepeatedTest(5)
	public void testYearAndYearMonth() {
		Year year = RandomDateUtils.randomYear();
		assertTrue(year.getValue() >= 1970 && !year.isAfter(Year.now()));
		assertEquals(Y2020, RandomDateUtils.randomYear(Y2020, Y2020));
		assertEquals(Y2021, RandomDateUtils.randomYear(2021, 2021));

		assertEquals(2020, RandomDateUtils.randomYearMonth(Y2020).getYear());
		assertEquals(YearMonth.of(2020, 3), RandomDateUtils.randomYearMonth(Y2020, Month.MARCH, Month.MARCH));
		YearMonth ym = RandomDateUtils.randomYearMonth(Y2020, Y2021, Month.JUNE, Month.JULY);
		assertTrue(ym.getYear() >= 2020 && ym.getYear() <= 2021);
		assertTrue(ym.getMonthValue() == 6 || ym.getMonthValue() == 7);
		// whole year: month must be within 1..12
		ym = RandomDateUtils.randomYearMonth(2020);
		assertEquals(2020, ym.getYear());
		assertEquals(YearMonth.of(2020, 12), RandomDateUtils.randomYearMonth(2020, 12, 12));
		ym = RandomDateUtils.randomYearMonth(2020, 2021, 1, 2);
		assertTrue(ym.getMonthValue() <= 2);
	}

	@RepeatedTest(5)
	public void testLocalDate() {
		assertEquals(Year.now().getValue(), RandomDateUtils.randomLocalDate().getYear());
		assertEquals(2021, RandomDateUtils.randomLocalDate(Y2021).getYear());
		assertEquals(LocalDate.of(2021, 12, 31), RandomDateUtils.randomLocalDate(Y2021, 365, 400));
		LocalDate ld = RandomDateUtils.randomLocalDate(Y2020, Y2021, 1, 1);
		assertEquals(1, ld.getDayOfYear());
		ld = RandomDateUtils.randomLocalDate(Y2021, Month.FEBRUARY);
		assertEquals(YearMonth.of(2021, 2), YearMonth.from(ld));
		ld = RandomDateUtils.randomLocalDate(YearMonth.of(2020, 2));
		assertEquals(YearMonth.of(2020, 2), YearMonth.from(ld));
		assertEquals(LocalDate.of(2020, 2, 29), RandomDateUtils.randomLocalDate(YearMonth.of(2020, 2), 29, 31));
		assertEquals(LocalDate.of(2021, 2, 28), RandomDateUtils.randomLocalDate(Y2021, Month.FEBRUARY, 28, 31));
		ld = RandomDateUtils.randomLocalDate(Y2021, Month.MARCH, Month.APRIL, 1, 2);
		assertTrue(ld.getDayOfMonth() <= 2);
		ld = RandomDateUtils.randomLocalDate(Y2020, Y2021, Month.MARCH, Month.APRIL, 1, 2);
		assertTrue(ld.getMonthValue() == 3 || ld.getMonthValue() == 4);
		assertEquals(LocalDate.of(2021, 4, 30), RandomDateUtils.randomLocalDate(2021, 4, 30, 31));
		ld = RandomDateUtils.randomLocalDate(2021, 4, 5, 1, 1);
		assertEquals(1, ld.getDayOfMonth());
		ld = RandomDateUtils.randomLocalDate(2020, 2021, 4, 5, 1, 1);
		assertEquals(1, ld.getDayOfMonth());
	}

	@RepeatedTest(5)
	public void testLocalDateBetween() {
		LocalDate ld = RandomDateUtils.randomLocalDate("2021-01-01", "2021-01-03");
		assertFalse(ld.isBefore(LocalDate.of(2021, 1, 1)));
		assertFalse(ld.isAfter(LocalDate.of(2021, 1, 3)));
		assertEquals(LocalDate.of(2021, 1, 1), RandomDateUtils.randomLocalDate("20210101", "20210101", DateTimeFormatter.BASIC_ISO_DATE));
		// end is exclusive
		LocalDate from = LocalDate.of(2021, 1, 1);
		for (int i = 0; i < 20; i++) {
			assertEquals(from, RandomDateUtils.randomLocalDate(from, from.plusDays(1)));
		}
		assertThrows(IllegalArgumentException.class, () -> RandomDateUtils.randomLocalDate(from, from.minusDays(1)));
	}

	@RepeatedTest(5)
	public void testLocalDateTime() {
		assertEquals(Year.now().getValue(), RandomDateUtils.randomLocalDateTime().getYear());
		assertEquals(2021, RandomDateUtils.randomLocalDateTime(Y2021).getYear());
		assertEquals(365, RandomDateUtils.randomLocalDateTime(Y2021, 365, 365).getDayOfYear());

		LocalDateTime ldt = RandomDateUtils.randomLocalDateTime(Y2021, 10, "10:30:00", "11:10:00");
		assertEquals(10, ldt.getDayOfYear());
		assertTimeBetween(ldt.toLocalTime(), LocalTime.of(10, 30), LocalTime.of(11, 10));
		ldt = RandomDateUtils.randomLocalDateTime(Y2021, 1, 2, LocalTime.of(1, 0), LocalTime.of(1, 0));
		assertEquals(LocalTime.of(1, 0), ldt.toLocalTime());
		ldt = RandomDateUtils.randomLocalDateTime(Y2020, Y2021, 1, 1, LocalTime.of(1, 0), LocalTime.of(2, 0));
		assertEquals(1, ldt.getDayOfYear());
		ldt = RandomDateUtils.randomLocalDateTime(LocalDate.of(2021, 5, 5), "23:59:00", "23:59:59");
		assertEquals(LocalDate.of(2021, 5, 5), ldt.toLocalDate());
		assertEquals(59, ldt.getMinute());

		// February 2021 has 28 days
		for (int i = 0; i < 20; i++) {
			ldt = RandomDateUtils.randomLocalDateTime(Y2021, Month.FEBRUARY);
			assertEquals(Month.FEBRUARY, ldt.getMonth());
		}
		ldt = RandomDateUtils.randomLocalDateTime(Y2021, Month.MAY, 5);
		assertEquals(LocalDate.of(2021, 5, 5), ldt.toLocalDate());
		ldt = RandomDateUtils.randomLocalDateTime(Y2021, Month.MAY, 31, 40);
		assertEquals(LocalDate.of(2021, 5, 31), ldt.toLocalDate());
		ldt = RandomDateUtils.randomLocalDateTime(Y2021, Month.MAY, Month.MAY, 1, 1);
		assertEquals(LocalDate.of(2021, 5, 1), ldt.toLocalDate());
		ldt = RandomDateUtils.randomLocalDateTime(Y2021, Y2021, Month.MAY, Month.MAY, 1, 1);
		assertEquals(LocalDate.of(2021, 5, 1), ldt.toLocalDate());
		ldt = RandomDateUtils.randomLocalDateTime(Y2021, Month.MAY, 1, "08:00:00", "08:00:00");
		assertEquals(LocalDateTime.of(2021, 5, 1, 8, 0), ldt);
		ldt = RandomDateUtils.randomLocalDateTime(Y2021, Month.MAY, 1, 1, LocalTime.NOON, LocalTime.NOON);
		assertEquals(LocalDateTime.of(2021, 5, 1, 12, 0), ldt);
		ldt = RandomDateUtils.randomLocalDateTime(Y2021, Month.MAY, Month.MAY, 1, 1, LocalTime.NOON, LocalTime.NOON);
		assertEquals(LocalDateTime.of(2021, 5, 1, 12, 0), ldt);
		ldt = RandomDateUtils.randomLocalDateTime(Y2021, Y2021, Month.MAY, Month.MAY, 1, 1, LocalTime.NOON, LocalTime.NOON);
		assertEquals(LocalDateTime.of(2021, 5, 1, 12, 0), ldt);
		ldt = RandomDateUtils.randomLocalDateTime(LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 1));
		assertEquals(LocalDate.of(2021, 1, 1), ldt.toLocalDate());
		ldt = RandomDateUtils.randomLocalDateTime(LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 1), LocalTime.MIDNIGHT,
				LocalTime.MIDNIGHT);
		assertEquals(LocalDateTime.of(2021, 1, 1, 0, 0), ldt);
	}

	@RepeatedTest(5)
	public void testLocalDateTimeBetween() {
		LocalDateTime ldt = RandomDateUtils.randomLocalDateTime("2021-01-01T00:00:00", "2021-01-01T00:00:10");
		assertTrue(!ldt.isBefore(LocalDateTime.of(2021, 1, 1, 0, 0)) && !ldt.isAfter(LocalDateTime.of(2021, 1, 1, 0, 0, 10)));
		// a range far longer than Integer.MAX_VALUE seconds must work
		LocalDateTime start = LocalDateTime.of(1900, 1, 1, 0, 0);
		LocalDateTime end = LocalDateTime.of(2100, 1, 1, 0, 0);
		ldt = RandomDateUtils.randomLocalDateTime(start, end);
		assertTrue(!ldt.isBefore(start) && !ldt.isAfter(end));
		assertThrows(IllegalArgumentException.class, () -> RandomDateUtils.randomLocalDateTime(end, start));

		ldt = RandomDateUtils.randomLocalDateTime(LocalDate.of(2021, 1, 1));
		assertEquals(LocalDate.of(2021, 1, 1), ldt.toLocalDate());
		ldt = RandomDateUtils.randomLocalDateTime(2021, 1, 1, 5, 5, 6, 6, 7, 7);
		assertEquals(LocalDateTime.of(2021, 1, 1, 5, 6, 7), ldt);
		ldt = RandomDateUtils.randomLocalDateTime(2020, 2020, 1, 1, 1, 1, 5, 5, 6, 6, 7, 7);
		assertEquals(LocalDateTime.of(2020, 1, 1, 5, 6, 7), ldt);
	}

	@RepeatedTest(5)
	public void testLocalTime() {
		RandomDateUtils.randomLocalTime();
		LocalTime lt = RandomDateUtils.randomLocalTime(10, 11, 50, 10);
		assertTimeBetween(lt, LocalTime.of(10, 50), LocalTime.of(11, 10, 59));
		assertEquals(LocalTime.of(1, 2, 3), RandomDateUtils.randomLocalTime(1, 1, 2, 2, 3, 3));
		assertEquals(LocalTime.of(1, 2, 3), RandomDateUtils.randomLocalTime("01:02:03", "01:02:03"));
		lt = RandomDateUtils.randomLocalTime("0102", "0103", DateTimeFormatter.ofPattern("HHmm"));
		assertTimeBetween(lt, LocalTime.of(1, 2), LocalTime.of(1, 3));
		assertThrows(IllegalArgumentException.class, () -> RandomDateUtils.randomLocalTime(LocalTime.NOON, LocalTime.MIDNIGHT));
	}

	@RepeatedTest(5)
	public void testDate() {
		Calendar c = Calendar.getInstance();
		c.setTime(RandomDateUtils.randomDate());
		assertEquals(Calendar.getInstance().get(Calendar.YEAR), c.get(Calendar.YEAR));
		c.setTime(RandomDateUtils.randomDate(2021));
		assertEquals(2021, c.get(Calendar.YEAR));
		c.setTime(RandomDateUtils.randomDate(2021, 2));
		assertEquals(Calendar.FEBRUARY, c.get(Calendar.MONTH));
		assertDate(RandomDateUtils.randomDate(2021, 2, 28, 31), 2021, 2, 28);
		assertDate(RandomDateUtils.randomDate(2021, 3, 3, 5, 5), 2021, 3, 5);
		assertDate(RandomDateUtils.randomDate(2020, 2020, 3, 3, 5, 5), 2020, 3, 5);
		assertDate(RandomDateUtils.randomDate("2021-03-05", "2021-03-05", "yyyy-MM-dd"), 2021, 3, 5);
		Date from = RandomDateUtils.randomDate("2021-03-05", "2021-03-05", "yyyy-MM-dd");
		Date d = RandomDateUtils.randomDate(from, new Date(from.getTime() + 3L * 24 * 3600 * 1000));
		assertTrue(!d.before(from) && d.getTime() <= from.getTime() + 3L * 24 * 3600 * 1000);
		assertThrows(IllegalArgumentException.class, () -> RandomDateUtils.randomDate(new Date(), new Date(0)));
	}

	@RepeatedTest(5)
	public void testDateTime() {
		Calendar c = Calendar.getInstance();
		c.setTime(RandomDateUtils.randomDateTime());
		assertEquals(Calendar.getInstance().get(Calendar.YEAR), c.get(Calendar.YEAR));
		c.setTime(RandomDateUtils.randomDateTime(2021));
		assertEquals(2021, c.get(Calendar.YEAR));
		c.setTime(RandomDateUtils.randomDateTime(2021, 4));
		assertEquals(Calendar.APRIL, c.get(Calendar.MONTH));
		c.setTime(RandomDateUtils.randomDateTime(2021, 4, 2, 2));
		assertEquals(2, c.get(Calendar.DAY_OF_MONTH));
		c.setTime(RandomDateUtils.randomDateTime(2021, 2021, 4, 4, 2, 2));
		assertEquals(2, c.get(Calendar.DAY_OF_MONTH));
		c.setTime(RandomDateUtils.randomDateTime(2021, 4, 2));
		assertEquals(2, c.get(Calendar.DAY_OF_MONTH));
		assertDateTime(RandomDateUtils.randomDateTime(2021, 4, 2, 5, 5, 6, 6, 7, 7), 2021, 4, 2, 5, 6, 7);
		assertDateTime(RandomDateUtils.randomDateTime(2021, 4, 2, 2, 5, 5, 6, 6, 7, 7), 2021, 4, 2, 5, 6, 7);
		assertDateTime(RandomDateUtils.randomDateTime(2021, 4, 4, 2, 2, 5, 5, 6, 6, 7, 7), 2021, 4, 2, 5, 6, 7);
		assertDateTime(RandomDateUtils.randomDateTime(2021, 2021, 4, 4, 2, 2, 5, 5, 6, 6, 7, 7), 2021, 4, 2, 5, 6, 7);
		Date day = RandomDateUtils.randomDate(2021, 4, 2, 2);
		assertDateTime(RandomDateUtils.randomDateTime(day, "05:06:07", "05:06:07", "HH:mm:ss"), 2021, 4, 2, 5, 6, 7);
		assertDateTime(RandomDateUtils.randomDateTime("2021-04-02 05:06:07", "2021-04-02 05:06:07", "yyyy-MM-dd HH:mm:ss"), 2021, 4,
				2, 5, 6, 7);
		Date d = RandomDateUtils.randomDateTime("2021-04-02 05:06:07", null, "yyyy-MM-dd HH:mm:ss");
		assertTrue(d.getTime() <= System.currentTimeMillis());
		// a range far longer than Integer.MAX_VALUE seconds must work
		Date from = new Date(0L);
		Date to = new Date(200L * 365 * 24 * 3600 * 1000);
		d = RandomDateUtils.randomDateTime(from, to);
		assertTrue(!d.before(from) && !d.after(to));
	}

	private static void assertTimeBetween(LocalTime value, LocalTime from, LocalTime to) {
		assertTrue(!value.isBefore(from) && !value.isAfter(to), value + " not in [" + from + ", " + to + "]");
	}

	private static void assertDate(Date date, int year, int month, int day) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		assertEquals(year, c.get(Calendar.YEAR));
		assertEquals(month, c.get(Calendar.MONTH) + 1);
		assertEquals(day, c.get(Calendar.DAY_OF_MONTH));
	}

	private static void assertDateTime(Date date, int year, int month, int day, int hour, int minute, int second) {
		assertDate(date, year, month, day);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		assertEquals(hour, c.get(Calendar.HOUR_OF_DAY));
		assertEquals(minute, c.get(Calendar.MINUTE));
		assertEquals(second, c.get(Calendar.SECOND));
	}


	@Test
	public void randomDateTimeOfMonth_staysInsideShortMonths() {
		Calendar c = Calendar.getInstance();
		for (int i = 0; i < 2000; i++) {
			c.setTime(RandomDateUtils.randomDateTime(2021, 4));
			assertEquals(Calendar.APRIL, c.get(Calendar.MONTH));
			c.setTime(RandomDateUtils.randomDateTime(2021, 2));
			assertEquals(Calendar.FEBRUARY, c.get(Calendar.MONTH));
		}
	}
}
