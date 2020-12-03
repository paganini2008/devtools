package com.github.paganini2008.devtools.cron4j.cron;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * 
 * EveryYear
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class EveryYear implements Year, Serializable {

	private static final long serialVersionUID = 1487831872493410360L;

	public EveryYear(int fromYear, Function<Year, Integer> to, int interval) {
		CalendarAssert.checkYear(fromYear);
		if (interval <= 0) {
			throw new IllegalArgumentException("Invalid interval: " + interval);
		}
		this.year = CalendarUtils.setField(new Date(), Calendar.YEAR, fromYear);
		this.interval = interval;
		this.self = true;
		this.toYear = to.apply(this);
	}

	private final Calendar year;
	private final int toYear;
	private final int interval;
	private boolean self;

	public int getYear() {
		return year.get(Calendar.YEAR);
	}

	public int getWeekCount() {
		return year.getActualMaximum(Calendar.WEEK_OF_YEAR);
	}

	public int getLastDay() {
		return year.getActualMaximum(Calendar.DAY_OF_YEAR);
	}

	public Date getTime() {
		return year.getTime();
	}

	public long getTimeInMillis() {
		return year.getTimeInMillis();
	}

	public boolean hasNext() {
		return self || year.get(Calendar.YEAR) + interval <= toYear;
	}

	public Year next() {
		if (self) {
			self = false;
		} else {
			year.add(Calendar.YEAR, interval);
		}
		return this;
	}

	public Month everyMonth(Function<Year, Integer> from, Function<Year, Integer> to, int interval) {
		final Year copy = (Year) this.copy();
		return new EveryMonth(CollectionUtils.getFirst(copy), from, to, interval);
	}

	public TheDay day(int day) {
		final Year copy = (Year) this.copy();
		return new ThisDayOfYear(CollectionUtils.getFirst(copy), day);
	}

	public TheWeek week(int week) {
		final Year copy = (Year) this.copy();
		return new ThisWeekOfYear(CollectionUtils.getFirst(copy), week);
	}

	public TheMonth month(int month) {
		final Year copy = (Year) this.copy();
		return new ThisMonth(CollectionUtils.getFirst(copy), month);
	}

	public Week lastWeek() {
		final Year copy = (Year) this.copy();
		return new LastWeekOfYear(CollectionUtils.getFirst(copy));
	}

	public CronExpression getParent() {
		return null;
	}

	public String toCronString() {
		return interval > 1 ? "*/" + interval : "";
	}
}
