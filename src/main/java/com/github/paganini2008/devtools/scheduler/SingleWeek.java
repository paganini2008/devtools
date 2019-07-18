package com.github.paganini2008.devtools.scheduler;

import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * 
 * SingleWeek
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public class SingleWeek implements ConcreteWeek {

	private final TreeMap<Integer, Calendar> siblings;
	private Month month;
	private int index;
	private Calendar calendar;
	private int lastWeek;

	SingleWeek(Month month, int week) {
		CalendarAssert.checkWeekOfMonth(month, week);
		this.month = month;
		siblings = new TreeMap<Integer, Calendar>();
		Calendar calendar = CalendarUtils.setField(month.getTime(), Calendar.WEEK_OF_MONTH, week);
		siblings.put(week, calendar);
		this.lastWeek = week;
	}

	public SingleWeek andWeek(int week) {
		CalendarAssert.checkWeekOfMonth(month, week);
		Calendar calendar = CalendarUtils.setField(month.getTime(), Calendar.WEEK_OF_MONTH, week);
		siblings.put(week, calendar);
		this.lastWeek = week;
		return this;
	}

	public ConcreteWeek toWeek(int week, int interval) {
		CalendarAssert.checkWeekOfMonth(month, week);
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
		return new WeekDay(CollectionUtils.getFirst(this), day);
	}

	public Day everyDay(Function<Week, Integer> from, Function<Week, Integer> to, int interval) {
		return new EveryWeekDay(CollectionUtils.getFirst(this), from, to, interval);
	}

	public boolean hasNext() {
		boolean next = index < siblings.size();
		if (!next) {
			if (month.hasNext()) {
				month = month.next();
				index = 0;
				next = true;
			}
		}
		return next;
	}

	public Week next() {
		calendar = CollectionUtils.get(siblings.values().iterator(), index++);
		calendar.set(Calendar.YEAR, month.getYear());
		calendar.set(Calendar.MONTH, month.getMonth());
		return this;
	}

}
