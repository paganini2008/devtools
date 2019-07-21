package com.github.paganini2008.devtools.scheduler.cron;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * 
 * SingleHour
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public class SingleHour implements ConcreteHour , Serializable{

	private static final long serialVersionUID = 8124589572544886753L;
	private final TreeMap<Integer, Calendar> siblings;
	private Day day;
	private int index;
	private Calendar calendar;
	private int lastHour;

	SingleHour(Day day, int hour) {
		CalendarAssert.checkHourOfDay(hour);
		this.day = day;
		siblings = new TreeMap<Integer, Calendar>();
		Calendar calendar = CalendarUtils.setField(day.getTime(), Calendar.HOUR_OF_DAY, hour);
		siblings.put(hour, calendar);
		this.lastHour = hour;
	}

	public SingleHour andHour(int hour) {
		CalendarAssert.checkHourOfDay(hour);
		Calendar calendar = CalendarUtils.setField(day.getTime(), Calendar.HOUR_OF_DAY, hour);
		siblings.put(hour, calendar);
		this.lastHour = hour;
		return this;
	}

	public ConcreteHour toHour(int hour, int interval) {
		CalendarAssert.checkHourOfDay(hour);
		for (int i = lastHour + interval; i < hour; i += interval) {
			andHour(i);
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

	public int getMonth() {
		return calendar.get(Calendar.MONTH);
	}

	public int getDay() {
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	public int getHour() {
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	public ConcreteMinute minute(int minute) {
		return new SingleMinute(CollectionUtils.getFirst(this), minute);
	}

	public Minute everyMinute(Function<Hour, Integer> from, Function<Hour, Integer> to, int interval) {
		return new EveryMinute(CollectionUtils.getFirst(this), from, to, interval);
	}

	public boolean hasNext() {
		boolean next = index < siblings.size();
		if (!next) {
			if (day.hasNext()) {
				day = day.next();
				index = 0;
				next = true;
			}
		}
		return next;
	}

	public Hour next() {
		calendar = CollectionUtils.get(siblings.values().iterator(), index++);
		calendar.set(Calendar.YEAR, day.getYear());
		calendar.set(Calendar.MONTH, day.getMonth());
		calendar.set(Calendar.DAY_OF_MONTH, day.getDay());
		return this;
	}

}
