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

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.cron4j.CRON;

/**
 * 
 * EveryHour
 *
 * @author Fred Feng
 * @version 1.0
 */
public class EveryHour implements Hour, Serializable {

	private static final long serialVersionUID = -5459905273757712271L;
	private Day day;
	private final Calendar hour;
	private final int fromHour;
	private final int toHour;
	private final int interval;
	private boolean self;
	private boolean forward = true;

	EveryHour(Day day, Function<Day, Integer> from, Function<Day, Integer> to, int interval) {
		if (interval <= 0) {
			throw new IllegalArgumentException("Invalid interval: " + interval);
		}
		this.day = day;
		this.fromHour = from.apply(day);
		CalendarAssert.checkHourOfDay(fromHour);
		this.hour = CalendarUtils.setField(day.getTime(), Calendar.HOUR_OF_DAY, fromHour);
		this.interval = interval;
		this.self = true;
		this.toHour = to.apply(day);
		CalendarAssert.checkHourOfDay(toHour);
	}

	@Override
	public boolean hasNext() {
		boolean next = self || hour.get(Calendar.HOUR_OF_DAY) + interval <= toHour;
		if (!next) {
			if (day.hasNext()) {
				day = day.next();
				hour.set(Calendar.YEAR, day.getYear());
				hour.set(Calendar.MONTH, day.getMonth());
				hour.set(Calendar.DAY_OF_MONTH, day.getDay());
				hour.set(Calendar.HOUR_OF_DAY, fromHour);
				forward = false;
				next = true;
			}
		}
		return next;
	}

	@Override
	public Hour next() {
		if (self) {
			self = false;
		} else {
			if (forward) {
				hour.add(Calendar.HOUR_OF_DAY, interval);
			} else {
				forward = true;
			}
		}
		return this;
	}

	@Override
	public int getYear() {
		return hour.get(Calendar.YEAR);
	}

	@Override
	public int getMonth() {
		return hour.get(Calendar.MONTH);
	}

	@Override
	public int getDay() {
		return hour.get(Calendar.DAY_OF_MONTH);
	}

	@Override
	public int getHour() {
		return hour.get(Calendar.HOUR_OF_DAY);
	}

	@Override
	public Date getTime() {
		return hour.getTime();
	}

	@Override
	public long getTimeInMillis() {
		return hour.getTimeInMillis();
	}

	@Override
	public TheMinute minute(int minute) {
		final Hour copy = (Hour) this.copy();
		return new ThisMinute(CollectionUtils.getFirst(copy), minute);
	}

	@Override
	public Minute everyMinute(Function<Hour, Integer> from, Function<Hour, Integer> to, int interval) {
		final Hour copy = (Hour) this.copy();
		return new EveryMinute(CollectionUtils.getFirst(copy), from, to, interval);
	}

	@Override
	public CronExpression getParent() {
		return day;
	}

	@Override
	public String toCronString() {
		String s = toHour != 23 ? fromHour + "-" + toHour : fromHour + "";
		return interval > 1 ? s + "/" + interval : "*";
	}

	@Override
	public String toString() {
		return CRON.toCronString(this);
	}

}
