/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

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

/**
 * 
 * TheDayOfWeek
 *
 * @author Fred Feng
 * 
 * 
 * @since 2.0.1
 */
public interface TheDayOfWeek extends Day {

	TheDayOfWeek andDay(int day);

	default TheDayOfWeek toDay(int day) {
		return toDay(day, 1);
	}

	TheDayOfWeek toDay(int day, int interval);

	default TheDayOfWeek toTues() {
		return toDay(Calendar.TUESDAY);
	}

	default TheDayOfWeek toWed() {
		return toDay(Calendar.WEDNESDAY);
	}

	default TheDayOfWeek toThur() {
		return toDay(Calendar.THURSDAY);
	}

	default TheDayOfWeek toFri() {
		return toDay(Calendar.FRIDAY);
	}

	default TheDayOfWeek toSat() {
		return toDay(Calendar.SATURDAY);
	}

	default TheDayOfWeek andSun() {
		return andDay(Calendar.SUNDAY);
	}

	default TheDayOfWeek andMon() {
		return andDay(Calendar.MONDAY);
	}

	default TheDayOfWeek andTues() {
		return andDay(Calendar.TUESDAY);
	}

	default TheDayOfWeek andWed() {
		return andDay(Calendar.WEDNESDAY);
	}

	default TheDayOfWeek andThur() {
		return andDay(Calendar.THURSDAY);
	}

	default TheDayOfWeek andFri() {
		return andDay(Calendar.FRIDAY);
	}

	default TheDayOfWeek andSat() {
		return andDay(Calendar.SATURDAY);
	}
}
