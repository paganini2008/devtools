package com.github.paganini2008.devtools.date;

import java.util.Date;
import java.util.Iterator;

/**
 * DateIterator
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class DateIterator implements Iterator<Date> {

	private final DateTime spot;
	private final Date endDate;
	private final int amount;
	private final Step step;
	private final boolean asc;

	public DateIterator(Date endDate, int amount, Step step, boolean asc) {
		this(new Date(), endDate, amount, step, asc);
	}

	public DateIterator(Date startDate, Date endDate, int amount, Step step, boolean asc) {
		this(new DateTime(startDate), endDate, amount, step, asc);
	}

	public DateIterator(DateTime spot, Date endDate, int amount, Step step, boolean asc) {
		this.spot = spot;
		this.endDate = endDate;
		this.amount = amount;
		this.step = step;
		this.asc = asc;
	}

	public boolean hasNext() {
		step.touch(spot, amount, asc);
		return asc ? spot.before(endDate) : spot.after(endDate);
	}

	public Date next() {
		return spot.getTime();
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

}
