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
package com.github.paganini2008.devtools.cron4j.cron;

import java.util.Calendar;

import com.github.paganini2008.devtools.time.DateUtils;

/**
 * 
 * CalendarAssert
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public abstract class CalendarAssert {

	public static void checkYear(int year) {
		int thisYear = DateUtils.getYear();
		if (year < thisYear) {
			throw new IllegalArgumentException("Year '" + year + "' is past.");
		}
		if (year > Year.MAX_YEAR) {
			throw new IllegalArgumentException("Year '" + year + "' is greater than the Max Year " + Year.MAX_YEAR);
		}
	}

	public static void checkMonth(int month) {
		if (month < Calendar.JANUARY || month > Calendar.DECEMBER) {
			throw new IllegalArgumentException("Month's range is " + Calendar.JANUARY + " to " + Calendar.DECEMBER);
		}
	}

	public static void checkDayOfYear(Year year, int day) {
		if (day < 1 || day > year.getLastDay()) {
			throw new IllegalArgumentException("Day's range is 1 to " + year.getLastDay());
		}
	}

	public static void checkWeekOfYear(Year year, int week) {
		if (week < 1 || week > year.getWeekCount()) {
			throw new IllegalArgumentException("Week's range is 1 to " + year.getWeekCount());
		}
	}

	public static void checkWeekOfMonth(Month month, int week) {
		if (week < 1 || week > month.getWeekCount()) {
			throw new IllegalArgumentException("Week's range is 1 to " + month.getWeekCount());
		}
	}

	public static void checkDayOfMonth(Month month, int day) {
		if (day < 1 || day > month.getLastDay()) {
			throw new IllegalArgumentException("Day's range of this month is 1 to " + month.getLastDay());
		}
	}

	public static void checkDayOfWeek(int dayOfWeek) {
		if (dayOfWeek < Calendar.SUNDAY || dayOfWeek > Calendar.SATURDAY) {
			throw new IllegalArgumentException("WeekDay's range is " + Calendar.SUNDAY + " to " + Calendar.SATURDAY);
		}
	}

	public static void checkHourOfDay(int hour) {
		if (hour < 0 || hour > 23) {
			throw new IllegalArgumentException("Hour's range is 0 to 23.");
		}
	}

	public static void checkMinute(int minute) {
		if (minute < 0 || minute > 59) {
			throw new IllegalArgumentException("Minute's range is 0 to 59.");
		}
	}

	public static void checkSecond(int second) {
		if (second < 0 || second > 59) {
			throw new IllegalArgumentException("Second's range is 0 to 59.");
		}
	}

}
