package com.github.paganini2008.devtools.cron4j.cron;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.cron4j.CRON;

/**
 * 
 * LastDayOfMonth
 *
 * @author Jimmy Hoff
 * 
 * 
 * @version 1.0
 */
public class LastDayOfMonth implements Day, Serializable {

	private static final long serialVersionUID = 3379984313144390130L;

	private Month month;
	private Calendar day;
	private boolean self;

	LastDayOfMonth(Month month) {
		this.month = month;
		this.day = CalendarUtils.setField(month.getTime(), Calendar.DAY_OF_MONTH, month.getLastDay());
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

	public TheHour hour(int hour) {
		final Day copy = (Day) this.copy();
		return new ThisHour(CollectionUtils.getFirst(copy), hour);
	}

	public Hour everyHour(Function<Day, Integer> from, Function<Day, Integer> to, int interval) {
		final Day copy = (Day) this.copy();
		return new EveryHour(CollectionUtils.getFirst(copy), from, to, interval);
	}

	public boolean hasNext() {
		boolean next = self;
		if (!next) {
			if (month.hasNext()) {
				month = month.next();
				day.set(Calendar.YEAR, month.getYear());
				day.set(Calendar.MONTH, month.getMonth());
				day.set(Calendar.DAY_OF_MONTH, month.getLastDay());
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

	public String toString() {
		return CRON.toCronString(this);
	}

}
