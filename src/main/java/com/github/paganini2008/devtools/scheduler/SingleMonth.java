package com.github.paganini2008.devtools.scheduler;

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
public class SingleMonth implements ConcreteMonth {

	private final TreeMap<Integer, Calendar> siblings;
	private Year year;
	private int index;
	private Calendar calendar;

	SingleMonth(Year year, int month) {
		this.year = year;
		siblings = new TreeMap<Integer, Calendar>();
		Calendar calendar = CalendarUtils.setField(year.getTime(), Calendar.MONTH, month);
		siblings.put(month, calendar);
	}

	public ConcreteMonth and(int month) {
		Calendar calendar = CalendarUtils.setField(year.getTime(), Calendar.MONTH, month);
		siblings.put(month, calendar);
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

	public int getLasyDay() {
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	public ConcreteDay day(int day) {
		return new SingleDay(CollectionUtils.getFirst(this), day);
	}

	public Day everyDay(Function<Month, Integer> from, Function<Month, Integer> to, int interval) {
		return new EveryDay(CollectionUtils.getFirst(this), from, to, interval);
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
		calendar = CollectionUtils.get(siblings.values().iterator(), index++);
		calendar.set(Calendar.YEAR, year.getYear());
		return this;
	}

	public static void main(String[] args) {
		ConcreteYear singleYear = new SingleYear(2019);
		singleYear = singleYear.and(2024).and(2028);
		ConcreteMonth singleMonth = singleYear.month(9).and(11);
		ConcreteDay singleDay = singleMonth.day(15).and(18);
		while (singleDay.hasNext()) {
			Day day = singleDay.next();
			System.out.println(DateUtils.format(day.getTime()));
		}
	}

}
