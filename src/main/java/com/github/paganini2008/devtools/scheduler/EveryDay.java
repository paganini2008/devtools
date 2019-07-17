package com.github.paganini2008.devtools.scheduler;

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
public class EveryDay implements Day {

	private Month month;
	private final Calendar day;
	private final int fromDay;
	private final int toDay;
	private final int interval;
	private boolean state;
	private boolean flag = true;

	EveryDay(Month month, Function<Month, Integer> from, Function<Month, Integer> to, int interval) {
		this.month = month;
		this.fromDay = from.apply(month);
		this.day = CalendarUtils.setField(month.getTime(), Calendar.DAY_OF_MONTH, fromDay);
		this.toDay = to.apply(month);
		this.interval = interval;
		this.state = true;
	}

	public boolean hasNext() {
		boolean next = day.get(Calendar.DAY_OF_MONTH) + interval <= toDay;
		if (!next) {
			if (month.hasNext()) {
				month = month.next();
				day.set(Calendar.YEAR, month.getYear());
				day.set(Calendar.MONTH, month.getMonth());
				day.set(Calendar.DAY_OF_MONTH, fromDay);
				flag = false;
				next = true;
			}
		}
		return next;
	}

	public Day next() {
		if (state) {
			state = false;
		} else {
			if (flag) {
				day.add(Calendar.DAY_OF_MONTH, interval);
			} else {
				flag = true;
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
		Year everyYear = new EveryYear(2019, 2030, 3);
		Month everyMonth = everyYear.everyMonth(5, 10, 2);
		Day everyDay = everyMonth.everyDay(1, 15, 3);
		while (everyDay.hasNext()) {
			Day day = everyDay.next();
			System.out.println(DateUtils.format(day.getTime()));
		}
	}

}
