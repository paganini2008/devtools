package com.github.paganini2008.devtools.cron4j.cron;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.cron4j.CRON;

/**
 * 
 * EveryHour
 *
 * @author Fred Feng
 * @version 1.0
 */
public class EveryHour implements Hour, Serializable {

	private static final long serialVersionUID = -5459905273757712271L;
	private Day day;
	private final Calendar hour;
	private final int fromHour;
	private final int toHour;
	private final int interval;
	private boolean self;
	private boolean forward = true;

	EveryHour(Day day, Function<Day, Integer> from, Function<Day, Integer> to, int interval) {
		if (interval <= 0) {
			throw new IllegalArgumentException("Invalid interval: " + interval);
		}
		this.day = day;
		this.fromHour = from.apply(day);
		CalendarAssert.checkHourOfDay(fromHour);
		this.hour = CalendarUtils.setField(day.getTime(), Calendar.HOUR_OF_DAY, fromHour);
		this.interval = interval;
		this.self = true;
		this.toHour = to.apply(day);
		CalendarAssert.checkHourOfDay(toHour);
	}

	public boolean hasNext() {
		boolean next = self || hour.get(Calendar.HOUR_OF_DAY) + interval <= toHour;
		if (!next) {
			if (day.hasNext()) {
				day = day.next();
				hour.set(Calendar.YEAR, day.getYear());
				hour.set(Calendar.MONTH, day.getMonth());
				hour.set(Calendar.DAY_OF_MONTH, day.getDay());
				hour.set(Calendar.HOUR_OF_DAY, fromHour);
				forward = false;
				next = true;
			}
		}
		return next;
	}

	public Hour next() {
		if (self) {
			self = false;
		} else {
			if (forward) {
				hour.add(Calendar.HOUR_OF_DAY, interval);
			} else {
				forward = true;
			}
		}
		return this;
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

	public Date getTime() {
		return hour.getTime();
	}

	public long getTimeInMillis() {
		return hour.getTimeInMillis();
	}

	public TheMinute minute(int minute) {
		final Hour copy = (Hour) this.copy();
		return new ThisMinute(CollectionUtils.getFirst(copy), minute);
	}

	public Minute everyMinute(Function<Hour, Integer> from, Function<Hour, Integer> to, int interval) {
		final Hour copy = (Hour) this.copy();
		return new EveryMinute(CollectionUtils.getFirst(copy), from, to, interval);
	}

	public CronExpression getParent() {
		return day;
	}

	public String toCronString() {
		String s = toHour != 23 ? fromHour + "-" + toHour : fromHour + "";
		return interval > 1 ? s + "/" + interval : "*";
	}

	public String toString() {
		return CRON.toCronString(this);
	}

}
