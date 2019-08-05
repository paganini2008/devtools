package com.github.paganini2008.devtools.scheduler.cron;

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
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public class EveryDay implements Day, Serializable {

	private static final long serialVersionUID = -2114922383566430661L;
	private Month month;
	private final Calendar day;
	private final int fromDay;
	private final int toDay;
	private final int interval;
	private boolean state;
	private boolean forward = true;

	EveryDay(Month month, Function<Month, Integer> from, Function<Month, Integer> to, int interval) {
		this.month = month;
		this.fromDay = from.apply(month);
		CalendarAssert.checkDayOfMonth(month, fromDay);
		this.day = CalendarUtils.setField(month.getTime(), Calendar.DAY_OF_MONTH, fromDay);
		this.interval = interval;
		this.state = true;
		this.toDay = to.apply(month);
		CalendarAssert.checkDayOfMonth(month, toDay);
	}

	public boolean hasNext() {
		boolean next = state || day.get(Calendar.DAY_OF_MONTH) + interval <= toDay;
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
		if (state) {
			state = false;
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

	public int getWeekDay() {
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

	public ConcreteHour hour(int hour) {
		return new SingleHour(CollectionUtils.getFirst(this), hour);
	}

	public Hour everyHour(Function<Day, Integer> from, Function<Day, Integer> to, int interval) {
		return new EveryHour(CollectionUtils.getFirst(this), from, to, interval);
	}

	public static void main(String[] args) {
		Day everyDay = CronBuilder.everyYear(2019, 2030, 3).everyMonth(5, 10, 2).everyDay(1, 15, 3);
		while (everyDay.hasNext()) {
			Day day = everyDay.next();
			System.out.println(DateUtils.format(day.getTime()));
		}
	}

}
