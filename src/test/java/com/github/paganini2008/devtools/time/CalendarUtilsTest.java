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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;

/**
 * CalendarUtilsTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class CalendarUtilsTest {

	private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
	private static final TimeZone SHANGHAI = TimeZone.getTimeZone("Asia/Shanghai");
	private static final Instant INS = Instant.parse("2021-01-31T10:20:30Z");

	private static Calendar utc() {
		Calendar c = Calendar.getInstance(UTC);
		c.setTimeInMillis(INS.toEpochMilli());
		return c;
	}

	/** A calendar in the system default time zone */
	private static Calendar local() {
		return CalendarUtils.of(2021, 1, 31, 10, 20, 30);
	}

	@Test
	public void testFormat() {
		assertEquals("2021-01-31 10:20:30", CalendarUtils.format(local()));
		assertEquals("2021/01/31", CalendarUtils.format(local(), "yyyy/MM/dd"));
		assertEquals("", CalendarUtils.format(null, "yyyy"));
		assertEquals("-", CalendarUtils.format(null, "yyyy", "-"));

		SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
		df.setTimeZone(UTC);
		assertEquals("10:20", CalendarUtils.format(utc(), df));
		assertNull(CalendarUtils.format(null, df));
		assertEquals("x", CalendarUtils.format(null, df, "x"));
	}

	@Test
	public void testFormatMany() {
		Calendar[] array = { local(), null };
		assertArrayEquals(new String[] { "2021-01-31 10:20:30", "" }, CalendarUtils.formatMany(array));
		assertArrayEquals(new String[] { "2021", "" }, CalendarUtils.formatMany(array, "yyyy"));
		assertArrayEquals(new String[] { "2021", "?" }, CalendarUtils.formatMany(array, "yyyy", "?"));
		SimpleDateFormat df = new SimpleDateFormat("MM", Locale.ENGLISH);
		assertArrayEquals(new String[] { "01", "" }, CalendarUtils.formatMany(array, df));
		assertArrayEquals(new String[] { "01", "?" }, CalendarUtils.formatMany(array, df, "?"));
		assertThrows(IllegalArgumentException.class, () -> CalendarUtils.formatMany(null, "yyyy"));
		assertThrows(IllegalArgumentException.class, () -> CalendarUtils.formatMany(null, df));
	}

	@Test
	public void testParse() {
		Calendar c = CalendarUtils.parse("2021-01-31 10:20:30", DateUtils.DEFAULT_DATE_PATTERN);
		assertEquals(2021, c.get(Calendar.YEAR));
		assertEquals(Calendar.JANUARY, c.get(Calendar.MONTH));
		assertEquals(31, c.get(Calendar.DAY_OF_MONTH));
		assertEquals(10, c.get(Calendar.HOUR_OF_DAY));
		assertEquals(TimeZone.getDefault().getID(), c.getTimeZone().getID());

		c = CalendarUtils.parse("2021/01/31", new String[] { "yyyy-MM-dd", "yyyy/MM/dd" });
		assertEquals(31, c.get(Calendar.DAY_OF_MONTH));
		assertNull(CalendarUtils.parse("", "yyyy"));
		assertNull(CalendarUtils.parse("bad", "yyyy-MM-dd"));
		assertNull(CalendarUtils.parse(null, new String[] { "yyyy" }));
		Calendar def = utc();
		assertSame(def, CalendarUtils.parse("bad", "yyyy-MM-dd", def));
		assertSame(def, CalendarUtils.parse("bad", new String[] { "yyyy-MM-dd" }, def));

		Calendar[] array = CalendarUtils.parseMany(new String[] { "2021-01-31", "bad" }, new String[] { "yyyy-MM-dd" });
		assertEquals(2, array.length);
		assertEquals(31, array[0].get(Calendar.DAY_OF_MONTH));
		assertNull(array[1]);
	}

	@Test
	public void testToCalendar() {
		Calendar c = CalendarUtils.toCalendar(INS.toEpochMilli(), SHANGHAI);
		assertEquals(18, c.get(Calendar.HOUR_OF_DAY));
		assertEquals(SHANGHAI.getID(), c.getTimeZone().getID());
		assertEquals(INS.toEpochMilli(), CalendarUtils.toCalendar(Date.from(INS), UTC).getTimeInMillis());
		assertEquals(INS.toEpochMilli(), CalendarUtils.toCalendar(INS, UTC).getTimeInMillis());
		assertEquals(INS.toEpochMilli(), CalendarUtils.toCalendar(LocalDateTime.of(2021, 1, 31, 18, 20, 30), SHANGHAI).getTimeInMillis());
		assertEquals(Instant.parse("2021-01-30T16:00:00Z").toEpochMilli(),
				CalendarUtils.toCalendar(LocalDate.of(2021, 1, 31), SHANGHAI).getTimeInMillis());
		assertEquals(TimeZone.getDefault().getID(), CalendarUtils.toCalendar(INS, null).getTimeZone().getID());
		assertEquals(LocalDateTime.of(2021, 1, 31, 18, 20, 30).atZone(TimeZone.getDefault().toZoneId()).toInstant().toEpochMilli(),
				CalendarUtils.toCalendar(LocalDateTime.of(2021, 1, 31, 18, 20, 30), null).getTimeInMillis());

		assertNull(CalendarUtils.toCalendar((Long) null, UTC));
		assertNull(CalendarUtils.toCalendar((Date) null, UTC));
		assertNull(CalendarUtils.toCalendar((Instant) null, UTC));
		assertNull(CalendarUtils.toCalendar((LocalDateTime) null, UTC));
		assertNull(CalendarUtils.toCalendar((LocalDate) null, null));
		Calendar def = utc();
		assertSame(def, CalendarUtils.toCalendar((Long) null, UTC, def));
		assertSame(def, CalendarUtils.toCalendar((LocalDateTime) null, null, def));
	}

	@Test
	public void testToCalendarArray() {
		Calendar[] array = CalendarUtils.toCalendarArray(new Long[] { INS.toEpochMilli(), null }, UTC);
		assertEquals(INS.toEpochMilli(), array[0].getTimeInMillis());
		assertNull(array[1]);
		Calendar def = utc();
		array = CalendarUtils.toCalendarArray(new Long[] { null }, UTC, def);
		assertSame(def, array[0]);

		array = CalendarUtils.toCalendarArray(new Date[] { Date.from(INS), null }, UTC);
		assertEquals(INS.toEpochMilli(), array[0].getTimeInMillis());
		assertNull(array[1]);
		assertThrows(IllegalArgumentException.class, () -> CalendarUtils.toCalendarArray((Date[]) null, UTC));
	}

	@Test
	public void testGetTimeInMillis() {
		assertEquals(INS.toEpochMilli(), CalendarUtils.getTimeInMillis(utc()));
		assertNull(CalendarUtils.getTimeInMillis((Calendar) null));
		assertEquals(1L, CalendarUtils.getTimeInMillis((Calendar) null, 1L));
		assertArrayEquals(new Long[] { INS.toEpochMilli(), null }, CalendarUtils.getTimeInMillis(new Calendar[] { utc(), null }));
		assertThrows(IllegalArgumentException.class, () -> CalendarUtils.getTimeInMillis((Calendar[]) null));
	}

	@Test
	public void testSetTime() {
		Calendar c = CalendarUtils.setTime(INS.toEpochMilli() + 123, 1, 2, 3, UTC);
		// milliseconds are kept
		assertEquals(Instant.parse("2021-01-31T01:02:03.123Z").toEpochMilli(), c.getTimeInMillis());
		c = CalendarUtils.setTime(INS.toEpochMilli(), 1, 2, 3);
		assertEquals(1, c.get(Calendar.HOUR_OF_DAY));
		assertEquals(2, c.get(Calendar.MINUTE));
		assertEquals(3, c.get(Calendar.SECOND));
	}

	@Test
	public void testOf() {
		Calendar c = CalendarUtils.of(2021, 1, 31, 10, 20, 30, UTC);
		assertEquals(INS.toEpochMilli(), c.getTimeInMillis());
		assertEquals(Instant.parse("1965-07-01T00:00:00Z").toEpochMilli(), CalendarUtils.of(1965, 7, 1, 0, 0, 0, UTC).getTimeInMillis());
		assertEquals(Instant.parse("1900-01-01T00:00:00Z").toEpochMilli(), CalendarUtils.of(1900, 1, 1, 0, 0, 0, UTC).getTimeInMillis());
		// lenient rolling is kept
		assertEquals(Instant.parse("2021-02-28T00:00:00Z").toEpochMilli(), CalendarUtils.of(2021, 3, 0, 0, 0, 0, UTC).getTimeInMillis());
		assertEquals(Instant.parse("2022-01-01T00:00:00Z").toEpochMilli(), CalendarUtils.of(2021, 13, 1, 0, 0, 0, UTC).getTimeInMillis());
		c = CalendarUtils.of(2021, 1, 31);
		assertEquals(2021, c.get(Calendar.YEAR));
		assertEquals(Calendar.JANUARY, c.get(Calendar.MONTH));
		assertEquals(31, c.get(Calendar.DAY_OF_MONTH));
		assertEquals(0, c.get(Calendar.HOUR_OF_DAY));
		assertEquals(0, c.get(Calendar.MILLISECOND));
		assertEquals(LocalDate.of(2021, 1, 31).atStartOfDay(TimeZone.getDefault().toZoneId()).toInstant().toEpochMilli(),
				c.getTimeInMillis());
	}

}
