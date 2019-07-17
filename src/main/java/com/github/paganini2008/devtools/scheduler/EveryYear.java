package com.github.paganini2008.devtools.scheduler;

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
public class EveryYear implements Year {

	EveryYear(int fromYear, int toYear, int interval) {
		this.year = CalendarUtils.setField(new Date(), Calendar.YEAR, fromYear);
		this.toYear = toYear;
		this.interval = interval;
		this.state = true;
	}

	private final Calendar year;
	private final int toYear;
	private final int interval;
	private boolean state;

	public int getYear() {
		return year.get(Calendar.YEAR);
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

	public ConcreteMonth month(int month) {
		return new SingleMonth(CollectionUtils.getFirst(this), month);
	}
	
	public static void main(String[] args) {
		Year everyYear = new EveryYear(2019, 2030, 3);
		while (everyYear.hasNext()) {
			Year time = everyYear.next();
			System.out.println(DateUtils.format(time.getTime()));
		}
	}

}
