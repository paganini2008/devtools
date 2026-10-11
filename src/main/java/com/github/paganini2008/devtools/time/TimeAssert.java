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

import java.time.Year;
import java.time.YearMonth;

import com.github.paganini2008.devtools.Assert;

/**
 * 
 * Argument checks for date and time fields, throwing {@link IllegalArgumentException} on invalid values.
 *
 * @author Fred Feng
 * @date 2021-11-21
 *
 * @since 2.0.4
 */
public abstract class TimeAssert {

	/**
	 * Checks the year is within the range supported by {@code java.time} ({@link Year#MIN_VALUE} to
	 * {@link Year#MAX_VALUE}). Note that {@link DateUtils#MIN_YEAR} and {@link DateUtils#MAX_YEAR} are only
	 * default bounds for generating random dates and are not enforced here, so years before 1970 are valid.
	 *
	 * <pre>
	 * TimeAssert.validateYear(1900)       = OK
	 * TimeAssert.validateYear(2021)       = OK
	 * TimeAssert.validateYear(1000000000) = IllegalArgumentException
	 * </pre>
	 *
	 * @throws IllegalArgumentException if out of range
	 */
	public static void validateYear(int year) {
		checkRange(year, Year.MIN_VALUE, Year.MAX_VALUE, "Out of year range. Input: " + year);
	}

	/**
	 * Checks the month is within [1, 12].
	 *
	 * @throws IllegalArgumentException if out of range
	 */
	public static void validateMonth(int month) {
		checkRange(month, 1, 12, "Out of month range. Input: " + month);
	}

	/**
	 * Checks the day is a valid day of the given year (leap years have 366 days).
	 *
	 * @throws IllegalArgumentException if the year is null or the day is out of range
	 */
	public static void validateDayOfYear(Year year, int dayOfYear) {
		Assert.isNull(year, "Year must not be null.");
		checkRange(dayOfYear, 1, year.length(), "Out of day range of year: " + year + ". Input: " + dayOfYear);
	}

	/**
	 * Checks the day is a valid day of the given month.
	 *
	 * @throws IllegalArgumentException if the month is null or the day is out of range
	 */
	public static void validateDayOfMonth(YearMonth yearMonth, int dayOfMonth) {
		Assert.isNull(yearMonth, "YearMonth must not be null.");
		checkRange(dayOfMonth, 1, yearMonth.lengthOfMonth(), "Out of day range of month: " + yearMonth + ". Input: " + dayOfMonth);
	}

	/**
	 * Checks the year, the month and the day of month together.
	 *
	 * @throws IllegalArgumentException if any of them is out of range
	 */
	public static void validateDayOfMonth(int year, int month, int dayOfMonth) {
		validateDayOfMonth(YearMonthUtils.of(year, month), dayOfMonth);
	}

	/**
	 * Checks hour (0-23), minute (0-59) and second (0-59).
	 *
	 * @throws IllegalArgumentException if any of them is out of range
	 */
	public static void validateTime(int hourOfDay, int minute, int second) {
		validateHourOfDay(hourOfDay);
		checkRange(minute, 0, 59, "Out of minute range of one hour. Input: " + minute);
		checkRange(second, 0, 59, "Out of second range of one minute. Input: " + second);
	}

	/**
	 * Checks the hour is within [0, 23].
	 *
	 * @throws IllegalArgumentException if out of range
	 */
	public static void validateHourOfDay(int hourOfDay) {
		checkRange(hourOfDay, 0, 23, "Out of hour range of a day. Input: " + hourOfDay);
	}

	/**
	 * Checks a minute or second value is within [0, 59].
	 *
	 * @throws IllegalArgumentException if out of range
	 */
	public static void validateMinuteOrSecond(int timeValue) {
		checkRange(timeValue, 0, 59, "Out of time range of one minute or one second. Input: " + timeValue);
	}

	private static void checkRange(int value, int minValue, int maxValue, String msg) {
		if (value < minValue || value > maxValue) {
			throw new IllegalArgumentException(msg);
		}
	}

}
