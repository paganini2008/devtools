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
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public class SingleWeekOfYear implements ConcreteWeek, Serializable {

	private static final long serialVersionUID = -3294283555586718358L;
	private final TreeMap<Integer, Calendar> siblings;
	private Year year;
	private int index;
	private Calendar calendar;
	private int lastWeek;

	SingleWeekOfYear(Year year, int week) {
		CalendarAssert.checkWeekOfYear(year, week);
		this.year = year;
		siblings = new TreeMap<Integer, Calendar>();
		Calendar calendar = CalendarUtils.setField(year.getTime(), Calendar.WEEK_OF_YEAR, week);
		siblings.put(week, calendar);
		this.lastWeek = week;
	}

	public ConcreteWeek andWeek(int week) {
		CalendarAssert.checkWeekOfYear(year, week);
		Calendar calendar = CalendarUtils.setField(year.getTime(), Calendar.WEEK_OF_YEAR, week);
		siblings.put(week, calendar);
		this.lastWeek = week;
		return this;
	}

	public ConcreteWeek toWeek(int week, int interval) {
		CalendarAssert.checkWeekOfYear(year, week);
		for (int i = lastWeek + interval; i < week; i += interval) {
			andWeek(i);
		}
		return null;
	}

	public Date getTime() {
		return calendar.getTime();
	}

	public long getTimeInMillis() {
		return calendar.getTimeInMillis();
	}

	public int getYear() {
		return calendar.get(Calendar.YEAR);
	}

	public int getMonth() {
		return calendar.get(Calendar.MONTH);
	}

	public int getWeek() {
		return calendar.get(Calendar.WEEK_OF_MONTH);
	}

	public int getWeekOfYear() {
		return calendar.get(Calendar.WEEK_OF_YEAR);
	}

	public ConcreteWeekDay weekday(int day) {
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
		calendar = CollectionUtils.get(siblings.values().iterator(), index++);
		calendar.set(Calendar.YEAR, year.getYear());
		return this;
	}
}
