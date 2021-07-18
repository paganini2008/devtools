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
 * EveryMinute
 *
 * @author Fred Feng
 * @version 1.0
 */
public class EveryMinute implements Minute, Serializable {

	private static final long serialVersionUID = -7939881133025374416L;
	private Hour hour;
	private final Calendar minute;
	private final int fromMinute;
	private final int toMinute;
	private final int interval;
	private boolean self;
	private boolean forward = true;

	EveryMinute(Hour hour, Function<Hour, Integer> from, Function<Hour, Integer> to, int interval) {
		if (interval <= 0) {
			throw new IllegalArgumentException("Invalid interval: " + interval);
		}
		this.hour = hour;
		this.fromMinute = from.apply(hour);
		this.minute = CalendarUtils.setField(hour.getTime(), Calendar.MINUTE, fromMinute);
		CalendarAssert.checkMinute(fromMinute);
		this.interval = interval;
		this.self = true;
		this.toMinute = to.apply(hour);
		CalendarAssert.checkMinute(toMinute);
	}

	@Override
	public boolean hasNext() {
		boolean next = self || minute.get(Calendar.MINUTE) + interval <= toMinute;
		if (!next) {
			if (hour.hasNext()) {
				hour = hour.next();
				minute.set(Calendar.YEAR, hour.getYear());
				minute.set(Calendar.MONTH, hour.getMonth());
				minute.set(Calendar.DAY_OF_MONTH, hour.getDay());
				minute.set(Calendar.HOUR_OF_DAY, hour.getHour());
				minute.set(Calendar.MINUTE, fromMinute);
				forward = false;
				next = true;
			}
		}
		return next;
	}

	@Override
	public Minute next() {
		if (self) {
			self = false;
		} else {
			if (forward) {
				minute.add(Calendar.MINUTE, interval);
			} else {
				forward = true;
			}
		}
		return this;
	}

	@Override
	public int getYear() {
		return minute.get(Calendar.YEAR);
	}

	@Override
	public int getMonth() {
		return minute.get(Calendar.MONTH);
	}

	@Override
	public int getDay() {
		return minute.get(Calendar.DAY_OF_MONTH);
	}

	@Override
	public int getHour() {
		return minute.get(Calendar.HOUR_OF_DAY);
	}

	@Override
	public int getMinute() {
		return minute.get(Calendar.MINUTE);
	}

	@Override
	public Date getTime() {
		return minute.getTime();
	}

	@Override
	public long getTimeInMillis() {
		return minute.getTimeInMillis();
	}

	@Override
	public TheSecond second(int second) {
		final Minute copy = (Minute) this.copy();
		return new ThisSecond(CollectionUtils.getFirst(copy), second);
	}

	@Override
	public Second everySecond(Function<Minute, Integer> from, Function<Minute, Integer> to, int interval) {
		final Minute copy = (Minute) this.copy();
		return new EverySecond(CollectionUtils.getFirst(copy), from, to, interval);
	}

	@Override
	public CronExpression getParent() {
		return hour;
	}

	@Override
	public String toCronString() {
		String s = toMinute != 59 ? fromMinute + "-" + toMinute : fromMinute + "";
		return interval > 1 ? s + "/" + interval : "*";
	}

	@Override
	public String toString() {
		return CRON.toCronString(this);
	}

}
