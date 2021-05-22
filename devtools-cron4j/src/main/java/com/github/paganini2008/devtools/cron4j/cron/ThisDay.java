package com.github.paganini2008.devtools.cron4j.cron;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.cron4j.CRON;

/**
 * 
 * ThisDay
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class ThisDay implements TheDay, Serializable {

	private static final long serialVersionUID = -6007054113405112202L;
	private final TreeMap<Integer, Calendar> siblings = new TreeMap<Integer, Calendar>();
	private Month month;
	private int index;
	private Calendar day;
	private int lastDay;
	private final StringBuilder cron;

	ThisDay(Month month, int day) {
		CalendarAssert.checkDayOfMonth(month, day);
		this.month = month;
		Calendar calendar = CalendarUtils.setField(month.getTime(), Calendar.DAY_OF_MONTH, day);
		this.siblings.put(day, calendar);
		this.day = calendar;
		this.lastDay = day;
		this.cron = new StringBuilder().append(day);
	}

	public TheDay andDay(int day) {
		return andDay(day, true);
	}

	private TheDay andDay(int day, boolean writeCron) {
		CalendarAssert.checkDayOfMonth(month, day);
		Calendar calendar = CalendarUtils.setField(month.getTime(), Calendar.DAY_OF_MONTH, day);
		this.siblings.put(day, calendar);
		this.lastDay = day;
		if (writeCron) {
			this.cron.append(",").append(day);
		}
		return this;
	}

	public TheDay toDay(int day, int interval) {
		CalendarAssert.checkDayOfMonth(month, day);
		if (interval < 0) {
			throw new IllegalArgumentException("Invalid interval: " + interval);
		}
		for (int i = lastDay + interval; i <= day; i += interval) {
			andDay(i, false);
		}
		if (interval > 1) {
			this.cron.append("-").append(day).append("/").append(interval);
		} else {
			this.cron.append("-").append(day);
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
			if (month.hasNext()) {
				month = month.next();
				index = 0;
				next = true;
			}
		}
		return next;
	}

	public Day next() {
		Map.Entry<Integer, Calendar> entry = MapUtils.getEntry(siblings, index++);
		day = entry.getValue();
		day.set(Calendar.YEAR, month.getYear());
		day.set(Calendar.MONTH, month.getMonth());
		day.set(Calendar.DAY_OF_MONTH, Math.min(entry.getKey(), month.getLasyDay()));
		return this;
	}

	public CronExpression getParent() {
		return month;
	}

	public String toCronString() {
		return this.cron.toString();
	}

	public String toString() {
		return CRON.toCronString(this);
	}

}
