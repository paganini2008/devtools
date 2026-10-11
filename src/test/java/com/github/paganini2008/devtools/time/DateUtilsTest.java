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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

/**
 * DateUtilsTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class DateUtilsTest {

	private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

	/** 2021-01-31 10:20:30.000 in the system default time zone */
	private static Date d() {
		return DateUtils.of(2021, 1, 31, 10, 20, 30);
	}

	private static Date local(int y, int m, int day, int h, int min, int s) {
		return Date.from(LocalDateTime.of(y, m, day, h, min, s).atZone(ZoneId.systemDefault()).toInstant());
	}

	private static void assertNearNow(long expectedOffsetMs, Date actual) {
		long diff = Math.abs(actual.getTime() - (System.currentTimeMillis() + expectedOffsetMs));
		assertTrue(diff < 60000L, "Unexpected date: " + actual);
	}

	@Test
	public void testToDate() {
		assertEquals(new Date(1000L), DateUtils.toDate(1000L));
		assertNull(DateUtils.toDate((Long) null));
		assertEquals(new Date(1000L), DateUtils.toDate(Instant.ofEpochMilli(1000L)));
		assertNull(DateUtils.toDate((Instant) null));
		Calendar c = Calendar.getInstance(UTC);
		c.setTimeInMillis(1000L);
		assertEquals(new Date(1000L), DateUtils.toDate(c));
		assertNull(DateUtils.toDate((Calendar) null));
		Date def = new Date(5L);
		assertSame(def, DateUtils.toDate((Calendar) null, def));

		assertEquals(Date.from(Instant.parse("2021-01-30T16:00:00Z")), DateUtils.toDate(LocalDate.of(2021, 1, 31), ZoneId.of("Asia/Shanghai")));
		assertEquals(local(2021, 1, 31, 0, 0, 0), DateUtils.toDate(LocalDate.of(2021, 1, 31), null));
		assertNull(DateUtils.toDate((LocalDate) null, null));
		assertEquals(Date.from(Instant.parse("2021-01-31T02:20:30Z")),
				DateUtils.toDate(LocalDateTime.of(2021, 1, 31, 10, 20, 30), ZoneId.of("Asia/Shanghai")));
		assertEquals(d(), DateUtils.toDate(LocalDateTime.of(2021, 1, 31, 10, 20, 30), null));
		assertNull(DateUtils.toDate((LocalDateTime) null, null));
	}

	@Test
	public void testToDateArray() {
		assertArrayEquals(new Date[] { new Date(1L), new Date(2L) }, DateUtils.toDateArray(new long[] { 1L, 2L }));
		assertArrayEquals(new Date[] { new Date(1L), null }, DateUtils.toDateArray(new Long[] { 1L, null }));
		Calendar c = Calendar.getInstance(UTC);
		c.setTimeInMillis(3L);
		assertArrayEquals(new Date[] { new Date(3L), null }, DateUtils.toDateArray(new Calendar[] { c, null }));
	}

	@Test
	public void testGetTimeInMillis() {
		assertEquals(5L, DateUtils.getTimeInMillis(Instant.ofEpochMilli(5L)));
		assertNull(DateUtils.getTimeInMillis((Instant) null));
		assertEquals(5L, DateUtils.getTimeInMillis(new Date(5L)));
		assertNull(DateUtils.getTimeInMillis((Date) null));
		assertArrayEquals(new Long[] { 5L, -1L }, DateUtils.getTimeInMillis(new Date[] { new Date(5L), null }, -1L));
		assertArrayEquals(new Long[] { null }, DateUtils.getTimeInMillis(new Date[] { null }));
		assertThrows(IllegalArgumentException.class, () -> DateUtils.getTimeInMillis((Date[]) null));
	}

	@Test
	public void testFormat() {
		Date d = d();
		assertEquals("2021-01-31 10:20:30", DateUtils.format(d));
		assertEquals("31/Jan/2021", DateUtils.format(d, "dd/MMM/yyyy"));
		assertEquals("", DateUtils.format((Date) null));
		assertEquals("-", DateUtils.format((Date) null, "yyyy", "-"));
		assertEquals("2021-01-31 10:20:30", DateUtils.format(d.getTime()));
		assertEquals("2021", DateUtils.format(d.getTime(), "yyyy"));
		assertEquals("", DateUtils.format((Long) null));
		assertEquals("-", DateUtils.format((Long) null, "yyyy", "-"));
		assertThrows(IllegalArgumentException.class, () -> DateUtils.format(d, " "));

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
		df.setTimeZone(UTC);
		assertEquals("1970-01-01T00:00:01", DateUtils.format(1000L, df));
		assertEquals("1970-01-01T00:00:01", DateUtils.format(new Date(1000L), df));
		assertEquals("", DateUtils.format((Long) null, df));
		assertEquals("", DateUtils.format((Date) null, df));
		assertThrows(IllegalArgumentException.class, () -> DateUtils.format(new Date(), (java.text.DateFormat) null));

		assertArrayEquals(new String[] { "2021-01-31 10:20:30", "" }, DateUtils.formatMany(new Date[] { d, null }));
		assertArrayEquals(new String[] { "2021", "?" }, DateUtils.formatMany(new Date[] { d, null }, "yyyy", "?"));
		assertArrayEquals(new String[] { "1970-01-01T00:00:01" }, DateUtils.formatMany(new Date[] { new Date(1000L) }, df));
		assertThrows(IllegalArgumentException.class, () -> DateUtils.formatMany(null, df));
	}

	@Test
	public void testParse() {
		assertEquals(d(), DateUtils.parse("2021-01-31 10:20:30", DateUtils.DEFAULT_DATE_PATTERN));
		assertEquals(d(), DateUtils.parse("31/Jan/2021 10:20:30", "dd/MMM/yyyy HH:mm:ss"));
		assertNull(DateUtils.parse("bad", "yyyy-MM-dd"));
		assertNull(DateUtils.parse(null, "yyyy-MM-dd"));
		assertNull(DateUtils.parse("", "yyyy-MM-dd"));
		Date def = new Date(0L);
		assertSame(def, DateUtils.parse("bad", "yyyy-MM-dd", def));
		assertEquals(local(2021, 1, 31, 0, 0, 0), DateUtils.parse("2021/01/31", new String[] { "yyyy-MM-dd", "yyyy/MM/dd" }));
		assertNull(DateUtils.parse("bad", new String[] { "yyyy-MM-dd", "yyyy/MM/dd" }));
		assertSame(def, DateUtils.parse("bad", new String[] { "yyyy-MM-dd" }, def));
		assertThrows(IllegalArgumentException.class, () -> DateUtils.parse("2021", (String[]) null));

		Date[] array = DateUtils.parseMany(new String[] { "2021-01-31", "x" }, new String[] { "yyyy-MM-dd" });
		assertEquals(local(2021, 1, 31, 0, 0, 0), array[0]);
		assertNull(array[1]);
		assertSame(def, DateUtils.parseMany(new String[] { "x" }, new String[] { "yyyy" }, def)[0]);
		assertThrows(IllegalArgumentException.class, () -> DateUtils.parseMany(null, new String[] { "yyyy" }));
	}

	@Test
	public void testReformat() {
		assertEquals("31/01/2021", DateUtils.reformat("2021-01-31", "yyyy-MM-dd", "dd/MM/yyyy"));
		assertNull(DateUtils.reformat("bad", "yyyy-MM-dd", "dd/MM/yyyy"));
		assertEquals("-", DateUtils.reformat(null, "yyyy-MM-dd", "dd/MM/yyyy", "-"));
	}

	@Test
	public void testConcurrentFormatAndParse() throws Exception {
		final int threads = 8;
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		try {
			CountDownLatch start = new CountDownLatch(1);
			List<Future<Boolean>> futures = new ArrayList<>();
			for (int t = 0; t < threads; t++) {
				final int offset = t;
				futures.add(executor.submit(() -> {
					start.await();
					for (int i = 0; i < 300; i++) {
						Date date = local(2000 + offset, 1 + (i % 12), 1 + (i % 28), i % 24, i % 60, (i + offset) % 60);
						String text = DateUtils.format(date, "yyyy-MM-dd HH:mm:ss");
						if (!date.equals(DateUtils.parse(text, "yyyy-MM-dd HH:mm:ss"))) {
							// skip local DST gaps
							if (DateUtils.parse(text, "yyyy-MM-dd HH:mm:ss") == null) {
								return false;
							}
						}
						if (!text.startsWith(String.valueOf(2000 + offset))) {
							return false;
						}
					}
					return true;
				}));
			}
			start.countDown();
			for (Future<Boolean> f : futures) {
				assertTrue(f.get(10, TimeUnit.SECONDS));
			}
		} finally {
			executor.shutdownNow();
		}
	}

	@Test
	public void testAdd() {
		Date d = d();
		assertEquals(local(2022, 1, 31, 10, 20, 30), DateUtils.addYears(d, 1));
		assertEquals(local(2021, 2, 28, 10, 20, 30), DateUtils.addMonths(d, 1));
		assertEquals(local(2021, 2, 7, 10, 20, 30), DateUtils.addWeekOfYear(d, 1));
		assertEquals(local(2021, 2, 7, 10, 20, 30), DateUtils.addWeekOfMonth(d, 1));
		assertEquals(local(2021, 2, 1, 10, 20, 30), DateUtils.addDayOfYear(d, 1));
		assertEquals(local(2021, 2, 1, 10, 20, 30), DateUtils.addDayOfMonth(d, 1));
		assertEquals(local(2021, 2, 1, 10, 20, 30), DateUtils.addDayOfWeek(d, 1));
		assertEquals(local(2021, 1, 31, 11, 20, 30), DateUtils.addHourOfDay(d, 1));
		assertEquals(local(2021, 1, 31, 10, 21, 30), DateUtils.addMinutes(d, 1));
		assertEquals(local(2021, 1, 31, 10, 20, 31), DateUtils.addSeconds(d, 1));
		assertEquals(local(2021, 1, 31, 10, 20, 31), DateUtils.addField(d, Calendar.SECOND, 1));
	}

	@Test
	public void testAddRelativeToNow() {
		long day = 24L * 3600 * 1000;
		assertNearNow(0, DateUtils.addField(null, Calendar.SECOND, 0));
		assertNearNow(1000L, DateUtils.addSeconds(1));
		assertNearNow(60000L, DateUtils.addMinutes(1));
		assertNearNow(3600000L, DateUtils.addHourOfDay(1));
		assertNearNow(day, DateUtils.addDayOfMonth(1));
		assertNearNow(day, DateUtils.addDayOfYear(1));
		assertNearNow(day, DateUtils.addDayOfWeek(1));
		assertNearNow(7 * day, DateUtils.addWeekOfYear(1));
		assertNearNow(7 * day, DateUtils.addWeekOfMonth(1));
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		assertTrue(DateUtils.getYear(DateUtils.addYears(1)) - year >= 1);
		assertTrue(DateUtils.addMonths(1).after(new Date()));
	}

	@Test
	public void testSet() {
		Date d = d();
		assertEquals(local(2000, 1, 31, 10, 20, 30), DateUtils.setYear(d, 2000));
		assertEquals(local(1965, 1, 31, 10, 20, 30), DateUtils.setYear(d, 1965));
		assertEquals(local(2021, 2, 1, 10, 20, 30), DateUtils.setDayOfMonth(d, 32));
		assertEquals(local(2021, 3, 31, 10, 20, 30), DateUtils.setMonth(d, Calendar.MARCH));
		assertEquals(local(2021, 1, 5, 10, 20, 30), DateUtils.setDayOfMonth(d, 5));
		assertEquals(local(2021, 2, 1, 10, 20, 30), DateUtils.setDayOfYear(d, 32));
		assertEquals(Calendar.MONDAY, DateUtils.getDayOfWeek(DateUtils.setDayOfWeek(d, Calendar.MONDAY)));
		assertEquals(10, DateUtils.getField(DateUtils.setWeekOfYear(d, 10), Calendar.WEEK_OF_YEAR));
		assertEquals(2, DateUtils.getWeekOfMonth(DateUtils.setWeekOfMonth(d, 2)));

		assertEquals(2000, DateUtils.getYear(DateUtils.setYear(2000)));
		assertEquals(Calendar.MARCH, DateUtils.getMonth(DateUtils.setMonth(Calendar.MARCH)));
		assertEquals(1, DateUtils.getDayOfMonth(DateUtils.setDayOfMonth(1)));
		assertEquals(10, DateUtils.getDayOfYear(DateUtils.setDayOfYear(10)));
		assertEquals(Calendar.FRIDAY, DateUtils.getDayOfWeek(DateUtils.setDayOfWeek(Calendar.FRIDAY)));
		assertEquals(10, DateUtils.getField(DateUtils.setWeekOfYear(10), Calendar.WEEK_OF_YEAR));
		assertEquals(2, DateUtils.getWeekOfMonth(DateUtils.setWeekOfMonth(2)));
	}

	@Test
	public void testTodayAndTime() {
		Date today = DateUtils.today();
		Calendar c = Calendar.getInstance();
		c.setTime(today);
		assertEquals(0, c.get(Calendar.HOUR_OF_DAY));
		assertEquals(0, c.get(Calendar.MINUTE));
		assertEquals(0, c.get(Calendar.SECOND));
		assertEquals(0, c.get(Calendar.MILLISECOND));
		assertFalse(today.after(new Date()));

		Date d = d();
		assertEquals(local(2021, 1, 31, 9, 8, 7), DateUtils.setAM(d, 9, 8, 7));
		assertEquals(local(2021, 1, 31, 21, 8, 7), DateUtils.setPM(d, 9, 8, 7));
		assertEquals(9, DateUtils.getHourOfDay(DateUtils.setAM(null, 9, 0, 0)));
		assertEquals(21, DateUtils.getHourOfDay(DateUtils.setPM(null, 9, 0, 0)));
		assertEquals(local(2021, 1, 31, 1, 2, 3), DateUtils.setTime(d, 1, 2, 3));
		assertEquals(local(2021, 1, 31, 1, 2, 3), DateUtils.setTime(d.getTime(), 1, 2, 3));
		assertEquals(1, DateUtils.getHourOfDay(DateUtils.setTime(1, 2, 3)));
		assertEquals(local(2021, 1, 31, 5, 6, 7), DateUtils.setTime(d, local(1999, 5, 5, 5, 6, 7)));
		assertThrows(IllegalArgumentException.class, () -> DateUtils.setTime((Date) null, 1, 2, 3));
		assertThrows(IllegalArgumentException.class, () -> DateUtils.setTime(d, (Date) null));
	}

	@Test
	public void testGetFields() {
		Date d = d();
		assertEquals(2021, DateUtils.getYear(d));
		assertEquals(Calendar.JANUARY, DateUtils.getMonth(d));
		assertEquals(31, DateUtils.getDayOfMonth(d));
		assertEquals(Calendar.SUNDAY, DateUtils.getDayOfWeek(d));
		assertEquals(31, DateUtils.getDayOfYear(d));
		assertEquals(10, DateUtils.getHourOfDay(d));
		assertEquals(20, DateUtils.getMinute(d));
		assertEquals(30, DateUtils.getSecond(d));
		assertTrue(DateUtils.getWeekOfMonth(d) >= 4);
		assertThrows(IllegalArgumentException.class, () -> DateUtils.getField(null, Calendar.YEAR));

		assertTrue(DateUtils.getYear() >= 2020);
		assertTrue(DateUtils.getMonth() >= 0 && DateUtils.getMonth() <= 11);
		assertTrue(DateUtils.getDayOfMonth() >= 1 && DateUtils.getDayOfMonth() <= 31);
		assertTrue(DateUtils.getDayOfWeek() >= 1 && DateUtils.getDayOfWeek() <= 7);
		assertTrue(DateUtils.getDayOfYear() >= 1 && DateUtils.getDayOfYear() <= 366);
		assertTrue(DateUtils.getWeekOfMonth() >= 0 && DateUtils.getWeekOfMonth() <= 6);
		assertTrue(DateUtils.getHourOfDay() >= 0 && DateUtils.getHourOfDay() <= 23);
		assertTrue(DateUtils.getMinute() >= 0 && DateUtils.getMinute() <= 59);
		assertTrue(DateUtils.getSecond() >= 0 && DateUtils.getSecond() <= 59);
	}

	@Test
	public void testLastDay() {
		assertEquals(365, DateUtils.getLastDayOfYear(d()));
		assertEquals(366, DateUtils.getLastDayOfYear(DateUtils.of(2020, 6, 1)));
		assertEquals(28, DateUtils.getLastDayOfMonth(DateUtils.of(2021, 2, 10)));
		assertEquals(29, DateUtils.getLastDayOfMonth(DateUtils.of(2020, 2, 10)));
		assertTrue(DateUtils.getLastWeekOfYear(d()) >= 52);
		assertThrows(IllegalArgumentException.class, () -> DateUtils.getLastDay(null, Calendar.DAY_OF_MONTH));
	}

	@Test
	public void testOf() {
		assertEquals(local(2021, 1, 31, 0, 0, 0), DateUtils.of(2021, 1, 31));
		assertEquals(local(1965, 7, 1, 0, 0, 0), DateUtils.of(1965, 7, 1));
		assertEquals(local(1900, 1, 1, 12, 0, 0), DateUtils.of(1900, 1, 1, 12, 0, 0));
		// lenient Calendar rolling is kept: day 0 is the last day of the previous month, month 13 is next January
		assertEquals(local(2021, 2, 28, 0, 0, 0), DateUtils.of(2021, 3, 0));
		assertEquals(local(2022, 1, 1, 0, 0, 0), DateUtils.of(2021, 13, 1));
		assertEquals(local(2021, 2, 1, 1, 0, 0), DateUtils.of(2021, 1, 31, 25, 0, 0));
		// no random milliseconds from the current time
		assertEquals(0L, d().getTime() % 1000);
	}

	@Test
	public void testConvert() {
		assertEquals(120L, DateUtils.convertToSeconds(2, TimeUnit.MINUTES));
		assertEquals(5L, DateUtils.convertToSeconds(5, TimeUnit.SECONDS));
		assertEquals(120L, DateUtils.converToSecond(2, TimeUnit.MINUTES));
		assertEquals(2000L, DateUtils.convertToMillis(2, TimeUnit.SECONDS));
		assertEquals(5L, DateUtils.convertToMillis(5, TimeUnit.MILLISECONDS));
		assertEquals(2000L, DateUtils.convertToNanos(2, TimeUnit.MICROSECONDS));
		assertEquals(5L, DateUtils.convertToNanos(5, TimeUnit.NANOSECONDS));
		assertThrows(IllegalArgumentException.class, () -> DateUtils.convertToSeconds(-1, TimeUnit.SECONDS));
		assertThrows(IllegalArgumentException.class, () -> DateUtils.convertToMillis(-1, TimeUnit.SECONDS));
		assertThrows(IllegalArgumentException.class, () -> DateUtils.convertToNanos(-1, TimeUnit.SECONDS));
		assertEquals(90L, DateUtils.until(new Date(0L), new Date(90000L), TimeUnit.SECONDS));
		assertEquals(-90L, DateUtils.until(new Date(90000L), new Date(0L), TimeUnit.SECONDS));
	}

	private static List<Date> toList(Iterator<Calendar> it) {
		List<Date> list = new ArrayList<>();
		while (it.hasNext()) {
			list.add(it.next().getTime());
		}
		return list;
	}

	@Test
	public void testIterator() {
		Date from = DateUtils.of(2021, 1, 1);
		Date to = DateUtils.of(2021, 1, 4);
		List<Date> forward = toList(DateUtils.toIterator(from, to, 1, Calendar.DAY_OF_MONTH));
		assertEquals(3, forward.size());
		assertEquals(from, forward.get(0));
		assertEquals(DateUtils.of(2021, 1, 3), forward.get(2));
		assertEquals(forward, toList(DateUtils.toIterator(from, 3, 1, Calendar.DAY_OF_MONTH)));

		List<Date> backward = toList(DateUtils.toIterator(to, from, 1, Calendar.DAY_OF_MONTH));
		assertEquals(3, backward.size());
		assertEquals(to, backward.get(0));
		assertEquals(DateUtils.of(2021, 1, 2), backward.get(2));

		Iterator<Calendar> it = DateUtils.toIterator(from, from, 1, Calendar.DAY_OF_MONTH);
		assertFalse(it.hasNext());
		assertFalse(DateUtils.toIterator(from, from, 0, Calendar.DAY_OF_MONTH).hasNext());
		assertThrows(IllegalArgumentException.class, () -> DateUtils.toIterator(from, to, 0, Calendar.DAY_OF_MONTH));
		assertThrows(IllegalArgumentException.class, () -> DateUtils.toIterator(to, from, -1, Calendar.DAY_OF_MONTH));
		assertThrows(IllegalArgumentException.class, () -> DateUtils.toIterator(null, to, 1, Calendar.DAY_OF_MONTH));
	}

	@Test
	public void testPopulate() {
		Date from = DateUtils.of(2021, 1, 1);
		Map<Date, Integer> map = DateUtils.populate(from, 3, 1, Calendar.DAY_OF_MONTH, c -> c.get(Calendar.DAY_OF_MONTH));
		assertEquals(3, map.size());
		assertEquals(Integer.valueOf(1), map.get(from));
		assertEquals(Integer.valueOf(3), map.get(DateUtils.of(2021, 1, 3)));

		Map<String, Integer> map2 = DateUtils.populate(from, 2, 12, Calendar.HOUR_OF_DAY, c -> DateUtils.format(c.getTime(), "dd HH"),
				c -> c.get(Calendar.HOUR_OF_DAY));
		assertEquals(4, map2.size());
		assertEquals(Integer.valueOf(12), map2.get("01 12"));
		assertEquals(Integer.valueOf(12), map2.get("02 12"));
	}

}
