package com.github.paganini2008.devtools.cron4j.cron;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * 
 * LastWeek
 *
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public class LastWeek implements Week, Serializable {

	private static final long serialVersionUID = 2658610900522209361L;
	private Month month;
	private Calendar week;
	private boolean self;

	LastWeek(Month month) {
		this.month = month;
		this.week = CalendarUtils.setField(month.getTime(), Calendar.WEEK_OF_MONTH, month.getWeekCount());
		this.self = true;
	}

	public Date getTime() {
		return week.getTime();
	}

	public long getTimeInMillis() {
		return week.getTimeInMillis();
	}

	public int getYear() {
		return week.get(Calendar.YEAR);
	}

	public int getMonth() {
		return week.get(Calendar.MONTH);
	}

	public int getWeek() {
		return week.get(Calendar.WEEK_OF_MONTH);
	}

	public int getWeekOfYear() {
		return week.get(Calendar.WEEK_OF_YEAR);
	}

	public OneDayOfWeek weekday(int day) {
		return new SingleDayOfWeek(CollectionUtils.getFirst(this), day);
	}

	public Day everyDay(Function<Week, Integer> from, Function<Week, Integer> to, int interval) {
		return new EveryDayOfWeek(CollectionUtils.getFirst(this), from, to, interval);
	}

	public boolean hasNext() {
		boolean next = self;
		if (!next) {
			if (month.hasNext()) {
				month = month.next();
				week.set(Calendar.YEAR, month.getYear());
				week.set(Calendar.MONTH, month.getMonth());
				week.set(Calendar.WEEK_OF_MONTH, month.getWeekCount());
				next = true;
			}
		}
		return next;
	}

	public Week next() {
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
