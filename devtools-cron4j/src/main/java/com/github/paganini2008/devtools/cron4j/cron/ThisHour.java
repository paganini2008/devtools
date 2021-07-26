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
import java.util.TreeMap;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.cron4j.CRON;

/**
 * 
 * ThisHour
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public class ThisHour implements TheHour, Serializable {

	private static final long serialVersionUID = 8124589572544886753L;
	private final TreeMap<Integer, Calendar> siblings = new TreeMap<Integer, Calendar>();
	private Day day;
	private int index;
	private Calendar hour;
	private int lastHour;
	private final StringBuilder cron;

	ThisHour(Day day, int hour) {
		CalendarAssert.checkHourOfDay(hour);
		this.day = day;
		Calendar calendar = CalendarUtils.setField(day.getTime(), Calendar.HOUR_OF_DAY, hour);
		this.siblings.put(hour, calendar);
		this.hour = calendar;
		this.lastHour = hour;
		this.cron = new StringBuilder().append(hour);
	}

	@Override
	public ThisHour andHour(int hour) {
		return andHour(hour, true);
	}

	private ThisHour andHour(int hour, boolean writeCron) {
		CalendarAssert.checkHourOfDay(hour);
		Calendar calendar = CalendarUtils.setField(day.getTime(), Calendar.HOUR_OF_DAY, hour);
		siblings.put(hour, calendar);
		this.lastHour = hour;
		if (writeCron) {
			cron.append(",").append(hour);
		}
		return this;
	}

	@Override
	public TheHour toHour(int hour, int interval) {
		CalendarAssert.checkHourOfDay(hour);
		if (interval < 0) {
			throw new IllegalArgumentException("Invalid interval: " + interval);
		}
		for (int i = lastHour + interval; i < hour; i += interval) {
			andHour(i, false);
		}
		if (interval > 1) {
			cron.append("-").append(hour).append("/").append(interval);
		} else {
			cron.append("-").append(hour);
		}
		return this;
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
	public boolean hasNext() {
		boolean next = index < siblings.size();
		if (!next) {
			if (day.hasNext()) {
				day = day.next();
				index = 0;
				next = true;
			}
		}
		return next;
	}

	@Override
	public Hour next() {
		hour = CollectionUtils.get(siblings.values().iterator(), index++);
		hour.set(Calendar.YEAR, day.getYear());
		hour.set(Calendar.MONTH, day.getMonth());
		hour.set(Calendar.DAY_OF_MONTH, day.getDay());
		return this;
	}

	@Override
	public CronExpression getParent() {
		return day;
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
