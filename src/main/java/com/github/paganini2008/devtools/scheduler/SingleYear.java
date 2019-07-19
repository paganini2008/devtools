package com.github.paganini2008.devtools.scheduler;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.date.DateUtils;

/**
 * 
 * SingleYear
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public class SingleYear implements ConcreteYear, Serializable {

	private static final long serialVersionUID = -5316436238766770045L;
	private final TreeMap<Integer, Calendar> siblings;
	private Calendar calendar;
	private int index;
	private int lastYear;

	SingleYear(int year) {
		CalendarAssert.checkYear(year);
		siblings = new TreeMap<Integer, Calendar>();
		Calendar calendar = CalendarUtils.setField(new Date(), Calendar.YEAR, year);
		siblings.put(year, calendar);
		this.lastYear = year;
	}

	public ConcreteYear andYear(int year) {
		CalendarAssert.checkYear(year);
		Calendar calendar = CalendarUtils.setField(new Date(), Calendar.YEAR, year);
		siblings.put(year, calendar);
		this.lastYear = year;
		return this;
	}

	public ConcreteYear andNextYears(int years) {
		CalendarAssert.checkYear(lastYear + years);
		Calendar calendar = CalendarUtils.setField(new Date(), Calendar.YEAR, lastYear + years);
		int year = calendar.get(Calendar.YEAR);
		siblings.put(year, calendar);
		this.lastYear = year;
		return this;
	}

	public ConcreteYear toYear(int year, int interval) {
		CalendarAssert.checkYear(year);
		for (int i = lastYear + interval; i <= year; i += interval) {
			andYear(i);
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

	public int getWeekCount() {
		return calendar.getActualMaximum(Calendar.WEEK_OF_YEAR);
	}

	public Month everyMonth(Function<Year, Integer> from, Function<Year, Integer> to, int interval) {
		return new EveryMonth(CollectionUtils.getFirst(this), from, to, interval);
	}

	public ConcreteWeek week(int week) {
		return new SingleWeekOfYear(CollectionUtils.getFirst(this), week);
	}

	public ConcreteMonth month(int month) {
		return new SingleMonth(CollectionUtils.getFirst(this), month);
	}

	public boolean hasNext() {
		return index < siblings.size();
	}

	public Year next() {
		calendar = CollectionUtils.get(siblings.values().iterator(), index++);
		return this;
	}

	public static void main(String[] args) {
		ConcreteYear singleYear = new SingleYear(2019);
		singleYear = singleYear.andYear(2028).andYear(2024).andNextYear().toYear(2035);
		Month everyMonth = singleYear.everyMonth(2);
		while (everyMonth.hasNext()) {
			Month month = everyMonth.next();
			System.out.println(DateUtils.format(month.getTime()));
		}
	}

}
