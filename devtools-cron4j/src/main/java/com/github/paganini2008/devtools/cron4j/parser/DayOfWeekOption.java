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

import java.util.Calendar;

import com.github.paganini2008.devtools.cron4j.cron.CalendarUtils;
import com.github.paganini2008.devtools.cron4j.cron.CronExpression;
import com.github.paganini2008.devtools.cron4j.cron.Day;
import com.github.paganini2008.devtools.cron4j.cron.Month;
import com.github.paganini2008.devtools.cron4j.cron.TheDayOfWeek;
import com.github.paganini2008.devtools.cron4j.cron.ThisDayOfWeekInMonth;

/**
 * 
 * DayOfWeekOption
 *
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public class DayOfWeekOption implements CronOption {

	private final String value;

	public DayOfWeekOption(String value) {
		this.value = value;
	}

	@Override
	public CronExpression join(CronExpression cronExpression) {
		final Month month = (Month) cronExpression;
		try {
			return month.everyWeek().day(Integer.parseInt(value));
		} catch (NumberFormatException e) {
			try {
				return month.everyWeek().day(CalendarUtils.getDayOfWeekValue(value));
			} catch (MalformedCronException ignored) {
			}
		}
		if (value.equals("*")) {
			return month.everyWeek().everyDay();
		}
		String[] args = value.split(",");
		Day dayOfWeek = null;
		for (String arg : args) {
			try {
				if (dayOfWeek != null) {
					dayOfWeek = setDayOfWeek(arg, dayOfWeek);
				} else {
					dayOfWeek = setDayOfWeek(arg, month);
				}
			} catch (ClassCastException e) {
				throw new MalformedCronException(value, e);
			}
		}
		return dayOfWeek;
	}

	private Day setDayOfWeek(String cron, Day day) {
		if (cron.equals("L")) {
			return ((ThisDayOfWeekInMonth) day).andLastSat();
		} else if (cron.equals("LW")) {
			return ((ThisDayOfWeekInMonth) day).andLastFri();
		} else if (cron.endsWith("L")) {
			return ((ThisDayOfWeekInMonth) day).andLast(Integer.parseInt(cron.substring(0, 1)));
		} else if (cron.contains("#")) {
			String[] args = cron.split("#", 2);
			return ((ThisDayOfWeekInMonth) day).and(Integer.parseInt(args[1]), getDayOfWeekValue(args[0]));
		} else if (cron.contains("-") && cron.contains("/")) {
			String[] args = cron.split("[\\-\\/]", 3);
			return ((TheDayOfWeek) day).andDay(getDayOfWeekValue(args[0])).toDay(getDayOfWeekValue(args[1]), Integer.parseInt(args[2]));
		} else if (cron.contains("-") && !cron.contains("/")) {
			String[] args = cron.split("-", 2);
			return ((TheDayOfWeek) day).andDay(getDayOfWeekValue(args[0])).toDay(getDayOfWeekValue(args[1]));
		} else if (!cron.contains("-") && cron.contains("/")) {
			String[] args = cron.split("\\/", 2);
			return ((TheDayOfWeek) day).andDay(getDayOfWeekValue(args[0])).toDay(Calendar.SATURDAY, Integer.parseInt(args[1]));
		} else {
			int dayOfWeek = getDayOfWeekValue(cron);
			return ((TheDayOfWeek) day).andDay(dayOfWeek);
		}
	}

	private Day setDayOfWeek(String cron, Month month) {
		if (cron.equals("L")) {
			return month.lastDayOfWeek(Calendar.SATURDAY);
		} else if (cron.equals("LW")) {
			return month.lastDayOfWeek(Calendar.FRIDAY);
		} else if (cron.endsWith("L")) {
			return month.lastDayOfWeek(Integer.parseInt(cron.substring(0, 1)));
		} else if (cron.contains("#")) {
			String[] args = cron.split("#", 2);
			return month.dayOfWeek(Integer.parseInt(args[1]), getDayOfWeekValue(args[0]));
		} else if (cron.contains("-") && cron.contains("/")) {
			String[] args = cron.split("[\\-\\/]", 3);
			return month.everyWeek().day(getDayOfWeekValue(args[0])).toDay(getDayOfWeekValue(args[1]), getDayOfWeekValue(args[2]));
		} else if (cron.contains("-") && !cron.contains("/")) {
			String[] args = cron.split("-", 2);
			return month.everyWeek().day(getDayOfWeekValue(args[0])).toDay(getDayOfWeekValue(args[1]));
		} else if (!cron.contains("-") && cron.contains("/")) {
			String[] args = cron.split("\\/", 2);
			return month.everyWeek().day(getDayOfWeekValue(args[0])).toDay(Calendar.SATURDAY, Integer.parseInt(args[1]));
		} else {
			int dayOfWeek = getDayOfWeekValue(cron);
			return month.everyWeek().day(dayOfWeek);
		}
	}

	private int getDayOfWeekValue(String cron) {
		try {
			return Integer.parseInt(cron);
		} catch (RuntimeException e) {
			return CalendarUtils.getDayOfWeekValue(cron);
		}
	}

}
