package com.github.paganini2008.devtools.scheduler.cron;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.date.DateUtils;

/**
 * 
 * SingleMonth
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public class SingleMonth implements ConcreteMonth, Serializable {

	private static final long serialVersionUID = 229203112866380942L;
	private final TreeMap<Integer, Calendar> siblings;
	private Year year;
	private int index;
	private Calendar month;
	private int lastMonth;

	SingleMonth(Year year, int month) {
		CalendarAssert.checkMonth(month);
		this.year = year;
		siblings = new TreeMap<Integer, Calendar>();
		Calendar calendar = CalendarUtils.setField(year.getTime(), Calendar.MONTH, month);
		siblings.put(month, calendar);
		this.month = calendar;
		this.lastMonth = month;
	}

	public ConcreteMonth andMonth(int month) {
		CalendarAssert.checkMonth(month);
		Calendar calendar = CalendarUtils.setField(year.getTime(), Calendar.MONTH, month);
		siblings.put(month, calendar);
		this.lastMonth = month;
		return this;
	}

	public ConcreteMonth andNextMonths(int months) {
		CalendarAssert.checkMonth(lastMonth + months);
		Calendar calendar = CalendarUtils.setField(year.getTime(), Calendar.MONTH, lastMonth + months);
		int month = calendar.get(Calendar.MONTH);
		siblings.put(month, calendar);
		this.lastMonth = month;
		return this;
	}

	public ConcreteMonth toMonth(int month, int interval) {
		CalendarAssert.checkMonth(month);
		for (int i = lastMonth + interval; i <= month; i += interval) {
			andMonth(i);
		}
		return this;
	}

	public Date getTime() {
		return month.getTime();
	}

	public long getTimeInMillis() {
		return month.getTimeInMillis();
	}

	public int getYear() {
		return month.get(Calendar.YEAR);
	}

	public int getMonth() {
		return month.get(Calendar.MONTH);
	}

	public int getLasyDay() {
		return month.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	public int getWeekCount() {
		return month.getActualMaximum(Calendar.WEEK_OF_MONTH);
	}

	public ConcreteDay day(int day) {
		return new SingleDay(CollectionUtils.getFirst(this), day);
	}

	public Day lastDay() {
		return new LastDay(CollectionUtils.getFirst(this));
	}

	public Day everyDay(Function<Month, Integer> from, Function<Month, Integer> to, int interval) {
		return new EveryDay(CollectionUtils.getFirst(this), from, to, interval);
	}

	public ConcreteWeek week(int week) {
		return new SingleWeek(CollectionUtils.getFirst(this), week);
	}

	public Week lastWeek() {
		return new LastWeek(CollectionUtils.getFirst(this));
	}

	public Week everyWeek(Function<Month, Integer> from, Function<Month, Integer> to, int interval) {
		return new EveryWeek(CollectionUtils.getFirst(this), from, to, interval);
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

	public Month next() {
		month = CollectionUtils.get(siblings.values().iterator(), index++);
		month.set(Calendar.YEAR, year.getYear());
		return this;
	}

	public static void main(String[] args) {
		ConcreteYear singleYear = new SingleYear(2019);
		singleYear = singleYear.andYear(2024).andYear(2028);
		ConcreteMonth singleMonth = singleYear.July().andAug().andMonth(11);
		Day every = singleMonth.lastWeek().Fri().andSat();
		while (every.hasNext()) {
			Day time = every.next();
			System.out.println(DateUtils.format(time.getTime()));
		}
	}

}
