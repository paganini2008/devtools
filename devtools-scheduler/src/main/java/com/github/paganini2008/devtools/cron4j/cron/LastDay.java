package com.github.paganini2008.devtools.cron4j.cron;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * 
 * LastDay
 *
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public class LastDay implements Day, Serializable {

	private static final long serialVersionUID = 3379984313144390130L;

	private Month month;
	private Calendar day;
	private boolean self;

	LastDay(Month month) {
		this.month = month;
		this.day = CalendarUtils.setField(month.getTime(), Calendar.DAY_OF_MONTH, month.getLasyDay());
		this.self = true;
	}

	public Date getTime() {
		return day.getTime();
	}

	public long getTimeInMillis() {
		return day.getTimeInMillis();
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

	public OneHour hour(int hour) {
		return new SingleHour(CollectionUtils.getFirst(this), hour);
	}

	public Hour everyHour(Function<Day, Integer> from, Function<Day, Integer> to, int interval) {
		return new EveryHour(CollectionUtils.getFirst(this), from, to, interval);
	}

	public boolean hasNext() {
		boolean next = self;
		if (!next) {
			if (month.hasNext()) {
				month = month.next();
				day.set(Calendar.YEAR, month.getYear());
				day.set(Calendar.MONTH, month.getMonth());
				day.set(Calendar.DAY_OF_MONTH, month.getLasyDay());
				next = true;
			}
		}
		return next;
	}

	public Day next() {
		if (self) {
			self = false;
		}
		return this;
	}
	
	public CronExpression getParent() {
		return month;
	}
	
	public String toCronString() {
		return "L";
	}

}
