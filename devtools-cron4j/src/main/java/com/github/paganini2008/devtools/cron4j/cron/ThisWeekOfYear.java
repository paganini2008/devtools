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
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.collection.MapUtils;

/**
 * 
 * ThisWeekOfYear
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public class ThisWeekOfYear implements TheWeek, Serializable {

	private static final long serialVersionUID = -3294283555586718358L;
	private final TreeMap<Integer, Calendar> siblings = new TreeMap<Integer, Calendar>();
	private Year year;
	private int index;
	private Calendar week;
	private int lastWeek;

	ThisWeekOfYear(Year year, int week) {
		CalendarAssert.checkWeekOfYear(year, week);
		this.year = year;
		Calendar calendar = CalendarUtils.setField(year.getTime(), Calendar.WEEK_OF_YEAR, week);
		this.siblings.put(week, calendar);
		this.week = calendar;
		this.lastWeek = week;
	}

	@Override
	public TheWeek andWeek(int week) {
		CalendarAssert.checkWeekOfYear(year, week);
		Calendar calendar = CalendarUtils.setField(year.getTime(), Calendar.WEEK_OF_YEAR, week);
		this.siblings.put(week, calendar);
		this.lastWeek = week;
		return this;
	}

	@Override
	public TheWeek toWeek(int week, int interval) {
		CalendarAssert.checkWeekOfYear(year, week);
		for (int i = lastWeek + interval; i < week; i += interval) {
			andWeek(i);
		}
		return this;
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
		final Week copy = (Week) this;
		return new ThisDayOfWeek(CollectionUtils.getFirst(copy), day);
	}

	@Override
	public Day everyDay(Function<Week, Integer> from, Function<Week, Integer> to, int interval) {
		final Week copy = (Week) this;
		return new EveryDayOfWeek(CollectionUtils.getFirst(copy), from, to, interval);
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
	public Week next() {
		Map.Entry<Integer, Calendar> entry = MapUtils.getEntry(siblings, index++);
		week = entry.getValue();
		week.set(Calendar.YEAR, year.getYear());
		week.set(Calendar.WEEK_OF_YEAR, Math.min(entry.getKey(), year.getWeekCount()));
		return this;
	}

	@Override
	public CronExpression getParent() {
		return year;
	}
}
