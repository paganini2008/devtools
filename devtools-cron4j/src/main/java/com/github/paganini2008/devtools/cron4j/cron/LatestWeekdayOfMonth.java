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

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.cron4j.CRON;

/**
 * 
 * LatestWeekdayOfMonth
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public class LatestWeekdayOfMonth implements Day, Serializable {

	private static final long serialVersionUID = -1745938729702028629L;

	private Month month;
	private Calendar day;
	private boolean self;
	private final int dayOfMonth;

	LatestWeekdayOfMonth(Month month, int dayOfMonth) {
		this.month = month;
		this.day = CalendarUtils.setField(month.getTime(), Calendar.DAY_OF_MONTH,
				month.getLatestWeekday(Math.min(dayOfMonth, month.getLastDay())));
		this.dayOfMonth = dayOfMonth;
		this.self = true;
	}

	@Override
	public boolean hasNext() {
		boolean next = self;
		if (!next) {
			if (month.hasNext()) {
				month = month.next();
				day.set(Calendar.YEAR, month.getYear());
				day.set(Calendar.MONTH, month.getMonth());
				day.set(Calendar.DAY_OF_MONTH, month.getLatestWeekday(Math.min(dayOfMonth, month.getLastDay())));
				next = true;
			}
		}
		return next;
	}

	@Override
	public Day next() {
		if (self) {
			self = false;
		}
		return this;
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
	public CronExpression getParent() {
		return month;
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
	public TheHour hour(int hourOfDay) {
		final Day copy = (Day) this.copy();
		return new ThisHour(CollectionUtils.getFirst(copy), hourOfDay);
	}

	@Override
	public Hour everyHour(Function<Day, Integer> from, Function<Day, Integer> to, int interval) {
		final Day copy = (Day) this.copy();
		return new EveryHour(CollectionUtils.getFirst(copy), from, to, interval);
	}

	@Override
	public String toCronString() {
		return dayOfMonth + "W";
	}

	@Override
	public String toString() {
		return CRON.toCronString(this);
	}

}
