package com.github.paganini2008.devtools.cron4j.cron;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * 
 * EveryDayOfWeek
 *
 * @author Fred Feng
 * @version 1.0
 */
public class EveryDayOfWeek implements Day, Serializable {

	private static final long serialVersionUID = 7871249122497937952L;
	private Week week;
	private final Calendar day;
	private final int fromDay;
	private final int toDay;
	private final int interval;
	private boolean self;
	private boolean forward = true;

	EveryDayOfWeek(Week week, Function<Week, Integer> from, Function<Week, Integer> to, int interval) {
		if (interval <= 0) {
			throw new IllegalArgumentException("Invalid interval: " + interval);
		}
		this.week = week;
		this.fromDay = from.apply(week);
		CalendarAssert.checkDayOfWeek(fromDay);
		Calendar calendar = CalendarUtils.setField(week.getTime(), Calendar.DAY_OF_WEEK, fromDay);
		this.day = calendar;
		this.interval = interval;
		this.self = true;
		this.toDay = to.apply(week);
		CalendarAssert.checkDayOfWeek(toDay);
	}

	public boolean hasNext() {
		boolean next = self || day.get(Calendar.DAY_OF_WEEK) + interval <= toDay;
		if (!next) {
			if (week.hasNext()) {
				week = week.next();
				day.set(Calendar.YEAR, week.getYear());
				day.set(Calendar.MONTH, week.getMonth());
				day.set(Calendar.WEEK_OF_MONTH, week.getWeek());
				day.set(Calendar.DAY_OF_WEEK, fromDay);
				forward = false;
				next = true;
			}
		}
		return next;
	}

	public Day next() {
		if (self) {
			self = false;
		} else {
			if (forward) {
				day.add(Calendar.DAY_OF_WEEK, interval);
			} else {
				forward = true;
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

	public int getDayOfWeek() {
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

	public TheHour hour(int hour) {
		final Day copy = (Day) this.copy();
		return new ThisHour(CollectionUtils.getFirst(copy), hour);
	}

	public Hour everyHour(Function<Day, Integer> from, Function<Day, Integer> to, int interval) {
		final Day copy = (Day) this.copy();
		return new EveryHour(CollectionUtils.getFirst(copy), from, to, interval);
	}

	public CronExpression getParent() {
		return week;
	}

	public String toCronString() {
		return "?";
	}

}