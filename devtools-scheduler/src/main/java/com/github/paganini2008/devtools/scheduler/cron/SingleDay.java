package com.github.paganini2008.devtools.scheduler.cron;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * 
 * SingleDay
 *
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public class SingleDay implements OneDay, Serializable {

	private static final long serialVersionUID = -6007054113405112202L;
	private final TreeMap<Integer, Calendar> siblings;
	private Month month;
	private int index;
	private Calendar day;
	private int lastDay;

	SingleDay(Month month, int day) {
		CalendarAssert.checkDayOfMonth(month, day);
		this.month = month;
		siblings = new TreeMap<Integer, Calendar>();
		Calendar calendar = CalendarUtils.setField(month.getTime(), Calendar.DAY_OF_MONTH, day);
		siblings.put(day, calendar);
		this.day = calendar;
		this.lastDay = day;
	}

	public OneDay andDay(int day) {
		CalendarAssert.checkDayOfMonth(month, day);
		Calendar calendar = CalendarUtils.setField(month.getTime(), Calendar.DAY_OF_MONTH, day);
		siblings.put(day, calendar);
		this.lastDay = day;
		return this;
	}

	public OneDay andNextDays(int days) {
		CalendarAssert.checkDayOfMonth(month, lastDay + days);
		Calendar calendar = CalendarUtils.setField(month.getTime(), Calendar.DAY_OF_MONTH, lastDay + days);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		siblings.put(day, calendar);
		this.lastDay = day;
		return this;
	}

	public OneDay toDay(int day, int interval) {
		CalendarAssert.checkDayOfMonth(month, day);
		for (int i = lastDay + interval; i <= day; i += interval) {
			andDay(i);
		}
		return this;
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

	public int getWeekDay() {
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

	public Day next() {
		day = CollectionUtils.get(siblings.values().iterator(), index++);
		day.set(Calendar.YEAR, month.getYear());
		day.set(Calendar.MONTH, month.getMonth());
		return this;
	}

}
