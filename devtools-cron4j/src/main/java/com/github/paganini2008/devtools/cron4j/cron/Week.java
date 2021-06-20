/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

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
import java.util.Iterator;
import java.util.function.Function;

/**
 * 
 * Week
 *
 * @author Fred Feng
 * @version 1.0
 */
public interface Week extends Iterator<Week>, CronExpression {

	int getYear();

	int getMonth();

	int getWeek();

	int getWeekOfYear();

	default Day everyDay() {
		return everyDay(1);
	}

	default Day everyDay(int interval) {
		return everyDay(Calendar.SUNDAY, Calendar.SATURDAY, interval);
	}

	default Day everyDay(int from, int to, int interval) {
		return everyDay(w -> from, w -> to, interval);
	}

	Day everyDay(Function<Week, Integer> from, Function<Week, Integer> to, int interval);

	TheDayOfWeek day(int dayOfWeek);

	default TheDayOfWeek everyWeekday() {
		return Mon().toFri();
	}

	default TheDayOfWeek Sun() {
		return day(Calendar.SUNDAY);
	}

	default TheDayOfWeek Mon() {
		return day(Calendar.MONDAY);
	}

	default TheDayOfWeek Tues() {
		return day(Calendar.TUESDAY);
	}

	default TheDayOfWeek Wed() {
		return day(Calendar.WEDNESDAY);
	}

	default TheDayOfWeek Thur() {
		return day(Calendar.THURSDAY);
	}

	default TheDayOfWeek Fri() {
		return day(Calendar.FRIDAY);
	}

	default TheDayOfWeek Sat() {
		return day(Calendar.SATURDAY);
	}

}
