package com.github.paganini2008.devtools.cron4j.cron;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * 
 * ThisDayOfYear
 *
 * @author Fred Feng
 * @version 1.0
 */
public class ThisDayOfYear implements ThatDay, Serializable {

	private static final long serialVersionUID = -8235489088108418524L;
	private final TreeMap<Integer, Calendar> siblings;
	private Year year;
	private int index;
	private Calendar day;
	private int lastDay;

	ThisDayOfYear(Year year, int day) {
		CalendarAssert.checkDayOfYear(year, day);
		this.year = year;
		siblings = new TreeMap<Integer, Calendar>();
		Calendar calendar = CalendarUtils.setField(year.getTime(), Calendar.DAY_OF_YEAR, day);
		siblings.put(day, calendar);
		this.lastDay = day;
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

	public int getDayOfWeek() {
		return day.get(Calendar.DAY_OF_WEEK);
	}

	public int getDayOfYear() {
		return day.get(Calendar.DAY_OF_YEAR);
	}

	public ThatHour hour(int hour) {
		return new ThisHour(CollectionUtils.getFirst(this), hour);
	}

	public Hour everyHour(Function<Day, Integer> from, Function<Day, Integer> to, int interval) {
		return new EveryHour(CollectionUtils.getFirst(this), from, to, interval);
	}

	public Date getTime() {
		return day.getTime();
	}

	public long getTimeInMillis() {
		return day.getTimeInMillis();
	}

	public ThatDay andDay(int day) {
		CalendarAssert.checkDayOfYear(year, day);
		Calendar calendar = CalendarUtils.setField(year.getTime(), Calendar.DAY_OF_YEAR, day);
		siblings.put(day, calendar);
		this.lastDay = day;
		return this;
	}

	public ThatDay toDay(int day, int interval) {
		CalendarAssert.checkDayOfYear(year, day);
		for (int i = lastDay + interval; i < day; i += interval) {
			andDay(i);
		}
		return this;
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

	public Day next() {
		day = CollectionUtils.get(siblings.values().iterator(), index++);
		day.set(Calendar.YEAR, year.getYear());
		return this;
	}
	
	public CronExpression getParent() {
		return year;
	}

}
