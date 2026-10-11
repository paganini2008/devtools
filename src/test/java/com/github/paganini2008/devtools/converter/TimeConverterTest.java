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
package com.github.paganini2008.devtools.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;

/**
 * TimeConverterTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class TimeConverterTest {

	private static final ZoneId UTC = ZoneOffset.UTC;
	private static final LocalDateTime LDT = LocalDateTime.of(2020, 3, 4, 5, 6, 7);
	private static final long MILLIS = LDT.toInstant(ZoneOffset.UTC).toEpochMilli();

	private static Calendar utcCalendar(long millis) {
		Calendar c = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		c.setTimeInMillis(millis);
		return c;
	}

	@Test
	public void testDateConverter() {
		DateConverter c = new DateConverter();
		c.setZoneId(UTC);
		assertEquals(new Date(MILLIS), c.convertValue(MILLIS, null));
		assertEquals(new Date(MILLIS), c.convertValue(Instant.ofEpochMilli(MILLIS), null));
		assertEquals(new Date(MILLIS), c.convertValue(utcCalendar(MILLIS), null));
		assertEquals(new Date(MILLIS), c.convertValue(LDT, null));
		assertEquals(new Date(LDT.toLocalDate().atStartOfDay(UTC).toInstant().toEpochMilli()), c.convertValue(LDT.toLocalDate(), null));

		Calendar local = Calendar.getInstance();
		local.clear();
		local.set(2020, Calendar.MARCH, 4, 5, 6, 7);
		assertEquals(local.getTime(), c.convertValue("2020-03-04 05:06:07", null));
		assertEquals(local.getTime(), c.convertValue("20200304050607", null));
		assertEquals(local.getTime(), c.convertValue(new int[] { 2020, 3, 4, 5, 6, 7 }, null));
		local.clear();
		local.set(2020, Calendar.MARCH, 4);
		assertEquals(local.getTime(), c.convertValue("2020-03-04", null));
		assertEquals(local.getTime(), c.convertValue(new int[] { 2020, 3, 4 }, null));
		assertThrows(IllegalArgumentException.class, () -> c.convertValue(new int[] { 2020 }, null));

		c.setDatePatterns(new String[] { "dd/MM/yyyy" });
		assertEquals(local.getTime(), c.convertValue("04/03/2020", null));
		Date dft = new Date(0);
		assertSame(dft, c.convertValue(null, dft));
	}

	@Test
	public void testCalendarConverter() {
		CalendarConverter c = new CalendarConverter();
		c.setTimeZone(TimeZone.getTimeZone("UTC"));
		assertEquals(MILLIS, c.convertValue(MILLIS, null).getTimeInMillis());
		assertEquals(MILLIS, c.convertValue(Instant.ofEpochMilli(MILLIS), null).getTimeInMillis());
		assertEquals(MILLIS, c.convertValue(new Date(MILLIS), null).getTimeInMillis());
		assertEquals(MILLIS, c.convertValue(LDT, null).getTimeInMillis());
		assertEquals(LDT.toLocalDate().atStartOfDay(UTC).toInstant().toEpochMilli(),
				c.convertValue(LDT.toLocalDate(), null).getTimeInMillis());
		assertEquals(TimeZone.getTimeZone("UTC"), c.convertValue(MILLIS, null).getTimeZone());

		c.setDatePattern("yyyy-MM-dd");
		Calendar parsed = c.convertValue("2020-03-04", null);
		assertEquals(2020, parsed.get(Calendar.YEAR));
		assertEquals(Calendar.MARCH, parsed.get(Calendar.MONTH));
		assertEquals(4, parsed.get(Calendar.DAY_OF_MONTH));
		assertNull(c.convertValue(null, null));
	}

	@Test
	public void testLocalDateConverter() {
		LocalDateConverter c = new LocalDateConverter();
		c.setZoneId(UTC);
		LocalDate expected = LDT.toLocalDate();
		assertEquals(expected, c.convertValue(MILLIS, null));
		assertEquals(expected, c.convertValue(Instant.ofEpochMilli(MILLIS), null));
		assertEquals(expected, c.convertValue(new Date(MILLIS), null));
		assertEquals(expected, c.convertValue(utcCalendar(MILLIS), null));
		assertEquals(expected, c.convertValue(LDT, null));
		assertEquals(expected, c.convertValue("2020-03-04", null));
		c.setDateFormatter(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		assertEquals(expected, c.convertValue("04/03/2020", null));
	}

	@Test
	public void testLocalDateTimeConverter() {
		LocalDateTimeConverter c = new LocalDateTimeConverter();
		c.setZoneId(UTC);
		assertEquals(LDT, c.convertValue(MILLIS, null));
		assertEquals(LDT, c.convertValue(Instant.ofEpochMilli(MILLIS), null));
		assertEquals(LDT, c.convertValue(new Date(MILLIS), null));
		assertEquals(LDT, c.convertValue(utcCalendar(MILLIS), null));
		assertEquals(LDT.toLocalDate().atStartOfDay(), c.convertValue(LDT.toLocalDate(), null));
		assertEquals(LDT, c.convertValue("2020-03-04T05:06:07", null));
		c.setDateTimeFormatter(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
		assertEquals(LDT, c.convertValue("2020/03/04 05:06:07", null));
	}

	@Test
	public void testLocalTimeConverter() {
		LocalTimeConverter c = new LocalTimeConverter();
		c.setZoneId(UTC);
		LocalTime expected = LDT.toLocalTime();
		assertEquals(expected, c.convertValue(MILLIS, null));
		assertEquals(expected, c.convertValue(Instant.ofEpochMilli(MILLIS), null));
		assertEquals(expected, c.convertValue(new Date(MILLIS), null));
		assertEquals(expected, c.convertValue(utcCalendar(MILLIS), null));
		assertEquals(expected, c.convertValue(LDT, null));
		assertEquals(expected, c.convertValue("05:06:07", null));
		c.setTimeFormatter(DateTimeFormatter.ofPattern("HH-mm-ss"));
		assertEquals(expected, c.convertValue("05-06-07", null));
	}

}
