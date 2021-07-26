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

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.cron4j.CRON;

/**
 * 
 * EveryDayOfWeek
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public class EveryDayOfWeek implements Day, Serializable {

	private static final long serialVersionUID = 7871249122497937952L;
	private Week week;
	private final Calendar day;
	private final int fromDay;
	private final int toDay;
	private final int interval;
	private boolean self;
	private boolean forward = true;

	EveryDayOfWeek(Week week, Function<Week, Integer> from, Function<Week, Integer> to, int interval) {
		if (interval <= 0) {
			throw new IllegalArgumentException("Invalid interval: " + interval);
		}
		this.week = week;
		this.fromDay = from.apply(week);
		CalendarAssert.checkDayOfWeek(fromDay);
		Calendar calendar = CalendarUtils.setField(week.getTime(), Calendar.DAY_OF_WEEK, fromDay);
		this.day = calendar;
		this.interval = interval;
		this.self = true;
		this.toDay = to.apply(week);
		CalendarAssert.checkDayOfWeek(toDay);
	}

	@Override
	public boolean hasNext() {
		boolean next = self || day.get(Calendar.DAY_OF_WEEK) + interval <= toDay;
		if (!next) {
			if (week.hasNext()) {
				week = week.next();
				day.set(Calendar.YEAR, week.getYear());
				day.set(Calendar.MONTH, week.getMonth());
				day.set(Calendar.WEEK_OF_MONTH, week.getWeek());
				day.set(Calendar.DAY_OF_WEEK, fromDay);
				forward = false;
				next = true;
			}
		}
		return next;
	}

	@Override
	public Day next() {
		if (self) {
			self = false;
		} else {
			if (forward) {
				day.add(Calendar.DAY_OF_WEEK, interval);
			} else {
				forward = true;
			}
		}
		return this;
	}

	@Override
	public int getYear() {
		return day.get(Calendar.YEAR);
	}

	@Override
	public int getMonth() {
		return day.get(Calendar.MONTH);
	}

	@Override
	public int getDay() {
		return day.get(Calendar.DAY_OF_MONTH);
	}

	@Override
	public int getDayOfWeek() {
		return day.get(Calendar.DAY_OF_WEEK);
	}

	@Override
	public int getDayOfYear() {
		return day.get(Calendar.DAY_OF_YEAR);
	}

	@Override
	public Date getTime() {
		return day.getTime();
	}

	@Override
	public long getTimeInMillis() {
		return day.getTimeInMillis();
	}

	@Override
	public TheHour hour(int hour) {
		final Day copy = (Day) this.copy();
		return new ThisHour(CollectionUtils.getFirst(copy), hour);
	}

	@Override
	public Hour everyHour(Function<Day, Integer> from, Function<Day, Integer> to, int interval) {
		final Day copy = (Day) this.copy();
		return new EveryHour(CollectionUtils.getFirst(copy), from, to, interval);
	}

	@Override
	public CronExpression getParent() {
		return week;
	}

	@Override
	public String toCronString() {
		String s = toDay != Calendar.SATURDAY ? fromDay + "-" + toDay : fromDay + "/";
		return interval > 1 ? s + "/" + interval : "?";
	}

	@Override
	public String toString() {
		return CRON.toCronString(this);
	}

}
