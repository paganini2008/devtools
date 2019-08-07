package com.github.paganini2008.devtools.scheduler.cron;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * 
 * SingleDayOfWeek
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public class SingleDayOfWeek implements ConcreteWeekDay, Serializable {

	private static final long serialVersionUID = -5353496894925284106L;
	private final TreeMap<Integer, Calendar> siblings;
	private Week week;
	private int index;
	private Calendar calendar;
	private int lastDay;

	SingleDayOfWeek(Week week, int day) {
		CalendarAssert.checkDayOfWeek(day);
		this.week = week;
		this.siblings = new TreeMap<Integer, Calendar>();
		Calendar calendar = CalendarUtils.setField(week.getTime(), Calendar.DAY_OF_WEEK, day);
		siblings.put(day, calendar);
		this.lastDay = day;
	}

	public ConcreteWeekDay andDay(int day) {
		CalendarAssert.checkDayOfWeek(day);
		Calendar calendar = CalendarUtils.setField(week.getTime(), Calendar.DAY_OF_WEEK, day);
		siblings.put(day, calendar);
		this.lastDay = day;
		return this;
	}

	public ConcreteWeekDay toDay(int day, int interval) {
		CalendarAssert.checkDayOfWeek(day);
		for (int i = lastDay + interval; i <= day; i += interval) {
			andDay(i);
		}
		return this;
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

	public int getDay() {
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	public int getWeekDay() {
		return calendar.get(Calendar.DAY_OF_WEEK);
	}

	public int getDayOfYear() {
		return calendar.get(Calendar.DAY_OF_YEAR);
	}

	public ConcreteHour hour(int hour) {
		return new SingleHour(CollectionUtils.getFirst(this), hour);
	}

	public Hour everyHour(Function<Day, Integer> from, Function<Day, Integer> to, int interval) {
		return new EveryHour(CollectionUtils.getFirst(this), from, to, interval);
	}

	public boolean hasNext() {
		boolean next = index < siblings.size();
		if (!next) {
			if (week.hasNext()) {
				week = week.next();
				index = 0;
				next = true;
			}
		}
		return next;
	}

	public Day next() {
		calendar = CollectionUtils.get(siblings.values().iterator(), index++);
		calendar.set(Calendar.YEAR, week.getYear());
		calendar.set(Calendar.MONTH, week.getMonth());
		calendar.set(Calendar.WEEK_OF_MONTH, week.getWeek());
		return this;
	}

}