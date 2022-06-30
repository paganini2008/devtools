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
 * LastWeekOfMonth
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public class LastWeekOfMonth implements Week, Serializable {

	private static final long serialVersionUID = 2658610900522209361L;
	private Month month;
	private Calendar week;
	private boolean self;

	LastWeekOfMonth(Month month) {
		this.month = month;
		this.week = CalendarUtils.setField(month.getTime(), Calendar.WEEK_OF_MONTH, month.getWeekCount());
		this.self = true;
	}

	@Override
	public Date getTime() {
		return week.getTime();
	}

	@Override
	public long getTimeInMillis() {
		return week.getTimeInMillis();
	}

	@Override
	public int getYear() {
		return week.get(Calendar.YEAR);
	}

	@Override
	public int getMonth() {
		return week.get(Calendar.MONTH);
	}

	@Override
	public int getWeek() {
		return week.get(Calendar.WEEK_OF_MONTH);
	}

	@Override
	public int getWeekOfYear() {
		return week.get(Calendar.WEEK_OF_YEAR);
	}

	@Override
	public TheDayOfWeek day(int day) {
		final Week copy = (Week) this.copy();
		return new ThisDayOfWeek(CollectionUtils.getFirst(copy), day);
	}

	@Override
	public Day everyDay(Function<Week, Integer> from, Function<Week, Integer> to, int interval) {
		final Week copy = (Week) this.copy();
		return new EveryDayOfWeek(CollectionUtils.getFirst(copy), from, to, interval);
	}

	@Override
	public boolean hasNext() {
		boolean next = self;
		if (!next) {
			if (month.hasNext()) {
				month = month.next();
				week.set(Calendar.YEAR, month.getYear());
				week.set(Calendar.MONTH, month.getMonth());
				week.set(Calendar.WEEK_OF_MONTH, month.getWeekCount());
				next = true;
			}
		}
		return next;
	}

	@Override
	public Week next() {
		if (self) {
			self = false;
		}
		return this;
	}

	@Override
	public CronExpression getParent() {
		return month;
	}

	@Override
	public String toCronString() {
		return "L";
	}

	@Override
	public String toString() {
		return CRON.toCronString(this);
	}

}
