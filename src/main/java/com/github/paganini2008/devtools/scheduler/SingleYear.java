package com.github.paganini2008.devtools.scheduler;

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
public class SingleYear implements ConcreteYear {

	private final TreeMap<Integer, Calendar> siblings;
	private Calendar calendar;
	private int index;

	SingleYear(int year) {
		siblings = new TreeMap<Integer, Calendar>();
		Calendar calendar = CalendarUtils.setField(new Date(), Calendar.YEAR, year);
		siblings.put(year, calendar);
	}

	public ConcreteYear and(int year) {
		Calendar calendar = CalendarUtils.setField(new Date(), Calendar.YEAR, year);
		siblings.put(year, calendar);
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

	public Month everyMonth(Function<Year, Integer> from, Function<Year, Integer> to, int interval) {
		return new EveryMonth(CollectionUtils.getFirst(this), from, to, interval);
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
		singleYear = singleYear.and(2028).and(2024);
		Month everyMonth = singleYear.everyMonth(2);
		while (everyMonth.hasNext()) {
			Month month = everyMonth.next();
			System.out.println(DateUtils.format(month.getTime()));
		}
	}

}
