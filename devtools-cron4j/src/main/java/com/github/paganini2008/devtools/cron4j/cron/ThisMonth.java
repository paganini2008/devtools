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
 * ThisMonth
 *
 * @author Jimmy Hoff
 * 
 * 
 * @version 1.0
 */
public class ThisMonth implements TheMonth, Serializable {

	private static final long serialVersionUID = 229203112866380942L;
	private final TreeMap<Integer, Calendar> siblings = new TreeMap<Integer, Calendar>();
	private Year year;
	private int index;
	private Calendar month;
	private int lastMonth;
	private final StringBuilder cron;

	ThisMonth(Year year, int month) {
		CalendarAssert.checkMonth(month);
		this.year = year;
		Calendar calendar = CalendarUtils.setField(year.getTime(), Calendar.MONTH, month);
		this.siblings.put(month, calendar);
		this.month = calendar;
		this.lastMonth = month;
		this.cron = new StringBuilder().append(month);
	}

	public TheMonth andMonth(int month) {
		return andMonth(month, true);
	}

	private TheMonth andMonth(int month, boolean writeCron) {
		CalendarAssert.checkMonth(month);
		Calendar calendar = CalendarUtils.setField(year.getTime(), Calendar.MONTH, month);
		this.siblings.put(month, calendar);
		this.lastMonth = month;
		if (writeCron) {
			this.cron.append(",").append(CalendarUtils.getMonthName(month));
		}
		return this;
	}

	public TheMonth toMonth(int month, int interval) {
		CalendarAssert.checkMonth(month);
		for (int i = lastMonth + interval; i <= month; i += interval) {
			andMonth(i, false);
		}
		if (interval > 1) {
			this.cron.append("-").append(month).append("/").append(interval);
		} else {
			this.cron.append("-").append(month);
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

	public int getLastDay() {
		return month.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	public int getWeekday(int dayOfMonth) {
		Calendar c = (Calendar) month.clone();
		c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		while (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
			if (dayOfWeek == Calendar.SATURDAY) {
				c.set(Calendar.DAY_OF_WEEK, dayOfWeek - 1);
			} else if (dayOfWeek == Calendar.SUNDAY) {
				c.set(Calendar.DAY_OF_WEEK, dayOfWeek + 1);
			}
			dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		}
		return c.get(Calendar.DAY_OF_MONTH);
	}

	public int getWeekCount() {
		return month.getActualMaximum(Calendar.WEEK_OF_MONTH);
	}

	public TheDay day(int day) {
		final Month copy = (Month) this.copy();
		return new ThisDay(CollectionUtils.getFirst(copy), day);
	}

	public Day lastDay() {
		final Month copy = (Month) this.copy();
		return new LastDayOfMonth(CollectionUtils.getFirst(copy));
	}

	public Day everyDay(Function<Month, Integer> from, Function<Month, Integer> to, int interval) {
		final Month copy = (Month) this.copy();
		return new EveryDay(CollectionUtils.getFirst(copy), from, to, interval);
	}

	public TheWeek week(int week) {
		final Month copy = (Month) this.copy();
		return new ThisWeek(CollectionUtils.getFirst(copy), week);
	}

	public TheDayOfWeekInMonth dayOfWeek(int week, int dayOfWeek) {
		final Month copy = (Month) this.copy();
		return new ThisDayOfWeekInMonth(CollectionUtils.getFirst(copy), week, dayOfWeek);
	}

	public Week lastWeek() {
		final Month copy = (Month) this.copy();
		return new LastWeekOfMonth(CollectionUtils.getFirst(copy));
	}

	public Week everyWeek(Function<Month, Integer> from, Function<Month, Integer> to, int interval) {
		final Month copy = (Month) this.copy();
		return new EveryWeek(CollectionUtils.getFirst(copy), from, to, interval);
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

	public CronExpression getParent() {
		return year;
	}

	public String toCronString() {
		return this.cron.toString();
	}

	public String toString() {
		return CRON.toCronString(this);
	}

	public static void main(String[] args) {
		TheYear singleYear = new ThisYear(2021);
		singleYear = singleYear.andYear(2024).andYear(2028);
		TheMonth singleMonth = singleYear.July().andAug().andMonth(11);
		Day every = singleMonth.lastWeek().Fri().andSat();
		System.out.println(every);
	}

}
