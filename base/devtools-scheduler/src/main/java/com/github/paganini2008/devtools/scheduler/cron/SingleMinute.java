package com.github.paganini2008.devtools.scheduler.cron;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * 
 * SingleMinute
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public class SingleMinute implements ConcreteMinute, Serializable {

	private static final long serialVersionUID = 7090607807516357598L;
	private final TreeMap<Integer, Calendar> siblings;
	private Hour hour;
	private int index;
	private Calendar calendar;
	private int lastMinute;

	SingleMinute(Hour hour, int minute) {
		CalendarAssert.checkMinute(minute);
		this.hour = hour;
		siblings = new TreeMap<Integer, Calendar>();
		Calendar calendar = CalendarUtils.setField(hour.getTime(), Calendar.MINUTE, minute);
		siblings.put(minute, calendar);
		this.lastMinute = minute;
	}

	public SingleMinute andMinute(int minute) {
		CalendarAssert.checkMinute(minute);
		Calendar calendar = CalendarUtils.setField(hour.getTime(), Calendar.MINUTE, minute);
		siblings.put(minute, calendar);
		this.lastMinute = minute;
		return this;
	}

	public ConcreteMinute toMinute(int minute, int interval) {
		CalendarAssert.checkMinute(minute);
		for (int i = lastMinute + interval; i < minute; i += interval) {
			andMinute(i);
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

	public int getMinute() {
		return calendar.get(Calendar.MINUTE);
	}

	public ConcreteSecond second(int second) {
		return new SingleSecond(CollectionUtils.getFirst(this), second);
	}

	public Second everySecond(Function<Minute, Integer> from, Function<Minute, Integer> to, int interval) {
		return new EverySecond(CollectionUtils.getFirst(this), from, to, interval);
	}

	public boolean hasNext() {
		boolean next = index < siblings.size();
		if (!next) {
			if (hour.hasNext()) {
				hour = hour.next();
				index = 0;
				next = true;
			}
		}
		return next;
	}

	public Minute next() {
		calendar = CollectionUtils.get(siblings.values().iterator(), index++);
		calendar.set(Calendar.YEAR, hour.getYear());
		calendar.set(Calendar.MONTH, hour.getMonth());
		calendar.set(Calendar.DAY_OF_MONTH, hour.getDay());
		calendar.set(Calendar.HOUR_OF_DAY, hour.getHour());
		return this;
	}

}