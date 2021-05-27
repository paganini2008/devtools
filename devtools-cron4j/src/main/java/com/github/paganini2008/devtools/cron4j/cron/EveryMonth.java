package com.github.paganini2008.devtools.cron4j.cron;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.cron4j.CRON;

/**
 * 
 * EveryMonth
 *
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public class EveryMonth implements Month, Serializable {

	private static final long serialVersionUID = -7085376125910878673L;
	private Year year;
	private final Calendar month;
	private final int fromMonth;
	private final int toMonth;
	private final int interval;
	private boolean self;
	private boolean forward = true;

	EveryMonth(Year year, Function<Year, Integer> from, Function<Year, Integer> to, int interval) {
		if (interval <= 0) {
			throw new IllegalArgumentException("Invalid interval: " + interval);
		}
		this.year = year;
		this.fromMonth = from.apply(year);
		CalendarAssert.checkMonth(fromMonth);
		this.month = CalendarUtils.setField(year.getTime(), Calendar.MONTH, fromMonth);
		this.interval = interval;
		this.self = true;
		this.toMonth = to.apply(year);
		CalendarAssert.checkMonth(toMonth);
	}

	public boolean hasNext() {
		boolean next = self || month.get(Calendar.MONTH) + interval <= toMonth;
		if (!next) {
			if (year.hasNext()) {
				year = year.next();
				month.set(Calendar.YEAR, year.getYear());
				month.set(Calendar.MONTH, fromMonth);
				forward = false;
				next = true;
			}
		}
		return next;
	}

	public Month next() {
		if (self) {
			self = false;
		} else {
			if (forward) {
				month.add(Calendar.MONTH, interval);
			} else {
				forward = true;
			}
		}
		return this;
	}

	public int getYear() {
		return month.get(Calendar.YEAR);
	}

	public int getMonth() {
		return month.get(Calendar.MONTH);
	}

	public int getLastDay() {
		return month.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	public int getWeekday(int dayOfMonth) {
		Calendar c = (Calendar) month.clone();
		c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		while (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
			if (dayOfWeek == Calendar.SATURDAY) {
				c.set(Calendar.DAY_OF_WEEK, dayOfWeek - 1);
			} else if (dayOfWeek == Calendar.SUNDAY) {
				c.set(Calendar.DAY_OF_WEEK, dayOfWeek + 1);
			}
			dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		}
		return c.get(Calendar.DAY_OF_MONTH);
	}

	public int getWeekCount() {
		return month.getActualMaximum(Calendar.WEEK_OF_MONTH);
	}

	public Date getTime() {
		return month.getTime();
	}

	public long getTimeInMillis() {
		return month.getTimeInMillis();
	}

	public TheDay day(int day) {
		final Month copy = (Month) this.copy();
		return new ThisDay(CollectionUtils.getFirst(copy), day);
	}

	public Day lastDay() {
		final Month copy = (Month) this.copy();
		return new LastDayOfMonth(CollectionUtils.getFirst(copy));
	}

	public Day everyDay(Function<Month, Integer> from, Function<Month, Integer> to, int interval) {
		final Month copy = (Month) this.copy();
		return new EveryDay(CollectionUtils.getFirst(copy), from, to, interval);
	}

	public TheWeek week(int week) {
		final Month copy = (Month) this.copy();
		return new ThisWeek(CollectionUtils.getFirst(copy), week);
	}

	public TheDayOfWeekInMonth dayOfWeek(int week, int dayOfWeek) {
		final Month copy = (Month) this.copy();
		return new ThisDayOfWeekInMonth(CollectionUtils.getFirst(copy), week, dayOfWeek);
	}

	public Week lastWeek() {
		final Month copy = (Month) this.copy();
		return new LastWeekOfMonth(CollectionUtils.getFirst(copy));
	}

	public Week everyWeek(Function<Month, Integer> from, Function<Month, Integer> to, int interval) {
		final Month copy = (Month) this.copy();
		return new EveryWeek(CollectionUtils.getFirst(copy), from, to, interval);
	}

	public CronExpression getParent() {
		return year;
	}

	public String toCronString() {
		String s = toMonth != 12 ? fromMonth + "-" + toMonth : fromMonth + "";
		return interval > 1 ? s + "/" + interval : "*";
	}

	public String toString() {
		return CRON.toCronString(this);
	}

}
