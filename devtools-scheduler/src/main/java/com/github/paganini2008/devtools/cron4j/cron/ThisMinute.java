package com.github.paganini2008.devtools.cron4j.cron;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * 
 * ThisMinute
 *
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public class ThisMinute implements TheMinute, Serializable {

	private static final long serialVersionUID = 7090607807516357598L;
	private final TreeMap<Integer, Calendar> siblings = new TreeMap<Integer, Calendar>();
	private Hour hour;
	private int index;
	private Calendar minute;
	private int lastMinute;
	private final StringBuilder cron = new StringBuilder();

	ThisMinute(Hour hour, int minute) {
		CalendarAssert.checkMinute(minute);
		this.hour = hour;
		Calendar calendar = CalendarUtils.setField(hour.getTime(), Calendar.MINUTE, minute);
		this.siblings.put(minute, calendar);
		this.minute = calendar;
		this.lastMinute = minute;
		this.cron.append(minute);
	}

	public ThisMinute andMinute(int minute) {
		return andMinute(minute, true);
	}

	private ThisMinute andMinute(int minute, boolean writeCron) {
		CalendarAssert.checkMinute(minute);
		Calendar calendar = CalendarUtils.setField(hour.getTime(), Calendar.MINUTE, minute);
		this.siblings.put(minute, calendar);
		this.lastMinute = minute;
		if (writeCron) {
			this.cron.append(",").append(minute);
		}
		return this;
	}

	public TheMinute toMinute(int minute, int interval) {
		CalendarAssert.checkMinute(minute);
		if (interval < 0) {
			throw new IllegalArgumentException("Invalid interval: " + interval);
		}
		for (int i = lastMinute + interval; i < minute; i += interval) {
			andMinute(i, false);
		}
		if (interval > 1) {
			this.cron.append("/").append(interval);
		} else {
			this.cron.append("-").append(minute);
		}
		return this;
	}

	public Date getTime() {
		return minute.getTime();
	}

	public long getTimeInMillis() {
		return minute.getTimeInMillis();
	}

	public int getYear() {
		return minute.get(Calendar.YEAR);
	}

	public int getMonth() {
		return minute.get(Calendar.MONTH);
	}

	public int getDay() {
		return minute.get(Calendar.DAY_OF_MONTH);
	}

	public int getHour() {
		return minute.get(Calendar.HOUR_OF_DAY);
	}

	public int getMinute() {
		return minute.get(Calendar.MINUTE);
	}

	public TheSecond second(int second) {
		final Minute copy = (Minute) this.copy();
		return new ThisSecond(CollectionUtils.getFirst(copy), second);
	}

	public Second everySecond(Function<Minute, Integer> from, Function<Minute, Integer> to, int interval) {
		final Minute copy = (Minute) this.copy();
		return new EverySecond(CollectionUtils.getFirst(copy), from, to, interval);
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
		minute = CollectionUtils.get(siblings.values().iterator(), index++);
		minute.set(Calendar.YEAR, hour.getYear());
		minute.set(Calendar.MONTH, hour.getMonth());
		minute.set(Calendar.DAY_OF_MONTH, hour.getDay());
		minute.set(Calendar.HOUR_OF_DAY, hour.getHour());
		return this;
	}

	public CronExpression getParent() {
		return hour;
	}

	public String toCronString() {
		return this.cron.toString();
	}

}
