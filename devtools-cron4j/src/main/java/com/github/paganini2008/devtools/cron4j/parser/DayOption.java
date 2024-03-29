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
package com.github.paganini2008.devtools.cron4j.parser;

import com.github.paganini2008.devtools.cron4j.cron.CronExpression;
import com.github.paganini2008.devtools.cron4j.cron.Month;
import com.github.paganini2008.devtools.cron4j.cron.TheDay;

/**
 * 
 * DayOption
 *
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public class DayOption implements CronOption {

	private final String value;

	public DayOption(String value) {
		this.value = value;
	}

	@Override
	public CronExpression join(CronExpression cronExpression) {
		final Month month = (Month) cronExpression;
		try {
			return month.day(Integer.parseInt(value));
		} catch (NumberFormatException ignored) {
		}
		if (value.equals("*")) {
			return month.everyDay();
		} else if (value.equals("L")) {
			return month.lastDay();
		} else if (value.equals("LW")) {
			return month.lastWeek().Fri();
		} else if (value.endsWith("W")) {
			int dayOfMonth = Integer.parseInt(value.substring(0, value.lastIndexOf('W')));
			return month.latestWeekday(dayOfMonth);
		}
		String[] args = value.split(",");
		TheDay day = null;
		for (String arg : args) {
			if (day != null) {
				day = setDay(arg, day, month);
			} else {
				day = setDay(arg, month);
			}
		}
		return day;
	}

	private TheDay setDay(String cron, TheDay day, Month month) {
		if (cron.contains("-") && cron.contains("/")) {
			String[] args = cron.split("[\\-\\/]", 3);
			return day.andDay(Integer.parseInt(args[0])).toDay(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
		} else if (cron.contains("-") && !cron.contains("/")) {
			String[] args = cron.split("-", 2);
			return day.andDay(Integer.parseInt(args[0])).toDay(Integer.parseInt(args[1]));
		} else if (!cron.contains("-") && cron.contains("/")) {
			String[] args = cron.split("\\/", 2);
			int start = getStartValue(args[0]);
			return day.andDay(start).toDay(month.getLastDay(), Integer.parseInt(args[1]));
		} else {
			return day.andDay(Integer.parseInt(cron));
		}
	}

	private TheDay setDay(String cron, Month month) {
		if (cron.contains("-") && cron.contains("/")) {
			String[] args = cron.split("[\\-\\/]", 3);
			return month.day(Integer.parseInt(args[0])).toDay(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
		} else if (cron.contains("-") && !cron.contains("/")) {
			String[] args = cron.split("-", 2);
			return month.day(Integer.parseInt(args[0])).toDay(Integer.parseInt(args[1]));
		} else if (!cron.contains("-") && cron.contains("/")) {
			String[] args = cron.split("\\/", 2);
			int start = getStartValue(args[0]);
			return month.day(start).toDay(month.getLastDay(), Integer.parseInt(args[1]));
		} else {
			return month.day(Integer.parseInt(cron));
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
