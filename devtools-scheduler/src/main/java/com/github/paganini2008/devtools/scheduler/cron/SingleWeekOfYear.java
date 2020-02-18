package com.github.paganini2008.devtools.scheduler.cron;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * 
 * SingleWeekOfYear
 *
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public class SingleWeekOfYear implements OneWeek, Serializable {

	private static final long serialVersionUID = -3294283555586718358L;
	private final TreeMap<Integer, Calendar> siblings;
	private Year year;
	private int index;
	private Calendar week;
	private int lastWeek;

	SingleWeekOfYear(Year year, int week) {
		CalendarAssert.checkWeekOfYear(year, week);
		this.year = year;
		siblings = new TreeMap<Integer, Calendar>();
		Calendar calendar = CalendarUtils.setField(year.getTime(), Calendar.WEEK_OF_YEAR, week);
		siblings.put(week, calendar);
		this.week = calendar;
		this.lastWeek = week;
	}

	public OneWeek andWeek(int week) {
		CalendarAssert.checkWeekOfYear(year, week);
		Calendar calendar = CalendarUtils.setField(year.getTime(), Calendar.WEEK_OF_YEAR, week);
		siblings.put(week, calendar);
		this.lastWeek = week;
		return this;
	}

	public OneWeek toWeek(int week, int interval) {
		CalendarAssert.checkWeekOfYear(year, week);
		for (int i = lastWeek + interval; i < week; i += interval) {
			andWeek(i);
		}
		return this;
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
		boolean next = index < siblings.size();
		if (!next) {
			if (year.hasNext()) {
				year = year.next();
				index = 0;
				next = true;
			}
		}
		return next;
	}

	public Week next() {
		week = CollectionUtils.get(siblings.values().iterator(), index++);
		week.set(Calendar.YEAR, year.getYear());
		return this;
	}
}