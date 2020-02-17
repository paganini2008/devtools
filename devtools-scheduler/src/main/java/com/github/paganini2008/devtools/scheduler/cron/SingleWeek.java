package com.github.paganini2008.devtools.scheduler.cron;

import java.io.Serializable;
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
 * 
 * 
 * @version 1.0
 */
public class SingleWeek implements OneWeek, Serializable {

	private static final long serialVersionUID = -4563991137870265612L;
	private final TreeMap<Integer, Calendar> siblings;
	private Month month;
	private int index;
	private Calendar week;
	private int lastWeek;

	SingleWeek(Month month, int week) {
		CalendarAssert.checkWeekOfMonth(month, week);
		this.month = month;
		siblings = new TreeMap<Integer, Calendar>();
		Calendar calendar = CalendarUtils.setField(month.getTime(), Calendar.WEEK_OF_MONTH, week);
		siblings.put(week, calendar);
		this.week = calendar;
		this.lastWeek = week;
	}

	public SingleWeek andWeek(int week) {
		CalendarAssert.checkWeekOfMonth(month, week);
		Calendar calendar = CalendarUtils.setField(month.getTime(), Calendar.WEEK_OF_MONTH, week);
		siblings.put(week, calendar);
		this.lastWeek = week;
		return this;
	}

	public OneWeek toWeek(int week, int interval) {
		CalendarAssert.checkWeekOfMonth(month, week);
		for (int i = lastWeek + interval; i < week; i += interval) {
			andWeek(i);
		}
		return null;
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
			if (month.hasNext()) {
				month = month.next();
				index = 0;
				next = true;
			}
		}
		return next;
	}

	public Week next() {
		week = CollectionUtils.get(siblings.values().iterator(), index++);
		week.set(Calendar.YEAR, month.getYear());
		week.set(Calendar.MONTH, month.getMonth());
		return this;
	}

}
