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
 * SingleYear
 *
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public class SingleYear implements OneYear, Serializable {

	private static final long serialVersionUID = -5316436238766770045L;
	private final TreeMap<Integer, Calendar> siblings;
	private Calendar year;
	private int index;
	private int lastYear;

	SingleYear(int year) {
		CalendarAssert.checkYear(year);
		siblings = new TreeMap<Integer, Calendar>();
		Calendar calendar = CalendarUtils.setField(new Date(), Calendar.YEAR, year);
		siblings.put(year, calendar);
		this.year = calendar;
		this.lastYear = year;
	}

	public OneYear andYear(int year) {
		CalendarAssert.checkYear(year);
		Calendar calendar = CalendarUtils.setField(new Date(), Calendar.YEAR, year);
		siblings.put(year, calendar);
		this.lastYear = year;
		return this;
	}

	public OneYear andNextYears(int years) {
		CalendarAssert.checkYear(lastYear + years);
		Calendar calendar = CalendarUtils.setField(new Date(), Calendar.YEAR, lastYear + years);
		int year = calendar.get(Calendar.YEAR);
		siblings.put(year, calendar);
		this.lastYear = year;
		return this;
	}

	public OneYear toYear(int year, int interval) {
		CalendarAssert.checkYear(year);
		for (int i = lastYear + interval; i <= year; i += interval) {
			andYear(i);
		}
		return this;
	}

	public Date getTime() {
		return year.getTime();
	}

	public long getTimeInMillis() {
		return year.getTimeInMillis();
	}

	public int getYear() {
		return year.get(Calendar.YEAR);
	}

	public int getWeekCount() {
		return year.getActualMaximum(Calendar.WEEK_OF_YEAR);
	}

	public int getLastDay() {
		return year.getActualMaximum(Calendar.DAY_OF_YEAR);
	}

	public Month everyMonth(Function<Year, Integer> from, Function<Year, Integer> to, int interval) {
		return new EveryMonth(CollectionUtils.getFirst(this), from, to, interval);
	}

	public OneDay day(int day) {
		return new SingleDayOfYear(CollectionUtils.getFirst(this), day);
	}

	public OneWeek week(int week) {
		return new SingleWeekOfYear(CollectionUtils.getFirst(this), week);
	}

	public OneMonth month(int month) {
		return new SingleMonth(CollectionUtils.getFirst(this), month);
	}
	
	public Week lastWeek() {
		return new LastWeekOfYear(CollectionUtils.getFirst(this));
	}

	public boolean hasNext() {
		return index < siblings.size();
	}

	public Year next() {
		year = CollectionUtils.get(siblings.values().iterator(), index++);
		return this;
	}

	public static void main(String[] args) {
		OneYear singleYear = new SingleYear(2019);
		singleYear = singleYear.andYear(2028).andYear(2024);
		Day day = singleYear.lastWeek().Mon().toFri();
		while (day.hasNext()) {
			Day time = day.next();
			System.out.println(DateUtils.format(time.getTime()));
		}
	}

}
