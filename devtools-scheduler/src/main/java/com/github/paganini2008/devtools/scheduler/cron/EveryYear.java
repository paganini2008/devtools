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
		return new EveryMonth(CollectionUtils.getFirst(this), from, to, interval);
	}

	public OneDay day(int day) {
		return new SingleDayOfYear(CollectionUtils.getFirst(this), day);
	}

	public OneWeek week(int week) {
		return new SingleWeekOfYear(CollectionUtils.getFirst(this), week);
	}

	public OneMonth month(int month) {
		return new SingleMonth(CollectionUtils.getFirst(this), month);
	}
	
	public Week lastWeek() {
		return new LastWeekOfYear(CollectionUtils.getFirst(this));
	}

	public static void main(String[] args) {
		Day every = CronBuilder.thisYear().toYear(2025).Feb().andAug().toDec().lastDay();
		while (every.hasNext()) {
			Day time = every.next();
			System.out.println(DateUtils.format(time.getTime()));
			//System.out.println(time.getLasyDay());
		}
	}

}
