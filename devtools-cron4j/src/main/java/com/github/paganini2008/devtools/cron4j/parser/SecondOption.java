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
import com.github.paganini2008.devtools.cron4j.cron.Minute;
import com.github.paganini2008.devtools.cron4j.cron.TheSecond;

/**
 * 
 * SecondOption
 *
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public class SecondOption implements CronOption {

	private final String value;

	public SecondOption(String value) {
		this.value = value;
	}

	@Override
	public CronExpression join(CronExpression cronExpression) {
		final Minute minute = (Minute) cronExpression;
		try {
			return minute.second(Integer.parseInt(value));
		} catch (NumberFormatException ignored) {
		}

		if (value.equals("*")) {
			return minute.everySecond();
		}
		String[] args = value.split(",");
		TheSecond second = null;
		for (String arg : args) {
			if (second != null) {
				second = setSecond(arg, second);
			} else {
				second = setSecond(arg, minute);
			}
		}
		return second;
	}

	private TheSecond setSecond(String cron, TheSecond oneSecond) {
		if (cron.contains("-") && cron.contains("/")) {
			String[] args = value.split("[\\-\\/]", 3);
			return oneSecond.andSecond(Integer.parseInt(args[0])).toSecond(Integer.parseInt(args[1], Integer.parseInt(args[2])));
		} else if (cron.contains("-") && !cron.contains("/")) {
			String[] args = value.split("-", 2);
			return oneSecond.andSecond(Integer.parseInt(args[0])).toSecond(Integer.parseInt(args[1]));
		} else if (!cron.contains("-") && cron.contains("/")) {
			String[] args = value.split("\\/", 2);
			int start = getStartValue(args[0]);
			return oneSecond.andSecond(start).toSecond(59, Integer.parseInt(args[1]));
		} else {
			return oneSecond.andSecond(Integer.parseInt(cron));
		}
	}

	private TheSecond setSecond(String cron, Minute minute) {
		if (cron.contains("-") && cron.contains("/")) {
			String[] args = value.split("[\\-\\/]", 3);
			return minute.second(Integer.parseInt(args[0])).toSecond(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
		} else if (cron.contains("-") && !cron.contains("/")) {
			String[] args = value.split("-", 2);
			return minute.second(Integer.parseInt(args[0])).toSecond(Integer.parseInt(args[1]));
		} else if (!cron.contains("-") && cron.contains("/")) {
			String[] args = value.split("\\/", 2);
			int start = getStartValue(args[0]);
			return minute.second(start).toSecond(59, Integer.parseInt(args[1]));
		} else {
			return minute.second(Integer.parseInt(cron));
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
