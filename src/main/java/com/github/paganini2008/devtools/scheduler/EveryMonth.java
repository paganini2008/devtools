package com.github.paganini2008.devtools.scheduler;

import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.date.DateUtils;

/**
 * 
 * EveryMonth
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public class EveryMonth implements Month {

	private Year year;
	private final Calendar month;
	private final int fromMonth;
	private final int toMonth;
	private final int interval;
	private boolean state;
	private boolean flag = true;

	EveryMonth(Year year, Function<Year, Integer> from, Function<Year, Integer> to, int interval) {
		this.year = year;
		this.fromMonth = from.apply(year);
		this.month = CalendarUtils.setField(year.getTime(), Calendar.MONTH, fromMonth);
		this.toMonth = to.apply(year);
		this.interval = interval;
		this.state = true;
	}

	public boolean hasNext() {
		boolean next = state || month.get(Calendar.MONTH) + interval <= toMonth;
		if (!next) {
			if (year.hasNext()) {
				year = year.next();
				month.set(Calendar.YEAR, year.getYear());
				month.set(Calendar.MONTH, fromMonth);
				flag = false;
				next = true;
			}
		}
		return next;
	}

	public Month next() {
		if (state) {
			state = false;
		} else {
			if (flag) {
				month.add(Calendar.MONTH, interval);
			} else {
				flag = true;
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

	public int getLasyDay() {
		return month.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	public Date getTime() {
		return month.getTime();
	}

	public long getTimeInMillis() {
		return month.getTimeInMillis();
	}

	public ConcreteDay day(int day) {
		return new SingleDay(CollectionUtils.getFirst(this), day);
	}

	public Day everyDay(Function<Month, Integer> from, Function<Month, Integer> to, int interval) {
		return new EveryDay(CollectionUtils.getFirst(this), from, to, interval);
	}

	public static void main(String[] args) {
		Year everyYear = new EveryYear(2019, 2030, 3);
		Month everyMonth = everyYear.everyMonth(5, 10, 2);
		while (everyMonth.hasNext()) {
			Month month = everyMonth.next();
			System.out.println(DateUtils.format(month.getTime()));
		}
	}

}
