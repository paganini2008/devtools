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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.cron4j.CRON;

/**
 * 
 * ThisWeek
 *
 * @author Fred Feng
 * @version 1.0
 */
public class ThisWeek implements TheWeek, Serializable {

	private static final long serialVersionUID = -4563991137870265612L;
	private final TreeMap<Integer, Calendar> siblings = new TreeMap<Integer, Calendar>();
	private Month month;
	private int index;
	private Calendar week;
	private int lastWeek;
	private final StringBuilder cron;

	ThisWeek(Month month, int week) {
		CalendarAssert.checkWeekOfMonth(month, week);
		this.month = month;
		Calendar calendar = CalendarUtils.setField(month.getTime(), Calendar.WEEK_OF_MONTH, week);
		this.siblings.put(week, calendar);
		this.week = calendar;
		this.lastWeek = week;
		this.cron = new StringBuilder().append("%s#").append(week);
	}

	@Override
	public ThisWeek andWeek(int week) {
		return andWeek(week, true);
	}

	private ThisWeek andWeek(int week, boolean writeCron) {
		CalendarAssert.checkWeekOfMonth(month, week);
		Calendar calendar = CalendarUtils.setField(month.getTime(), Calendar.WEEK_OF_MONTH, week);
		this.siblings.put(week, calendar);
		this.lastWeek = week;
		if (writeCron) {
			this.cron.append(",%s#").append(week);
		}
		return this;
	}

	@Override
	public ThisWeek toWeek(int week, int interval) {
		CalendarAssert.checkWeekOfMonth(month, week);
		List<Integer> weeks = new ArrayList<Integer>();
		for (int i = lastWeek + interval; i < week; i += interval) {
			andWeek(i, false);
			weeks.add(i);
		}
		for (int w : weeks) {
			this.cron.append(",%s#").append(w);
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
		boolean next = index < siblings.size();
		if (!next) {
			if (month.hasNext()) {
				month = month.next();
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
		week.set(Calendar.YEAR, month.getYear());
		week.set(Calendar.MONTH, month.getMonth());
		week.set(Calendar.WEEK_OF_MONTH, Math.min(entry.getKey(), month.getWeekCount()));
		return this;
	}

	@Override
	public CronExpression getParent() {
		return month;
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
