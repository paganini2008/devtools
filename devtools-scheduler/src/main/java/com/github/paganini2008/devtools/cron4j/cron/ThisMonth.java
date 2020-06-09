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
 * ThisMonth
 *
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public class ThisMonth implements ThatMonth, Serializable {

	private static final long serialVersionUID = 229203112866380942L;
	private final TreeMap<Integer, Calendar> siblings;
	private Year year;
	private int index;
	private Calendar month;
	private int lastMonth;
	private final StringBuilder cron = new StringBuilder();

	ThisMonth(Year year, int month) {
		CalendarAssert.checkMonth(month);
		this.year = year;
		siblings = new TreeMap<Integer, Calendar>();
		Calendar calendar = CalendarUtils.setField(year.getTime(), Calendar.MONTH, month);
		siblings.put(month, calendar);
		this.month = calendar;
		this.lastMonth = month;
		this.cron.append(CalendarUtils.getMonthName(month));
	}

	public ThatMonth andMonth(int month) {
		return andMonth(month, true);
	}

	private ThatMonth andMonth(int month, boolean writeCron) {
		CalendarAssert.checkMonth(month);
		Calendar calendar = CalendarUtils.setField(year.getTime(), Calendar.MONTH, month);
		siblings.put(month, calendar);
		this.lastMonth = month;
		if (writeCron) {
			this.cron.append(",").append(CalendarUtils.getMonthName(month));
		}
		return this;
	}

	public ThatMonth toMonth(int month, int interval) {
		CalendarAssert.checkMonth(month);
		for (int i = lastMonth + interval; i <= month; i += interval) {
			andMonth(i, false);
		}
		if (interval > 1) {
			this.cron.append("/").append(interval);
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

	public int getLasyDay() {
		return month.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	public int getWeekCount() {
		return month.getActualMaximum(Calendar.WEEK_OF_MONTH);
	}

	public ThatDay day(int day) {
		return new ThisDay(CollectionUtils.getFirst(this), day);
	}

	public Day lastDay() {
		return new LastDay(CollectionUtils.getFirst(this));
	}

	public Day everyDay(Function<Month, Integer> from, Function<Month, Integer> to, int interval) {
		return new EveryDay(CollectionUtils.getFirst(this), from, to, interval);
	}

	public ThatWeek week(int week) {
		return new ThisWeek(CollectionUtils.getFirst(this), week);
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

	public CronExpression getParent() {
		return year;
	}

	public String toCronString() {
		return this.cron.toString();
	}

	public static void main(String[] args) {
		ThatYear singleYear = new ThisYear(2019);
		singleYear = singleYear.andYear(2024).andYear(2028);
		ThatMonth singleMonth = singleYear.July().andAug().andMonth(11);
		Day every = singleMonth.lastWeek().Fri().andSat();
		while (every.hasNext()) {
			Day time = every.next();
			System.out.println(DateUtils.format(time.getTime()));
		}
	}

}
