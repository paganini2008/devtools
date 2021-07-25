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
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.function.Function;

/**
 * 
 * Year
 *
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public interface Year extends Iterator<Year>, CronExpression {

	static final int MAX_YEAR = 9999;

	int getYear();

	int getWeekCount();

	int getLastDay();

	TheDay day(int day);

	default Day lastDay() {
		return day(getLastDay());
	}

	TheWeek week(int week);

	Week lastWeek();

	default boolean isLeapYear() {
		GregorianCalendar calendar = new GregorianCalendar();
		return calendar.isLeapYear(getYear());
	}

	default Month everyMonth() {
		return everyMonth(1);
	}

	default Month everyMonth(int interval) {
		return everyMonth(Calendar.JANUARY, Calendar.DECEMBER, interval);
	}

	Month everyMonth(Function<Year, Integer> from, Function<Year, Integer> to, int interval);

	default Month everyMonth(int from, int to, int interval) {
		return everyMonth(y -> from, y -> to, interval);
	}

	TheMonth month(int month);

	default TheMonth Jan() {
		return month(Calendar.JANUARY);
	}

	default TheMonth Feb() {
		return month(Calendar.FEBRUARY);
	}

	default TheMonth Mar() {
		return month(Calendar.MARCH);
	}

	default TheMonth Apr() {
		return month(Calendar.APRIL);
	}

	default TheMonth May() {
		return month(Calendar.MAY);
	}

	default TheMonth June() {
		return month(Calendar.JUNE);
	}

	default TheMonth July() {
		return month(Calendar.JULY);
	}

	default TheMonth Aug() {
		return month(Calendar.AUGUST);
	}

	default TheMonth Sept() {
		return month(Calendar.SEPTEMBER);
	}

	default TheMonth Oct() {
		return month(Calendar.OCTOBER);
	}

	default TheMonth Nov() {
		return month(Calendar.NOVEMBER);
	}

	default TheMonth Dec() {
		return month(Calendar.DECEMBER);
	}
}
