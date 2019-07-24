package com.github.paganini2008.devtools.scheduler.cron;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.date.DateUtils;

/**
 * 
 * EveryYear
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public class EveryYear implements Year, Serializable {

	private static final long serialVersionUID = 1487831872493410360L;

	EveryYear(int fromYear, Function<Year, Integer> to, int interval) {
		CalendarAssert.checkYear(fromYear);
		this.year = CalendarUtils.setField(new Date(), Calendar.YEAR, fromYear);
		this.interval = interval;
		this.state = true;
		this.toYear = to.apply(this);
	}

	private final Calendar year;
	private final int toYear;
	private final int interval;
	private boolean state;

	public int getYear() {
		return year.get(Calendar.YEAR);
	}

	public int getWeekCount() {
		return year.getActualMaximum(Calendar.WEEK_OF_YEAR);
	}

	public Date getTime() {
		return year.getTime();
	}

	public long getTimeInMillis() {
		return year.getTimeInMillis();
	}

	public boolean hasNext() {
		return state || year.get(Calendar.YEAR) + interval <= toYear;
	}

	public Year next() {
		if (state) {
			state = false;
		} else {
			year.add(Calendar.YEAR, interval);
		}
		return this;
	}

	public Month everyMonth(Function<Year, Integer> from, Function<Year, Integer> to, int interval) {
		return new EveryMonth(CollectionUtils.getFirst(this), from, to, interval);
	}

	public ConcreteWeek week(int week) {
		return new SingleWeekOfYear(CollectionUtils.getFirst(this), week);
	}

	public ConcreteMonth month(int month) {
		return new SingleMonth(CollectionUtils.getFirst(this), month);
	}

	public static void main(String[] args) {
		Day every = CronBuilder.thisYear().andNextYears(2).week(41).everyDay();
		while (every.hasNext()) {
			Day time = every.next();
			System.out.println(DateUtils.format(time.getTime()));
		}
	}

}
