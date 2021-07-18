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
 * EveryMonth
 *
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public class EveryMonth implements Month, Serializable {

	private static final long serialVersionUID = -7085376125910878673L;
	private Year year;
	private final Calendar month;
	private final int fromMonth;
	private final int toMonth;
	private final int interval;
	private boolean self;
	private boolean forward = true;

	EveryMonth(Year year, Function<Year, Integer> from, Function<Year, Integer> to, int interval) {
		if (interval <= 0) {
			throw new IllegalArgumentException("Invalid interval: " + interval);
		}
		this.year = year;
		this.fromMonth = from.apply(year);
		CalendarAssert.checkMonth(fromMonth);
		this.month = CalendarUtils.setField(year.getTime(), Calendar.MONTH, fromMonth);
		this.interval = interval;
		this.self = true;
		this.toMonth = to.apply(year);
		CalendarAssert.checkMonth(toMonth);
	}

	@Override
	public boolean hasNext() {
		boolean next = self || month.get(Calendar.MONTH) + interval <= toMonth;
		if (!next) {
			if (year.hasNext()) {
				year = year.next();
				month.set(Calendar.YEAR, year.getYear());
				month.set(Calendar.MONTH, fromMonth);
				forward = false;
				next = true;
			}
		}
		return next;
	}

	@Override
	public Month next() {
		if (self) {
			self = false;
		} else {
			if (forward) {
				month.add(Calendar.MONTH, interval);
			} else {
				forward = true;
			}
		}
		return this;
	}

	@Override
	public int getYear() {
		return month.get(Calendar.YEAR);
	}

	@Override
	public int getMonth() {
		return month.get(Calendar.MONTH);
	}

	@Override
	public int getLastDay() {
		return month.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	@Override
	public Day latestWeekday(int dayOfMonth) {
		return new LatestWeekdayOfMonth(this, dayOfMonth);
	}

	@Override
	public int getLatestWeekday(int dayOfMonth) {
		CalendarAssert.checkDayOfMonth(this, dayOfMonth);
		return CalendarUtils.getLatestWeekday(month, dayOfMonth);
	}

	@Override
	public int getWeekCount() {
		return month.getActualMaximum(Calendar.WEEK_OF_MONTH);
	}

	@Override
	public Date getTime() {
		return month.getTime();
	}

	@Override
	public long getTimeInMillis() {
		return month.getTimeInMillis();
	}

	@Override
	public TheDay day(int day) {
		final Month copy = (Month) this.copy();
		return new ThisDay(CollectionUtils.getFirst(copy), day);
	}

	@Override
	public Day lastDay() {
		final Month copy = (Month) this.copy();
		return new LastDayOfMonth(CollectionUtils.getFirst(copy));
	}

	@Override
	public Day everyDay(Function<Month, Integer> from, Function<Month, Integer> to, int interval) {
		final Month copy = (Month) this.copy();
		return new EveryDay(CollectionUtils.getFirst(copy), from, to, interval);
	}

	@Override
	public TheWeek week(int week) {
		final Month copy = (Month) this.copy();
		return new ThisWeek(CollectionUtils.getFirst(copy), week);
	}

	@Override
	public TheDayOfWeekInMonth dayOfWeek(int week, int dayOfWeek) {
		final Month copy = (Month) this.copy();
		return new ThisDayOfWeekInMonth(CollectionUtils.getFirst(copy), week, dayOfWeek);
	}

	@Override
	public Week lastWeek() {
		final Month copy = (Month) this.copy();
		return new LastWeekOfMonth(CollectionUtils.getFirst(copy));
	}

	@Override
	public Week everyWeek(Function<Month, Integer> from, Function<Month, Integer> to, int interval) {
		final Month copy = (Month) this.copy();
		return new EveryWeek(CollectionUtils.getFirst(copy), from, to, interval);
	}

	@Override
	public CronExpression getParent() {
		return year;
	}

	@Override
	public String toCronString() {
		String s = toMonth != 12 ? fromMonth + "-" + toMonth : fromMonth + "";
		return interval > 1 ? s + "/" + interval : "*";
	}

	@Override
	public String toString() {
		return CRON.toCronString(this);
	}

}
