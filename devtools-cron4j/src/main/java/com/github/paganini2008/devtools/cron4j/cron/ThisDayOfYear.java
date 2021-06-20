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
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.collection.MapUtils;

/**
 * 
 * ThisDayOfYear
 *
 * @author Fred Feng
 * @version 1.0
 */
public class ThisDayOfYear implements TheDay, Serializable {

	private static final long serialVersionUID = -8235489088108418524L;
	private final TreeMap<Integer, Calendar> siblings = new TreeMap<Integer, Calendar>();
	private Year year;
	private int index;
	private Calendar day;
	private int lastDay;

	ThisDayOfYear(Year year, int day) {
		CalendarAssert.checkDayOfYear(year, day);
		this.year = year;
		Calendar calendar = CalendarUtils.setField(year.getTime(), Calendar.DAY_OF_YEAR, day);
		this.siblings.put(day, calendar);
		this.lastDay = day;
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
	public Date getTime() {
		return day.getTime();
	}

	@Override
	public long getTimeInMillis() {
		return day.getTimeInMillis();
	}

	@Override
	public TheDay andDay(int dayOfYear) {
		CalendarAssert.checkDayOfYear(year, dayOfYear);
		Calendar calendar = CalendarUtils.setField(year.getTime(), Calendar.DAY_OF_YEAR, dayOfYear);
		this.siblings.put(dayOfYear, calendar);
		this.lastDay = dayOfYear;
		return this;
	}

	@Override
	public TheDay toDay(int day, int interval) {
		CalendarAssert.checkDayOfYear(year, day);
		for (int dayOfYear = lastDay + interval; dayOfYear < day; dayOfYear += interval) {
			andDay(dayOfYear);
		}
		return this;
	}

	@Override
	public boolean hasNext() {
		boolean next = index < siblings.size();
		if (!next) {
			if (year.hasNext()) {
				year = year.next();
				index = 0;
				next = true;
			}
		}
		return next;
	}

	@Override
	public Day next() {
		Map.Entry<Integer, Calendar> entry = MapUtils.getEntry(siblings, index++);
		day = entry.getValue();
		day.set(Calendar.YEAR, year.getYear());
		day.set(Calendar.DAY_OF_YEAR, Math.min(entry.getKey(), year.getLastDay()));
		return this;
	}

	@Override
	public CronExpression getParent() {
		return null;
	}

}
