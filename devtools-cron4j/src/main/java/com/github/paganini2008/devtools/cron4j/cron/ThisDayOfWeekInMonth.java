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
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.cron4j.CRON;

/**
 * 
 * ThisDayOfWeekInMonth
 *
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public class ThisDayOfWeekInMonth implements TheDayOfWeekInMonth, Serializable {

	private static final long serialVersionUID = -5853750543470928852L;

	private final TreeMap<String, Calendar> siblings = new TreeMap<String, Calendar>(new InnerComparator());
	private final StringBuilder cron = new StringBuilder();
	private Month month;
	private Calendar day;
	private int index;

	private static class InnerComparator implements Comparator<String>, Serializable {

		private static final long serialVersionUID = 1L;

		@Override
		public int compare(String a, String b) {
			String x = a.split("#", 2)[1];
			String y = b.split("#", 2)[1];
			return Integer.parseInt(x) - Integer.parseInt(y);
		}

	}

	ThisDayOfWeekInMonth(Month month, int week, int dayOfWeek) {
		CalendarAssert.checkWeekOfMonth(month, week);
		CalendarAssert.checkDayOfWeek(dayOfWeek);
		this.month = month;
		Calendar calendar = CalendarUtils.setField(month.getTime(), Calendar.WEEK_OF_MONTH, week);
		calendar = CalendarUtils.setField(calendar, Calendar.DAY_OF_WEEK, dayOfWeek);
		this.siblings.put(dayOfWeek + "#" + week, calendar);
		this.cron.append(cronFor(week, dayOfWeek));
	}

	private String cronFor(int week, int dayOfWeek) {
		return month.getWeekCount() == week ? dayOfWeek + "L" : dayOfWeek + "#" + week;
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
	public Day next() {
		Map.Entry<String, Calendar> entry = MapUtils.getEntry(siblings, index++);
		String cron = entry.getKey();
		String[] args = cron.split("#", 2);
		day = entry.getValue();
		day.set(Calendar.YEAR, month.getYear());
		day.set(Calendar.MONTH, month.getMonth());
		day.set(Calendar.WEEK_OF_MONTH, Math.min(Integer.parseInt(args[1]), month.getWeekCount()));
		day.set(Calendar.DAY_OF_WEEK, Integer.parseInt(args[0]));
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
	public TheDayOfWeekInMonth and(int week, int dayOfWeek) {
		CalendarAssert.checkWeekOfMonth(month, week);
		CalendarAssert.checkDayOfWeek(dayOfWeek);
		Calendar calendar = CalendarUtils.setField(month.getTime(), Calendar.WEEK_OF_MONTH, week);
		calendar = CalendarUtils.setField(calendar, Calendar.DAY_OF_WEEK, dayOfWeek);
		this.siblings.put(dayOfWeek + "#" + week, calendar);
		this.cron.append(",").append(cronFor(week, dayOfWeek));
		return this;
	}

	@Override
	public TheDayOfWeekInMonth andLast(int datOfWeek) {
		return and(month.getWeekCount(), datOfWeek);
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
