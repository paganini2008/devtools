/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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
package com.github.paganini2008.devtools.date;

import static com.github.paganini2008.devtools.date.DateUtils.YEAR_END_WITH;
import static com.github.paganini2008.devtools.date.DateUtils.YEAR_START_FROM;

import java.time.Year;
import java.time.YearMonth;

import com.github.paganini2008.devtools.Assert;

/**
 * 
 * TimeAsserts
 *
 * @author Fred Feng
 *
 * @since 2.0.4
 */
public abstract class TimeAsserts {

	public static void validateYear(int year) {
		Assert.outOfRange(year, YEAR_START_FROM, YEAR_END_WITH, "Out of year range. Input: " + year);
	}

	public static void validateMonth(int month) {
		Assert.outOfRange(month, 1, 12, "Out of month range. Input: " + month);
	}

	public static void validateDayOfYear(Year year, int dayOfYear) {
		Assert.outOfRange(dayOfYear, 1, year.isLeap() ? 366 : 365, "Out of day range of year: " + year + ". Input: " + dayOfYear);
	}

	public static void validateDayOfMonth(YearMonth yearMonth, int dayOfMonth) {
		Assert.outOfRange(dayOfMonth, 1, yearMonth.atEndOfMonth().getDayOfMonth(),
				"Out of day range of month: " + yearMonth + ". Input: " + dayOfMonth);
	}

	public static void validateDayOfMonth(int year, int month, int dayOfMonth) {
		validateDayOfMonth(YearMonthUtils.of(year, month), dayOfMonth);
	}

	public static void validateTime(int hourOfDay, int minute, int second) {
		Assert.outOfRange(hourOfDay, 0, 23, "Out of hour range of a day. Input: " + hourOfDay);
		Assert.outOfRange(minute, 0, 59, "Out of minute range of one hour. Input: " + minute);
		Assert.outOfRange(second, 0, 59, "Out of second range of one minute. Input: " + second);
	}

	public static void validateHourOfDay(int hourOfDay) {
		Assert.outOfRange(hourOfDay, 0, 23, "Out of hour range of a day. Input: " + hourOfDay);
	}

	public static void validateMinuteOrSecond(int timeValue) {
		Assert.outOfRange(timeValue, 0, 59, "Out of time range of one minute or one hour. Input: " + timeValue);
	}

}
