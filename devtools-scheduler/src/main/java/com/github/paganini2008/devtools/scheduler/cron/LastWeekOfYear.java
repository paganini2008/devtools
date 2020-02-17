package com.github.paganini2008.devtools.scheduler.cron;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * 
 * LastWeekOfYear
 *
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public class LastWeekOfYear implements Week, Serializable {

	private static final long serialVersionUID = -2099892494149322184L;
	private Year year;
	private Calendar week;
	private boolean self;

	LastWeekOfYear(Year year) {
		this.year = year;
		this.week = CalendarUtils.setField(year.getTime(), Calendar.WEEK_OF_YEAR, year.getWeekCount());
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

	public OneWeekDay weekday(int day) {
		return new SingleDayOfWeek(CollectionUtils.getFirst(this), day);
	}

	public Day everyDay(Function<Week, Integer> from, Function<Week, Integer> to, int interval) {
		return new EveryWeekDay(CollectionUtils.getFirst(this), from, to, interval);
	}

	public boolean hasNext() {
		boolean next = self;
		if (!next) {
			if (year.hasNext()) {
				year = year.next();
				week.set(Calendar.YEAR, year.getYear());
				week.set(Calendar.WEEK_OF_YEAR, year.getWeekCount());
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
}
