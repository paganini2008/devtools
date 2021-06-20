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

import java.util.Iterator;
import java.util.function.Function;

/**
 * 
 * Day
 *
 * @author Fred Feng
 * @version 1.0
 */
public interface Day extends Iterator<Day>, CronExpression {

	int getYear();

	int getMonth();

	int getDay();

	int getDayOfWeek();

	int getDayOfYear();

	default Hour everyHour() {
		return everyHour(1);
	}

	default Hour everyHour(int interval) {
		return everyHour(0, 23, interval);
	}

	default Hour everyHour(int from, int to, int interval) {
		return everyHour(d -> from, d -> to, interval);
	}

	TheHour hour(int hourOfDay);

	default TheMinute at(int hourOfDay, int minute) {
		return hour(hourOfDay).minute(minute);
	}

	default TheSecond at(int hourOfDay, int minute, int second) {
		return hour(hourOfDay).minute(minute).second(second);
	}

	Hour everyHour(Function<Day, Integer> from, Function<Day, Integer> to, int interval);

}
