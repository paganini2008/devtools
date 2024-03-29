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

import java.util.Calendar;

import com.github.paganini2008.devtools.cron4j.cron.CalendarUtils;
import com.github.paganini2008.devtools.cron4j.cron.CronExpression;
import com.github.paganini2008.devtools.cron4j.cron.TheMonth;
import com.github.paganini2008.devtools.cron4j.cron.Year;

/**
 * 
 * MonthOption
 *
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public class MonthOption implements CronOption {

	private final String value;

	public MonthOption(String value) {
		this.value = value;
	}

	@Override
	public CronExpression join(CronExpression cronExpression) {
		final Year year = (Year) cronExpression;
		try {
			return year.month(Integer.parseInt(value));
		} catch (NumberFormatException e) {
			try {
				return year.month(CalendarUtils.getMonthValue(value));
			} catch (MalformedCronException ignored) {
			}
		}
		if (value.equals("*")) {
			return year.everyMonth();
		}
		String[] args = value.split(",");
		TheMonth month = null;
		for (String arg : args) {
			if (month != null) {
				month = setMonth(arg, month);
			} else {
				month = setMonth(arg, year);
			}
		}
		return month;
	}

	private TheMonth setMonth(String cron, TheMonth month) {
		if (cron.contains("-") && cron.contains("/")) {
			String[] args = cron.split("[\\/\\-]", 3);
			return month.andMonth(Integer.parseInt(args[0])).toMonth(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
		} else if (cron.contains("-") && !cron.contains("/")) {
			String[] args = cron.split("-", 2);
			return month.andMonth(Integer.parseInt(args[0])).toMonth(Integer.parseInt(args[1]));
		} else if (!cron.contains("-") && cron.contains("/")) {
			String[] args = cron.split("\\/", 2);
			return month.andMonth(Integer.parseInt(args[0])).toMonth(Calendar.DECEMBER, Integer.parseInt(args[1]));
		} else {
			return month.andMonth(Integer.parseInt(cron));
		}
	}

	private TheMonth setMonth(String cron, Year year) {
		if (cron.contains("-") && cron.contains("/")) {
			String[] args = cron.split("[\\-\\/]", 3);
			return year.month(Integer.parseInt(args[0])).toMonth(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
		} else if (cron.contains("-") && !cron.contains("/")) {
			String[] args = cron.split("-", 2);
			return year.month(Integer.parseInt(args[0])).toMonth(Integer.parseInt(args[1]));
		} else if (!cron.contains("-") && cron.contains("/")) {
			String[] args = cron.split("\\/", 2);
			return year.month(Integer.parseInt(args[0])).toMonth(Calendar.DECEMBER, Integer.parseInt(args[1]));
		} else {
			return year.month(Integer.parseInt(cron));
		}
	}

}
