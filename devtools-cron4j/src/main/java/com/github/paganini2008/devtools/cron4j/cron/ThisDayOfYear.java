package com.github.paganini2008.devtools.cron4j.cron;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.collection.MapUtils;

/**
 * 
 * ThisDayOfYear
 *
 * @author Fred Feng
 * @version 1.0
 */
public class ThisDayOfYear implements TheDay, Serializable {

	private static final long serialVersionUID = -8235489088108418524L;
	private final TreeMap<Integer, Calendar> siblings = new TreeMap<Integer, Calendar>();
	private Year year;
	private int index;
	private Calendar day;
	private int lastDay;

	ThisDayOfYear(Year year, int day) {
		CalendarAssert.checkDayOfYear(year, day);
		this.year = year;
		Calendar calendar = CalendarUtils.setField(year.getTime(), Calendar.DAY_OF_YEAR, day);
		this.siblings.put(day, calendar);
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

	public TheHour hour(int hour) {
		final Day copy = (Day) this.copy();
		return new ThisHour(CollectionUtils.getFirst(copy), hour);
	}

	public Hour everyHour(Function<Day, Integer> from, Function<Day, Integer> to, int interval) {
		final Day copy = (Day) this.copy();
		return new EveryHour(CollectionUtils.getFirst(copy), from, to, interval);
	}

	public Date getTime() {
		return day.getTime();
	}

	public long getTimeInMillis() {
		return day.getTimeInMillis();
	}

	public TheDay andDay(int day) {
		CalendarAssert.checkDayOfYear(year, day);
		Calendar calendar = CalendarUtils.setField(year.getTime(), Calendar.DAY_OF_YEAR, day);
		this.siblings.put(day, calendar);
		this.lastDay = day;
		return this;
	}

	public TheDay toDay(int day, int interval) {
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
		Map.Entry<Integer, Calendar> entry = MapUtils.getEntry(siblings, index++);
		day = entry.getValue();
		day.set(Calendar.YEAR, year.getYear());
		day.set(Calendar.DAY_OF_YEAR, Math.min(entry.getKey(), year.getLastDay()));
		return this;
	}

	public CronExpression getParent() {
		return year;
	}

}
