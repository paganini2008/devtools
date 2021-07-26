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
package com.github.paganini2008.devtools.cron4j.parser;

import com.github.paganini2008.devtools.cron4j.cron.CronExpression;
import com.github.paganini2008.devtools.cron4j.cron.Day;
import com.github.paganini2008.devtools.cron4j.cron.TheHour;

/**
 * 
 * HourOption
 *
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public class HourOption implements CronOption {

	private final String value;

	public HourOption(String value) {
		this.value = value;
	}

	@Override
	public CronExpression join(CronExpression cronExpression) {
		final Day day = (Day) cronExpression;
		try {
			return day.hour(Integer.parseInt(value));
		} catch (NumberFormatException ignored) {
		}
		if (value.equals("*")) {
			return day.everyHour();
		}
		String[] args = value.split(",");
		TheHour hour = null;
		for (String arg : args) {
			if (hour != null) {
				hour = setHour(arg, hour);
			} else {
				hour = setHour(arg, day);
			}
		}
		return hour;
	}

	private TheHour setHour(String cron, TheHour hour) {
		if (cron.contains("-") && cron.contains("/")) {
			String[] args = cron.split("[\\-\\/]", 3);
			return hour.andHour(Integer.parseInt(args[0])).toHour(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
		} else if (cron.contains("-") && !cron.contains("/")) {
			String[] args = cron.split("-", 2);
			return hour.andHour(Integer.parseInt(args[0])).toHour(Integer.parseInt(args[1]));
		} else if (!cron.contains("-") && cron.contains("/")) {
			String[] args = cron.split("\\/", 2);
			int start = getStartValue(args[0]);
			return hour.andHour(start).toHour(23, Integer.parseInt(args[1]));
		} else {
			return hour.andHour(Integer.parseInt(cron));
		}
	}

	private TheHour setHour(String cron, Day day) {
		if (cron.contains("-") && cron.contains("/")) {
			String[] args = cron.split("[\\-\\/]", 3);
			return day.hour(Integer.parseInt(args[0])).toHour(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
		} else if (cron.contains("-") && !cron.contains("/")) {
			String[] args = cron.split("-", 2);
			return day.hour(Integer.parseInt(args[0])).toHour(Integer.parseInt(args[1]));
		} else if (!cron.contains("-") && cron.contains("/")) {
			String[] args = cron.split("\\/", 2);
			int start = getStartValue(args[0]);
			return day.hour(start).toHour(23, Integer.parseInt(args[1]));
		} else {
			return day.hour(Integer.parseInt(cron));
		}
	}

	private int getStartValue(String cron) {
		try {
			return Integer.parseInt(cron);
		} catch (RuntimeException e) {
			return 0;
		}
	}

}
