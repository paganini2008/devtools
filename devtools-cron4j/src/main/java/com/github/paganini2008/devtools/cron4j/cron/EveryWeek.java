package com.github.paganini2008.devtools.cron4j.cron;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.cron4j.CRON;

/**
 * 
 * EveryWeek
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class EveryWeek implements Week, Serializable {

	private static final long serialVersionUID = -6457126115562721511L;
	private Month month;
	private final Calendar week;
	private final int fromWeek;
	private final int toWeek;
	private final int interval;
	private boolean self;
	private boolean forward = true;
	private Date previous;

	EveryWeek(Month month, Function<Month, Integer> from, Function<Month, Integer> to, int interval) {
		if (interval <= 0) {
			throw new IllegalArgumentException("Invalid interval: " + interval);
		}
		this.month = month;
		this.fromWeek = from.apply(month);
		CalendarAssert.checkWeekOfMonth(month, fromWeek);
		Calendar calendar = CalendarUtils.setField(month.getTime(), Calendar.WEEK_OF_MONTH, fromWeek);
		this.week = calendar;
		this.interval = interval;
		this.self = true;
		this.toWeek = to.apply(month);
		CalendarAssert.checkWeekOfMonth(month, toWeek);
	}

	public boolean hasNext() {
		boolean next = self || shoudNext();
		if (!next) {
			if (month.hasNext()) {
				month = month.next();
				week.set(Calendar.YEAR, month.getYear());
				week.set(Calendar.MONTH, month.getMonth());
				week.set(Calendar.WEEK_OF_MONTH, fromWeek);
				forward = previous != null && previous.compareTo(week.getTime()) >= 0;
				next = true;
			}
		}
		return next;
	}

	private boolean shoudNext() {
		if (month.getMonth() == week.get(Calendar.MONTH)) {
			boolean next = (week.get(Calendar.DAY_OF_MONTH) + 7 <= month.getLasyDay());
			next &= (week.get(Calendar.WEEK_OF_MONTH) + interval <= toWeek);
			return next;
		}
		return true;
	}

	public Week next() {
		if (self) {
			self = false;
		} else {
			if (forward) {
				week.add(Calendar.WEEK_OF_MONTH, interval);
			} else {
				forward = true;
			}
		}
		previous = week.getTime();
		return this;
	}

	public int getYear() {
		return week.get(Calendar.YEAR);
	}

	public int getMonth() {
		return week.get(Calendar.MONTH);
	}

	public int getWeek() {
		return week.get(Calendar.WEEK_OF_MONTH);
	}

	public int getWeekOfYear() {
		return week.get(Calendar.WEEK_OF_YEAR);
	}

	public Date getTime() {
		return week.getTime();
	}

	public long getTimeInMillis() {
		return week.getTimeInMillis();
	}

	public TheDayOfWeek day(int day) {
		final Week copy = (Week) this.copy();
		return new ThisDayOfWeek(CollectionUtils.getFirst(copy), day);
	}

	public Day everyDay(Function<Week, Integer> from, Function<Week, Integer> to, int interval) {
		final Week copy = (Week) this.copy();
		return new EveryDayOfWeek(CollectionUtils.getFirst(copy), from, to, interval);
	}

	public CronExpression getParent() {
		return month;
	}

	public String toCronString() {
		return "";
	}

	public String toString() {
		return CRON.toCronString(this);
	}

}
