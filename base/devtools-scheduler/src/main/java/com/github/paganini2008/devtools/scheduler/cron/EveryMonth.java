package com.github.paganini2008.devtools.scheduler.cron;

import java.io.Serializable;
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

	public int getLasyDay() {
		return month.getActualMaximum(Calendar.DAY_OF_MONTH);
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

	public ConcreteDay day(int day) {
		return new SingleDay(CollectionUtils.getFirst(this), day);
	}

	public Day lastDay() {
		return new LastDay(CollectionUtils.getFirst(this));
	}

	public Day everyDay(Function<Month, Integer> from, Function<Month, Integer> to, int interval) {
		return new EveryDay(CollectionUtils.getFirst(this), from, to, interval);
	}

	public ConcreteWeek week(int week) {
		return new SingleWeek(CollectionUtils.getFirst(this), week);
	}

	public Week everyWeek(Function<Month, Integer> from, Function<Month, Integer> to, int interval) {
		return new EveryWeek(CollectionUtils.getFirst(this), from, to, interval);
	}

	public static void main(String[] args) {
		Second every = CronBuilder.thisYear().Aug().lastDay().everyHour(2).minute(20).andMinute(30).second(5).toSecond(10);
		while (every.hasNext()) {
			Second time = every.next();
			System.out.println(DateUtils.format(time.getTime()));
		}
	}

}
