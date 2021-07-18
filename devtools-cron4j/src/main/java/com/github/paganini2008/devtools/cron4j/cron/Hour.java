/**
* Copyright 2018-2021 Fred Feng (paganini.fy@gmail.com)

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
 * Hour
 *
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public interface Hour extends Iterator<Hour>, CronExpression {

	int getYear();

	int getMonth();

	int getDay();

	int getHour();

	default Minute everyMinute() {
		return everyMinute(1);
	}

	default Minute everyMinute(int interval) {
		return everyMinute(0, 59, interval);
	}

	default Minute everyMinute(int from, int to, int interval) {
		return everyMinute(h -> from, h -> to, interval);
	}

	TheMinute minute(int minute);

	default TheSecond at(int minute, int second) {
		return minute(minute).second(second);
	}

	Minute everyMinute(Function<Hour, Integer> from, Function<Hour, Integer> to, int interval);

}
