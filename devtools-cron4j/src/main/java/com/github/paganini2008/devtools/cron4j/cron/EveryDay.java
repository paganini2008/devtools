package com.github.paganini2008.devtools.cron4j.cron;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.date.DateUtils;

/**
 * 
 * EveryDay
 *
 * @author Fred Feng
 * @version 1.0
 */
public class EveryDay implements Day, Serializable {

	private static final long serialVersionUID = -2114922383566430661L;
	private Month month;
	private final Calendar day;
	private final int fromDay;
	private final int toDay;
	private final int interval;
	private boolean self;
	private boolean forward = true;

	EveryDay(Month month, Function<Month, Integer> from, Function<Month, Integer> to, int interval) {
		if (interval <= 0) {
			throw new IllegalArgumentException("Invalid interval: " + interval);
		}
		this.month = month;
		this.fromDay = from.apply(month);
		CalendarAssert.checkDayOfMonth(month, fromDay);
		this.day = CalendarUtils.setField(month.getTime(), Calendar.DAY_OF_MONTH, fromDay);
		this.interval = interval;
		this.self = true;
		this.toDay = to.apply(month);
		CalendarAssert.checkDayOfMonth(month, toDay);
	}

	public boolean hasNext() {
		boolean next = self || day.get(Calendar.DAY_OF_MONTH) + interval <= toDay;
		if (!next) {
			if (month.hasNext()) {
				month = month.next();
				day.set(Calendar.YEAR, month.getYear());
				day.set(Calendar.MONTH, month.getMonth());
				day.set(Calendar.DAY_OF_MONTH, fromDay);
				forward = false;
				next = true;
			}
		}
		return next;
	}

	public Day next() {
		if (self) {
			self = false;
		} else {
			if (forward) {
				day.add(Calendar.DAY_OF_MONTH, interval);
			} else {
				forward = true;
			}
		}
		return this;
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

	public Date getTime() {
		return day.getTime();
	}

	public long getTimeInMillis() {
		return day.getTimeInMillis();
	}

	public TheHour hour(int hour) {
		final Day copy = (Day) this.copy();
		return new ThisHour(CollectionUtils.getFirst(copy), hour);
	}

	public Hour everyHour(Function<Day, Integer> from, Function<Day, Integer> to, int interval) {
		final Day copy = (Day) this.copy();
		return new EveryHour(CollectionUtils.getFirst(copy), from, to, interval);
	}

	public CronExpression getParent() {
		return month;
	}

	public String toCronString() {
		return "*/" + interval;
	}

	public static void main(String[] args) {
		Second time = CronExpressionBuilder.everyYear(2020, 2030, 3).everyMonth(5, 11, 2).everyDay(1, 15, 3).at(12, 0, 0);
		time.forEach(date -> {
			System.out.println(DateUtils.format(date));
		}, 100);
	}

}