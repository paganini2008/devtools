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
import java.util.function.Function;

import com.github.paganini2008.devtools.cron4j.CRON;

/**
 * 
 * EverySecond
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public class EverySecond implements Second, Serializable {

	private static final long serialVersionUID = -2606684197757806223L;
	private Minute minute;
	private final Calendar second;
	private final int fromSecond;
	private final int toSecond;
	private final int interval;
	private boolean self;
	private boolean forward = true;

	EverySecond(Minute minute, Function<Minute, Integer> from, Function<Minute, Integer> to, int interval) {
		if (interval <= 0) {
			throw new IllegalArgumentException("Invalid interval: " + interval);
		}
		this.minute = minute;
		this.fromSecond = from.apply(minute);
		CalendarAssert.checkSecond(fromSecond);
		this.second = CalendarUtils.setField(minute.getTime(), Calendar.SECOND, fromSecond);
		this.interval = interval;
		this.self = true;
		this.toSecond = to.apply(minute);
		CalendarAssert.checkSecond(toSecond);
	}

	@Override
	public boolean hasNext() {
		boolean next = self || second.get(Calendar.SECOND) + interval <= toSecond;
		if (!next) {
			if (minute.hasNext()) {
				minute = minute.next();
				second.set(Calendar.YEAR, minute.getYear());
				second.set(Calendar.MONTH, minute.getMonth());
				second.set(Calendar.DAY_OF_MONTH, minute.getDay());
				second.set(Calendar.HOUR_OF_DAY, minute.getHour());
				second.set(Calendar.MINUTE, minute.getMinute());
				second.set(Calendar.SECOND, fromSecond);
				forward = false;
				next = true;
			}
		}
		return next;
	}

	@Override
	public Second next() {
		if (self) {
			self = false;
		} else {
			if (forward) {
				second.add(Calendar.SECOND, interval);
			} else {
				forward = true;
			}
		}
		return this;
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
	public Date getTime() {
		return second.getTime();
	}

	@Override
	public long getTimeInMillis() {
		return second.getTimeInMillis();
	}

	@Override
	public CronExpression getParent() {
		return minute;
	}

	@Override
	public String toCronString() {
		String s = toSecond != 60 ? fromSecond + "-" + toSecond : fromSecond + "";
		return interval > 1 ? s + "/" + interval : "*";
	}

	@Override
	public String toString() {
		return CRON.toCronString(this);
	}

	public static void main(String[] args) {
		Year everyYear = CronExpressionBuilder.everyYear(2);
		Month everyMonth = everyYear.everyMonth(5, 10, 2);
		Day everyDay = everyMonth.everyDay(1, 15, 3);
		Hour everyHour = everyDay.everyHour(4);
		Second second = everyHour.at(12, 0);
		System.out.println(second);
	}

}
