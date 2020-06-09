package com.github.paganini2008.devtools.cron4j.cron;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.date.DateUtils;

/**
 * 
 * ThisYear
 *
 * @author Fred Feng
 * @version 1.0
 */
public class ThisYear implements ThatYear, Serializable {

	private static final long serialVersionUID = -5316436238766770045L;
	private final TreeMap<Integer, Calendar> siblings;
	private Calendar year;
	private int index;
	private int lastYear;
	private final StringBuilder cron = new StringBuilder();

	ThisYear(int year) {
		CalendarAssert.checkYear(year);
		siblings = new TreeMap<Integer, Calendar>();
		Calendar calendar = CalendarUtils.setField(new Date(), Calendar.YEAR, year);
		siblings.put(year, calendar);
		this.year = calendar;
		this.lastYear = year;
		this.cron.append(year);
	}

	public ThatYear andYear(int year) {
		return andYear(year, true);
	}

	public ThatYear andYear(int year, boolean writeCron) {
		CalendarAssert.checkYear(year);
		Calendar calendar = CalendarUtils.setField(new Date(), Calendar.YEAR, year);
		siblings.put(year, calendar);
		this.lastYear = year;
		if (writeCron) {
			this.cron.append(",").append(year);
		}
		return this;
	}

	public ThatYear toYear(int year, int interval) {
		CalendarAssert.checkYear(year);
		if (interval < 0) {
			throw new IllegalArgumentException("Invalid interval: " + interval);
		}
		for (int i = lastYear + interval; i <= year; i += interval) {
			andYear(i, false);
		}
		if (interval > 1) {
			this.cron.append("/").append(interval);
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
		return new EveryMonth(CollectionUtils.getFirst(this), from, to, interval);
	}

	public ThatDay day(int day) {
		return new ThisDayOfYear(CollectionUtils.getFirst(this), day);
	}

	public ThatWeek week(int week) {
		return new ThisWeekOfYear(CollectionUtils.getFirst(this), week);
	}

	public ThatMonth month(int month) {
		return new ThisMonth(CollectionUtils.getFirst(this), month);
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

	public CronExpression getParent() {
		return null;
	}

	public String toCronString() {
		return this.cron.toString();
	}

	public static void main(String[] args) {
		ThatYear singleYear = new ThisYear(2020);
		singleYear = singleYear.andYear(2028).andYear(2024);
		Day day = singleYear.lastWeek().Mon().toFri();
		while (day.hasNext()) {
			Day time = day.next();
			System.out.println(DateUtils.format(time.getTime()));
		}
	}

}
