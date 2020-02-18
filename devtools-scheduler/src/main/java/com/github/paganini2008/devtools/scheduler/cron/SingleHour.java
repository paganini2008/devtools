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
 * 
 * 
 * @version 1.0
 */
public class SingleHour implements OneHour , Serializable{

	private static final long serialVersionUID = 8124589572544886753L;
	private final TreeMap<Integer, Calendar> siblings;
	private Day day;
	private int index;
	private Calendar hour;
	private int lastHour;

	SingleHour(Day day, int hour) {
		CalendarAssert.checkHourOfDay(hour);
		this.day = day;
		siblings = new TreeMap<Integer, Calendar>();
		Calendar calendar = CalendarUtils.setField(day.getTime(), Calendar.HOUR_OF_DAY, hour);
		siblings.put(hour, calendar);
		this.hour = calendar;
		this.lastHour = hour;
	}

	public SingleHour andHour(int hour) {
		CalendarAssert.checkHourOfDay(hour);
		Calendar calendar = CalendarUtils.setField(day.getTime(), Calendar.HOUR_OF_DAY, hour);
		siblings.put(hour, calendar);
		this.lastHour = hour;
		return this;
	}

	public OneHour toHour(int hour, int interval) {
		CalendarAssert.checkHourOfDay(hour);
		for (int i = lastHour + interval; i < hour; i += interval) {
			andHour(i);
		}
		return this;
	}

	public Date getTime() {
		return hour.getTime();
	}

	public long getTimeInMillis() {
		return hour.getTimeInMillis();
	}

	public int getYear() {
		return hour.get(Calendar.YEAR);
	}

	public int getMonth() {
		return hour.get(Calendar.MONTH);
	}

	public int getDay() {
		return hour.get(Calendar.DAY_OF_MONTH);
	}

	public int getHour() {
		return hour.get(Calendar.HOUR_OF_DAY);
	}

	public OneMinute minute(int minute) {
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
		hour = CollectionUtils.get(siblings.values().iterator(), index++);
		hour.set(Calendar.YEAR, day.getYear());
		hour.set(Calendar.MONTH, day.getMonth());
		hour.set(Calendar.DAY_OF_MONTH, day.getDay());
		return this;
	}

}