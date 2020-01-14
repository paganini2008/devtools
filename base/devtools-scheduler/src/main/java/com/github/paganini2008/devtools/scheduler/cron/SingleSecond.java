package com.github.paganini2008.devtools.scheduler.cron;

import java.io.Serializable;
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
public class SingleSecond implements OneSecond, Serializable {

	private static final long serialVersionUID = 6264419114715870528L;
	private final TreeMap<Integer, Calendar> siblings;
	private Minute minute;
	private int index;
	private Calendar second;
	private int lastSecond;

	SingleSecond(Minute minute, int second) {
		CalendarAssert.checkSecond(second);
		this.minute = minute;
		siblings = new TreeMap<Integer, Calendar>();
		Calendar calendar = CalendarUtils.setField(minute.getTime(), Calendar.SECOND, second);
		siblings.put(second, calendar);
		this.second = calendar;
		this.lastSecond = second;
	}

	public SingleSecond andSecond(int second) {
		CalendarAssert.checkSecond(second);
		Calendar calendar = CalendarUtils.setField(minute.getTime(), Calendar.SECOND, second);
		siblings.put(second, calendar);
		this.lastSecond = second;
		return this;
	}

	public OneSecond toSecond(int second, int interval) {
		CalendarAssert.checkSecond(second);
		for (int i = lastSecond + interval; i < second; i += interval) {
			andSecond(i);
		}
		return this;
	}

	public Date getTime() {
		return second.getTime();
	}

	public long getTimeInMillis() {
		return second.getTimeInMillis();
	}

	public int getYear() {
		return second.get(Calendar.YEAR);
	}

	public int getMonth() {
		return second.get(Calendar.MONTH);
	}

	public int getDay() {
		return second.get(Calendar.DAY_OF_MONTH);
	}

	public int getHour() {
		return second.get(Calendar.HOUR_OF_DAY);
	}

	public int getMinute() {
		return second.get(Calendar.MINUTE);
	}

	public int getSecond() {
		return second.get(Calendar.SECOND);
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
		second = CollectionUtils.get(siblings.values().iterator(), index++);
		second.set(Calendar.YEAR, minute.getYear());
		second.set(Calendar.MONTH, minute.getMonth());
		second.set(Calendar.DAY_OF_MONTH, minute.getDay());
		second.set(Calendar.HOUR_OF_DAY, minute.getHour());
		second.set(Calendar.MINUTE, minute.getMinute());
		return this;
	}

}
