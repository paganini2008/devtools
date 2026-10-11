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
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;

/**
 * InstantUtilsTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class InstantUtilsTest {

	private static final Instant DEFAULT = Instant.EPOCH;
	private static final ZoneId NEW_YORK = ZoneId.of("America/New_York");

	@Test
	public void testFromMillisDateCalendar() {
		assertEquals(Instant.ofEpochMilli(1000L), InstantUtils.toInstant(1000L));
		assertNull(InstantUtils.toInstant((Long) null));
		assertEquals(DEFAULT, InstantUtils.toInstant((Long) null, DEFAULT));

		assertEquals(Instant.ofEpochMilli(2000L), InstantUtils.toInstant(new Date(2000L)));
		assertNull(InstantUtils.toInstant((Date) null));
		assertEquals(DEFAULT, InstantUtils.toInstant((Date) null, DEFAULT));

		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		c.setTimeInMillis(3000L);
		assertEquals(Instant.ofEpochMilli(3000L), InstantUtils.toInstant(c));
		assertNull(InstantUtils.toInstant((Calendar) null));
		assertEquals(DEFAULT, InstantUtils.toInstant((Calendar) null, DEFAULT));
	}

	@Test
	public void testFromLocalDateTime() {
		LocalDateTime ldt = LocalDateTime.of(2021, 1, 15, 12, 0, 0);
		assertEquals(Instant.parse("2021-01-15T12:00:00Z"), InstantUtils.toInstant(ldt, ZoneId.of("UTC")));
		// The offset must be the one at the given date time, not the current offset (DST)
		assertEquals(Instant.parse("2021-01-15T17:00:00Z"), InstantUtils.toInstant(ldt, NEW_YORK));
		assertEquals(Instant.parse("2021-07-15T16:00:00Z"), InstantUtils.toInstant(LocalDateTime.of(2021, 7, 15, 12, 0), NEW_YORK));
		assertEquals(ldt.atZone(ZoneId.systemDefault()).toInstant(), InstantUtils.toInstant(ldt));
		assertNull(InstantUtils.toInstant((LocalDateTime) null));
		assertEquals(DEFAULT, InstantUtils.toInstant((LocalDateTime) null, NEW_YORK, DEFAULT));
	}

	@Test
	public void testFromLocalDate() {
		assertEquals(Instant.parse("2021-01-15T05:00:00Z"), InstantUtils.toInstant(LocalDate.of(2021, 1, 15), NEW_YORK));
		assertEquals(Instant.parse("2021-07-15T04:00:00Z"), InstantUtils.toInstant(LocalDate.of(2021, 7, 15), NEW_YORK));
		LocalDate ld = LocalDate.of(2021, 1, 15);
		assertEquals(ld.atStartOfDay(ZoneId.systemDefault()).toInstant(), InstantUtils.toInstant(ld));
		assertNull(InstantUtils.toInstant((LocalDate) null));
		assertEquals(DEFAULT, InstantUtils.toInstant((LocalDate) null, NEW_YORK, DEFAULT));
	}

}
