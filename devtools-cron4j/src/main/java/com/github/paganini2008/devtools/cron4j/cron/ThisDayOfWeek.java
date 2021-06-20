/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.cron4j.CRON;

/**
 * 
 * ThisDayOfWeek
 *
 * @author Fred Feng
 * @version 1.0
 */
public class ThisDayOfWeek implements TheDayOfWeek, Serializable {

	private static final long serialVersionUID = -5353496894925284106L;
	private final TreeMap<Integer, Calendar> siblings = new TreeMap<Integer, Calendar>();
	private Week week;
	private int index;
	private Calendar day;
	private int lastDayOfWeek;
	private final StringBuilder cron;

	ThisDayOfWeek(Week week, int dayOfWeek) {
		CalendarAssert.checkDayOfWeek(dayOfWeek);
		this.week = week;
		Calendar calendar = CalendarUtils.setField(week.getTime(), Calendar.DAY_OF_WEEK, dayOfWeek);
		this.siblings.put(dayOfWeek, calendar);
		this.lastDayOfWeek = dayOfWeek;
		this.cron = new StringBuilder().append(getDayOfWeekName(dayOfWeek));
	}

	@Override
	public TheDayOfWeek andDay(int dayOfWeek) {
		return andDay(dayOfWeek, true);
	}

	private TheDayOfWeek andDay(int dayOfWeek, boolean writeCron) {
		CalendarAssert.checkDayOfWeek(dayOfWeek);
		Calendar calendar = CalendarUtils.setField(week.getTime(), Calendar.DAY_OF_WEEK, dayOfWeek);
		this.siblings.put(dayOfWeek, calendar);
		this.lastDayOfWeek = dayOfWeek;
		if (writeCron) {
			this.cron.append(",").append(getDayOfWeekName(dayOfWeek));
		}
		return this;
	}

	private String getDayOfWeekName(int dayOfWeek) {
		if (week instanceof LastWeekOfMonth) {
			return dayOfWeek + week.toCronString();
		} else if (week instanceof ThisWeek) {
			return week.toCronString().replaceAll("%s", String.valueOf(dayOfWeek));
		}
		return CalendarUtils.getDayOfWeekName(dayOfWeek);
	}

	@Override
	public TheDayOfWeek toDay(int dayOfWeek, int interval) {
		CalendarAssert.checkDayOfWeek(dayOfWeek);
		List<Integer> days = new ArrayList<Integer>();
		for (int i = lastDayOfWeek + interval; i <= dayOfWeek; i += interval) {
			andDay(i, false);
			days.add(i);
		}
		for (int day : days) {
			this.cron.append(",").append(getDayOfWeekName(day));
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
	public boolean hasNext() {
		boolean next = index < siblings.size();
		if (!next) {
			if (week.hasNext()) {
				week = week.next();
				index = 0;
				next = true;
			}
		}
		return next;
	}

	@Override
	public Day next() {
		day = CollectionUtils.get(siblings.values().iterator(), index++);
		day.set(Calendar.YEAR, week.getYear());
		day.set(Calendar.MONTH, week.getMonth());
		day.set(Calendar.WEEK_OF_MONTH, week.getWeek());
		return this;
	}

	@Override
	public CronExpression getParent() {
		return week;
	}

	@Override
	public String toCronString() {
		return this.cron.toString();
	}

	@Override
	public String toString() {
		return CRON.toCronString(this);
	}

}
