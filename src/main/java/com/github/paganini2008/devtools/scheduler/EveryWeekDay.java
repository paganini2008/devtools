package com.github.paganini2008.devtools.scheduler;

import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * 
 * EveryWeekDay
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public class EveryWeekDay implements Day {

	private Week week;
	private final Calendar day;
	private final int fromDay;
	private final int toDay;
	private final int interval;
	private boolean state;
	private boolean flag = true;

	EveryWeekDay(Week week, Function<Week, Integer> from, Function<Week, Integer> to, int interval) {
		this.week = week;
		this.fromDay = from.apply(week);
		this.day = CalendarUtils.setField(week.getTime(), Calendar.DAY_OF_WEEK, fromDay);
		this.toDay = to.apply(week);
		this.interval = interval;
		this.state = true;
	}

	public boolean hasNext() {
		boolean next = state || day.get(Calendar.DAY_OF_MONTH) + interval <= toDay;
		if (!next) {
			if (week.hasNext()) {
				week = week.next();
				day.set(Calendar.YEAR, week.getYear());
				day.set(Calendar.MONTH, week.getMonth());
				day.set(Calendar.WEEK_OF_MONTH, week.getWeek());
				day.set(Calendar.DAY_OF_WEEK, fromDay);
				flag = false;
				next = true;
			}
		}
		return next;
	}

	public Day next() {
		if (state) {
			state = false;
		} else {
			if (flag) {
				day.add(Calendar.DAY_OF_WEEK, interval);
			} else {
				flag = true;
			}
		}
		return this;
	}

	public int getYear() {
		return day.get(Calendar.YEAR);
	}

	public int getMonth() {
		return day.get(Calendar.MONTH);
	}

	public int getDay() {
		return day.get(Calendar.DAY_OF_MONTH);
	}

	public int getWeekDay() {
		return day.get(Calendar.DAY_OF_WEEK);
	}

	public int getDayOfYear() {
		return day.get(Calendar.DAY_OF_YEAR);
	}

	public Date getTime() {
		return day.getTime();
	}

	public long getTimeInMillis() {
		return day.getTimeInMillis();
	}

	public ConcreteHour hour(int hour) {
		return new SingleHour(CollectionUtils.getFirst(this), hour);
	}

	public Hour everyHour(Function<Day, Integer> from, Function<Day, Integer> to, int interval) {
		return new EveryHour(CollectionUtils.getFirst(this), from, to, interval);
	}
}
