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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Year;
import java.time.YearMonth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * TimeAssertTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class TimeAssertTest {

	@Test
	public void testValidateYear() {
		assertDoesNotThrow(() -> TimeAssert.validateYear(DateUtils.MIN_YEAR));
		assertDoesNotThrow(() -> TimeAssert.validateYear(DateUtils.MAX_YEAR));
		// MIN_YEAR/MAX_YEAR are only defaults for random dates, years before 1970 are valid
		assertDoesNotThrow(() -> TimeAssert.validateYear(1965));
		assertDoesNotThrow(() -> TimeAssert.validateYear(1900));
		assertDoesNotThrow(() -> TimeAssert.validateYear(10000));
		assertDoesNotThrow(() -> TimeAssert.validateYear(Year.MIN_VALUE));
		assertDoesNotThrow(() -> TimeAssert.validateYear(Year.MAX_VALUE));
		assertThrows(IllegalArgumentException.class, () -> TimeAssert.validateYear(Year.MIN_VALUE - 1));
		assertThrows(IllegalArgumentException.class, () -> TimeAssert.validateYear(Year.MAX_VALUE + 1));
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 13, -1 })
	public void testValidateMonthInvalid(int month) {
		assertThrows(IllegalArgumentException.class, () -> TimeAssert.validateMonth(month));
	}

	@Test
	public void testValidateMonth() {
		assertDoesNotThrow(() -> TimeAssert.validateMonth(1));
		assertDoesNotThrow(() -> TimeAssert.validateMonth(12));
	}

	@Test
	public void testValidateDayOfYear() {
		assertDoesNotThrow(() -> TimeAssert.validateDayOfYear(Year.of(2020), 366));
		assertDoesNotThrow(() -> TimeAssert.validateDayOfYear(Year.of(1964), 366));
		assertThrows(IllegalArgumentException.class, () -> TimeAssert.validateDayOfYear(Year.of(1900), 366));
		assertThrows(IllegalArgumentException.class, () -> TimeAssert.validateDayOfYear(Year.of(2021), 366));
		assertThrows(IllegalArgumentException.class, () -> TimeAssert.validateDayOfYear(Year.of(2021), 0));
		assertThrows(IllegalArgumentException.class, () -> TimeAssert.validateDayOfYear(null, 1));
	}

	@Test
	public void testValidateDayOfMonth() {
		assertDoesNotThrow(() -> TimeAssert.validateDayOfMonth(YearMonth.of(2020, 2), 29));
		assertThrows(IllegalArgumentException.class, () -> TimeAssert.validateDayOfMonth(YearMonth.of(2021, 2), 29));
		assertThrows(IllegalArgumentException.class, () -> TimeAssert.validateDayOfMonth(YearMonth.of(2021, 2), 0));
		assertThrows(IllegalArgumentException.class, () -> TimeAssert.validateDayOfMonth((YearMonth) null, 1));
		assertDoesNotThrow(() -> TimeAssert.validateDayOfMonth(2021, 4, 30));
		assertThrows(IllegalArgumentException.class, () -> TimeAssert.validateDayOfMonth(2021, 4, 31));
		assertThrows(IllegalArgumentException.class, () -> TimeAssert.validateDayOfMonth(2021, 13, 1));
	}

	@Test
	public void testValidateTime() {
		assertDoesNotThrow(() -> TimeAssert.validateTime(0, 0, 0));
		assertDoesNotThrow(() -> TimeAssert.validateTime(23, 59, 59));
		assertThrows(IllegalArgumentException.class, () -> TimeAssert.validateTime(24, 0, 0));
		assertThrows(IllegalArgumentException.class, () -> TimeAssert.validateTime(0, 60, 0));
		assertThrows(IllegalArgumentException.class, () -> TimeAssert.validateTime(0, 0, 60));
		assertThrows(IllegalArgumentException.class, () -> TimeAssert.validateTime(0, 0, -1));
	}

	@Test
	public void testValidateHourAndMinute() {
		assertDoesNotThrow(() -> TimeAssert.validateHourOfDay(23));
		assertThrows(IllegalArgumentException.class, () -> TimeAssert.validateHourOfDay(-1));
		assertDoesNotThrow(() -> TimeAssert.validateMinuteOrSecond(59));
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> TimeAssert.validateMinuteOrSecond(60));
		assertEquals("Out of time range of one minute or one second. Input: 60", e.getMessage());
	}

	@Test
	public void testYearUtils() {
		assertEquals(Year.of(2021), YearUtils.of(2021));
		assertEquals(1970, YearUtils.MIN_YEAR.getValue());
		assertEquals(9999, YearUtils.MAX_YEAR.getValue());
		assertEquals(Year.of(1969), YearUtils.of(1969));
		assertEquals(Year.of(1900), YearUtils.of(1900));
		assertThrows(IllegalArgumentException.class, () -> YearUtils.of(Year.MAX_VALUE + 1));
	}

	@Test
	public void testYearMonthUtils() {
		assertEquals(YearMonth.of(2021, 3), YearMonthUtils.of(2021, 3));
		assertThrows(IllegalArgumentException.class, () -> YearMonthUtils.of(2021, 0));
		assertEquals(YearMonth.of(1965, 2), YearMonthUtils.of(1965, 2));
		assertEquals(YearMonth.of(10000, 1), YearMonthUtils.of(10000, 1));
		assertEquals(YearMonth.of(2021, 3), YearMonthUtils.toYearMonth(Year.of(2021), java.time.Month.MARCH));
		assertEquals(YearMonth.of(1900, 3), YearMonthUtils.toYearMonth(Year.of(1900), java.time.Month.MARCH));
		assertThrows(IllegalArgumentException.class, () -> YearMonthUtils.toYearMonth(null, java.time.Month.MARCH));
		assertThrows(IllegalArgumentException.class, () -> YearMonthUtils.toYearMonth(Year.of(2021), null));
	}

	@Test
	public void testPatternNotFoundException() {
		PatternNotFoundException e = new PatternNotFoundException("msg");
		assertEquals("msg", e.getMessage());
		Throwable cause = new IllegalStateException();
		e = new PatternNotFoundException("msg2", cause);
		assertEquals("msg2", e.getMessage());
		assertEquals(cause, e.getCause());
	}

}
