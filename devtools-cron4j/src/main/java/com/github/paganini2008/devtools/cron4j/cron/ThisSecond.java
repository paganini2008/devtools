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
import java.util.TreeMap;

import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.cron4j.CRON;

/**
 * 
 * ThisSecond
 *
 * @author Fred Feng
 * @version 1.0
 */
public class ThisSecond implements TheSecond, Serializable {

	private static final long serialVersionUID = 6264419114715870528L;
	private final TreeMap<Integer, Calendar> siblings = new TreeMap<Integer, Calendar>();
	private Minute minute;
	private int index;
	private Calendar second;
	private int lastSecond;
	private final StringBuilder cron;

	ThisSecond(Minute minute, int second) {
		CalendarAssert.checkSecond(second);
		this.minute = minute;
		Calendar calendar = CalendarUtils.setField(minute.getTime(), Calendar.SECOND, second);
		this.siblings.put(second, calendar);
		this.second = calendar;
		this.lastSecond = second;
		this.cron = new StringBuilder().append(second);
	}

	@Override
	public ThisSecond andSecond(int second) {
		return andSecond(second, true);
	}

	private ThisSecond andSecond(int second, boolean writeCron) {
		CalendarAssert.checkSecond(second);
		Calendar calendar = CalendarUtils.setField(minute.getTime(), Calendar.SECOND, second);
		this.siblings.put(second, calendar);
		this.lastSecond = second;
		if (writeCron) {
			this.cron.append(",").append(second);
		}
		return this;
	}

	@Override
	public TheSecond toSecond(int second, int interval) {
		CalendarAssert.checkSecond(second);
		if (interval < 0) {
			throw new IllegalArgumentException("Invalid interval: " + interval);
		}
		for (int i = lastSecond + interval; i < second; i += interval) {
			andSecond(i, false);
		}
		if (interval > 1) {
			this.cron.append("-").append(second).append("/").append(interval);
		} else {
			this.cron.append("-").append(second);
		}
		return this;
	}

	@Override
	public Date getTime() {
		return second.getTime();
	}

	@Override
	public long getTimeInMillis() {
		return second.getTimeInMillis();
	}

	@Override
	public int getYear() {
		return second.get(Calendar.YEAR);
	}

	@Override
	public int getMonth() {
		return second.get(Calendar.MONTH);
	}

	@Override
	public int getDay() {
		return second.get(Calendar.DAY_OF_MONTH);
	}

	@Override
	public int getHour() {
		return second.get(Calendar.HOUR_OF_DAY);
	}

	@Override
	public int getMinute() {
		return second.get(Calendar.MINUTE);
	}

	@Override
	public int getSecond() {
		return second.get(Calendar.SECOND);
	}

	@Override
	public boolean hasNext() {
		boolean next = index < siblings.size();
		if (!next) {
			if (minute.hasNext()) {
				minute = minute.next();
				index = 0;
				next = true;
			}
		}
		return next;
	}

	@Override
	public Second next() {
		second = CollectionUtils.get(siblings.values().iterator(), index++);
		second.set(Calendar.YEAR, minute.getYear());
		second.set(Calendar.MONTH, minute.getMonth());
		second.set(Calendar.DAY_OF_MONTH, minute.getDay());
		second.set(Calendar.HOUR_OF_DAY, minute.getHour());
		second.set(Calendar.MINUTE, minute.getMinute());
		return this;
	}

	@Override
	public CronExpression getParent() {
		return minute;
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
