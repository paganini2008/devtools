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
 * EveryYear
 *
 * @author Fred Feng
 * @version 1.0
 */
public class EveryYear implements Year, Serializable {

	private static final long serialVersionUID = 1487831872493410360L;

	public EveryYear(int fromYear, Function<Year, Integer> to, int interval) {
		CalendarAssert.checkYear(fromYear);
		if (interval <= 0) {
			throw new IllegalArgumentException("Invalid interval: " + interval);
		}
		this.year = CalendarUtils.setField(new Date(), Calendar.YEAR, fromYear);
		this.fromYear = fromYear;
		this.interval = interval;
		this.self = true;
		this.toYear = to.apply(this);
	}

	private final Calendar year;
	private final int fromYear;
	private final int toYear;
	private final int interval;
	private boolean self;

	@Override
	public int getYear() {
		return year.get(Calendar.YEAR);
	}

	@Override
	public int getWeekCount() {
		return year.getActualMaximum(Calendar.WEEK_OF_YEAR);
	}

	@Override
	public int getLastDay() {
		return year.getActualMaximum(Calendar.DAY_OF_YEAR);
	}

	@Override
	public Date getTime() {
		return year.getTime();
	}

	@Override
	public long getTimeInMillis() {
		return year.getTimeInMillis();
	}

	@Override
	public boolean hasNext() {
		return self || year.get(Calendar.YEAR) + interval <= toYear;
	}

	@Override
	public Year next() {
		if (self) {
			self = false;
		} else {
			year.add(Calendar.YEAR, interval);
		}
		return this;
	}

	@Override
	public Month everyMonth(Function<Year, Integer> from, Function<Year, Integer> to, int interval) {
		final Year copy = (Year) this.copy();
		return new EveryMonth(CollectionUtils.getFirst(copy), from, to, interval);
	}

	@Override
	public TheDay day(int day) {
		final Year copy = (Year) this.copy();
		return new ThisDayOfYear(CollectionUtils.getFirst(copy), day);
	}

	@Override
	public TheWeek week(int week) {
		final Year copy = (Year) this.copy();
		return new ThisWeekOfYear(CollectionUtils.getFirst(copy), week);
	}

	@Override
	public TheMonth month(int month) {
		final Year copy = (Year) this.copy();
		return new ThisMonth(CollectionUtils.getFirst(copy), month);
	}

	@Override
	public Week lastWeek() {
		final Year copy = (Year) this.copy();
		return new LastWeekOfYear(CollectionUtils.getFirst(copy));
	}

	@Override
	public CronExpression getParent() {
		return null;
	}

	@Override
	public String toCronString() {
		return interval > 1 ? fromYear + "-" + toYear + "/" + interval : "";
	}

	@Override
	public String toString() {
		return CRON.toCronString(this);
	}
}
