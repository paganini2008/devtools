package com.github.paganini2008.devtools.cron4j.cron;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.cron4j.CRON;

/**
 * 
 * ThisYear
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class ThisYear implements TheYear, Serializable {

	private static final long serialVersionUID = -5316436238766770045L;
	private final TreeMap<Integer, Calendar> siblings = new TreeMap<Integer, Calendar>();
	private Calendar year;
	private int index;
	private int lastYear;
	private final StringBuilder cron;

	public ThisYear(int year) {
		CalendarAssert.checkYear(year);
		Calendar calendar = CalendarUtils.setField(new Date(), Calendar.YEAR, year);
		this.siblings.put(year, calendar);
		this.year = calendar;
		this.lastYear = year;
		this.cron = new StringBuilder().append(year);
	}

	public TheYear andYear(int year) {
		return andYear(year, true);
	}

	public TheYear andYear(int year, boolean writeCron) {
		CalendarAssert.checkYear(year);
		Calendar calendar = CalendarUtils.setField(new Date(), Calendar.YEAR, year);
		siblings.put(year, calendar);
		this.lastYear = year;
		if (writeCron) {
			this.cron.append(",").append(year);
		}
		return this;
	}

	public TheYear toYear(int year, int interval) {
		CalendarAssert.checkYear(year);
		if (interval < 0) {
			throw new IllegalArgumentException("Invalid interval: " + interval);
		}
		for (int i = lastYear + interval; i <= year; i += interval) {
			andYear(i, false);
		}
		if (interval > 1) {
			this.cron.append("-").append(year).append("/").append(interval);
		} else {
			this.cron.append("-").append(year);
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
		final Year copy = (Year) this.copy();
		return new EveryMonth(CollectionUtils.getFirst(copy), from, to, interval);
	}

	public TheDay day(int day) {
		final Year copy = (Year) this.copy();
		return new ThisDayOfYear(CollectionUtils.getFirst(copy), day);
	}

	public TheWeek week(int week) {
		final Year copy = (Year) this.copy();
		return new ThisWeekOfYear(CollectionUtils.getFirst(copy), week);
	}

	public TheMonth month(int month) {
		final Year copy = (Year) this.copy();
		return new ThisMonth(CollectionUtils.getFirst(copy), month);
	}

	public Week lastWeek() {
		final Year copy = (Year) this.copy();
		return new LastWeekOfYear(CollectionUtils.getFirst(copy));
	}

	public boolean hasNext() {
		return index < siblings.size();
	}

	public Year next() {
		year = CollectionUtils.get(siblings.values().iterator(), index++);
		return this;
	}

	public CronExpression getParent() {
		return null;
	}

	public String toCronString() {
		return this.cron.toString();
	}

	public String toString() {
		return CRON.toCronString(this);
	}

	public static void main(String[] args) {
		TheYear singleYear = new ThisYear(2021);
		singleYear = singleYear.andYear(2028).andYear(2024);
		Day day = singleYear.lastWeek().Mon().toFri();
		System.out.println(day);
	}

}
