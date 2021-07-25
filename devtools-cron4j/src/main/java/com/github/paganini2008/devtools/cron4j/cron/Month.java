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

import java.util.Iterator;
import java.util.function.Function;

/**
 * 
 * Month
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
public interface Month extends Iterator<Month>, CronExpression {

	int getYear();

	int getMonth();

	int getLastDay();

	int getLatestWeekday(int dayOfMonth);

	int getWeekCount();

	default Day everyDay() {
		return everyDay(1);
	}

	default Day everyDay(int interval) {
		return everyDay(m -> 1, m -> {
			return m.getLastDay();
		}, interval);
	}

	default Day everyDay(int from, int to, int interval) {
		return everyDay(m -> from, m -> to, interval);
	}

	TheDay day(int day);

	Day lastDay();

	Day latestWeekday(int dayOfMonth);

	Day everyDay(Function<Month, Integer> from, Function<Month, Integer> to, int interval);

	TheWeek week(int week);

	TheDayOfWeekInMonth dayOfWeek(int week, int dayOfWeek);

	default TheDayOfWeekInMonth lastDayOfWeek(int dayOfWeek) {
		return dayOfWeek(getWeekCount(), dayOfWeek);
	}

	Week lastWeek();

	default Week everyWeek() {
		return everyWeek(1);
	}

	default Week everyWeek(int interval) {
		return everyWeek(m -> 1, m -> {
			return m.getWeekCount();
		}, interval);
	}

	default Week everyWeek(int from, int to, int interval) {
		return everyWeek(m -> from, m -> to, interval);
	}

	Week everyWeek(Function<Month, Integer> from, Function<Month, Integer> to, int interval);

}
