package com.github.paganini2008.devtools.scheduler;

import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * 
 * EveryWeek
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public class EveryWeek implements Week {

	private Month month;
	private final Calendar week;
	private final int fromWeek;
	private final int toWeek;
	private final int interval;
	private boolean state;
	private boolean flag = true;

	EveryWeek(Month month, Function<Month, Integer> from, Function<Month, Integer> to, int interval) {
		this.month = month;
		this.fromWeek = from.apply(month);
		this.week = CalendarUtils.setField(month.getTime(), Calendar.WEEK_OF_MONTH, fromWeek);
		this.toWeek = to.apply(month);
		this.interval = interval;
		this.state = true;
	}

	public boolean hasNext() {
		boolean next = week.get(Calendar.WEEK_OF_MONTH) + interval <= toWeek;
		if (!next) {
			if (month.hasNext()) {
				month = month.next();
				week.set(Calendar.YEAR, month.getYear());
				week.set(Calendar.MONTH, month.getMonth());
				week.set(Calendar.WEEK_OF_MONTH, fromWeek);
				flag = false;
				next = true;
			}
		}
		return next;
	}

	public Week next() {
		if (state) {
			state = false;
		} else {
			if (flag) {
				week.add(Calendar.WEEK_OF_MONTH, interval);
			} else {
				flag = true;
			}
		}
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

	public ConcreteDay weekday(int day) {
		return new WeekDay(CollectionUtils.getFirst(this), day);
	}

	public Day everyDay(Function<Week, Integer> from, Function<Week, Integer> to, int interval) {
		return new EveryWeekDay(CollectionUtils.getFirst(this), from, to, interval);
	}

}
