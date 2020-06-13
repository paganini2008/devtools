package com.github.paganini2008.devtools.cron4j.cron;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * 
 * ThisDayOfWeek
 *
 * @author Fred Feng
 * @version 1.0
 */
public class ThisDayOfWeek implements TheDayOfWeek, Serializable {

	private static final long serialVersionUID = -5353496894925284106L;
	private final TreeMap<Integer, Calendar> siblings = new TreeMap<Integer, Calendar>();
	private Week week;
	private int index;
	private Calendar day;
	private int lastDay;
	private final StringBuilder cron = new StringBuilder();

	ThisDayOfWeek(Week week, int day) {
		CalendarAssert.checkDayOfWeek(day);
		this.week = week;
		Calendar calendar = CalendarUtils.setField(week.getTime(), Calendar.DAY_OF_WEEK, day);
		this.siblings.put(day, calendar);
		this.lastDay = day;
		this.cron.append(getDayOfWeekName(day));
	}

	public TheDayOfWeek andDay(int day) {
		return andDay(day, true);
	}

	private TheDayOfWeek andDay(int day, boolean writeCron) {
		CalendarAssert.checkDayOfWeek(day);
		Calendar calendar = CalendarUtils.setField(week.getTime(), Calendar.DAY_OF_WEEK, day);
		this.siblings.put(day, calendar);
		this.lastDay = day;
		if (writeCron) {
			this.cron.append(",").append(getDayOfWeekName(day));
		}
		return this;
	}

	private String getDayOfWeekName(int day) {
		if (week instanceof LastWeekOfMonth || week instanceof ThisWeek) {
			return day + week.toCronString();
		}
		return CalendarUtils.getDayOfWeekName(day);
	}

	public TheDayOfWeek toDay(int day, int interval) {
		CalendarAssert.checkDayOfWeek(day);
		for (int i = lastDay + interval; i <= day; i += interval) {
			andDay(i, false);
		}
		if (interval > 1) {
			this.cron.append("/").append(interval);
		} else {
			this.cron.append("-").append(getDayOfWeekName(day));
		}
		return this;
	}

	public Date getTime() {
		return day.getTime();
	}

	public long getTimeInMillis() {
		return day.getTimeInMillis();
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

	public boolean hasNext() {
		boolean next = index < siblings.size();
		if (!next) {
			if (week.hasNext()) {
				week = week.next();
				index = 0;
				next = true;
			}
		}
		return next;
	}

	public Day next() {
		day = CollectionUtils.get(siblings.values().iterator(), index++);
		day.set(Calendar.YEAR, week.getYear());
		day.set(Calendar.MONTH, week.getMonth());
		day.set(Calendar.WEEK_OF_MONTH, week.getWeek());
		return this;
	}

	public CronExpression getParent() {
		return week;
	}

	public String toCronString() {
		return this.cron.toString();
	}

}
