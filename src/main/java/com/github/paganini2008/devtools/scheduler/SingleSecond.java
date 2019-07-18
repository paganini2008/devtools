package com.github.paganini2008.devtools.scheduler;

import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;

import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * 
 * SingleSecond
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public class SingleSecond implements ConcreteSecond {

	private final TreeMap<Integer, Calendar> siblings;
	private Minute minute;
	private int index;
	private Calendar calendar;

	SingleSecond(Minute minute, int second) {
		this.minute = minute;
		siblings = new TreeMap<Integer, Calendar>();
		Calendar calendar = CalendarUtils.setField(minute.getTime(), Calendar.SECOND, second);
		siblings.put(second, calendar);
	}

	public SingleSecond andSecond(int second) {
		Calendar calendar = CalendarUtils.setField(minute.getTime(), Calendar.SECOND, second);
		siblings.put(second, calendar);
		return this;
	}

	public Date getTime() {
		return calendar.getTime();
	}

	public long getTimeInMillis() {
		return calendar.getTimeInMillis();
	}

	public int getYear() {
		return calendar.get(Calendar.YEAR);
	}

	public int getMonth() {
		return calendar.get(Calendar.MONTH);
	}

	public int getDay() {
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	public int getHour() {
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	public int getMinute() {
		return calendar.get(Calendar.MINUTE);
	}

	public int getSecond() {
		return calendar.get(Calendar.SECOND);
	}

	public boolean hasNext() {
		boolean next = index < siblings.size();
		if (!next) {
			if (minute.hasNext()) {
				minute = minute.next();
				index = 0;
				next = true;
			}
		}
		return next;
	}

	public Second next() {
		calendar = CollectionUtils.get(siblings.values().iterator(), index++);
		calendar.set(Calendar.YEAR, minute.getYear());
		calendar.set(Calendar.MONTH, minute.getMonth());
		calendar.set(Calendar.DAY_OF_MONTH, minute.getDay());
		calendar.set(Calendar.HOUR_OF_DAY, minute.getHour());
		calendar.set(Calendar.MINUTE, minute.getMinute());
		return this;
	}

}
