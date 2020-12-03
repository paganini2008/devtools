package com.github.paganini2008.devtools.cron4j.cron;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * 
 * ThisHour
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class ThisHour implements TheHour, Serializable {

	private static final long serialVersionUID = 8124589572544886753L;
	private final TreeMap<Integer, Calendar> siblings = new TreeMap<Integer, Calendar>();
	private Day day;
	private int index;
	private Calendar hour;
	private int lastHour;
	private final StringBuilder cron = new StringBuilder();

	ThisHour(Day day, int hour) {
		CalendarAssert.checkHourOfDay(hour);
		this.day = day;
		Calendar calendar = CalendarUtils.setField(day.getTime(), Calendar.HOUR_OF_DAY, hour);
		this.siblings.put(hour, calendar);
		this.hour = calendar;
		this.lastHour = hour;
		this.cron.append(hour);
	}

	public ThisHour andHour(int hour) {
		return andHour(hour, true);
	}

	private ThisHour andHour(int hour, boolean writeCron) {
		CalendarAssert.checkHourOfDay(hour);
		Calendar calendar = CalendarUtils.setField(day.getTime(), Calendar.HOUR_OF_DAY, hour);
		siblings.put(hour, calendar);
		this.lastHour = hour;
		if (writeCron) {
			cron.append(",").append(hour);
		}
		return this;
	}

	public TheHour toHour(int hour, int interval) {
		CalendarAssert.checkHourOfDay(hour);
		if (interval < 0) {
			throw new IllegalArgumentException("Invalid interval: " + interval);
		}
		for (int i = lastHour + interval; i < hour; i += interval) {
			andHour(i, false);
		}
		if (interval > 1) {
			cron.append("/").append(interval);
		} else {
			cron.append("-").append(hour);
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

	public TheMinute minute(int minute) {
		final Hour copy = (Hour) this.copy();
		return new ThisMinute(CollectionUtils.getFirst(copy), minute);
	}

	public Minute everyMinute(Function<Hour, Integer> from, Function<Hour, Integer> to, int interval) {
		final Hour copy = (Hour) this.copy();
		return new EveryMinute(CollectionUtils.getFirst(copy), from, to, interval);
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

	public CronExpression getParent() {
		return day;
	}

	public String toCronString() {
		return this.cron.toString();
	}

}
