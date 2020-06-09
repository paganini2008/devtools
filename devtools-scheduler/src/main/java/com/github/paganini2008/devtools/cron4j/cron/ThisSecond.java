package com.github.paganini2008.devtools.cron4j.cron;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;

import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * 
 * ThisSecond
 *
 * @author Fred Feng
 * @version 1.0
 */
public class ThisSecond implements ThatSecond, Serializable {

	private static final long serialVersionUID = 6264419114715870528L;
	private final TreeMap<Integer, Calendar> siblings;
	private Minute minute;
	private int index;
	private Calendar second;
	private int lastSecond;
	private final StringBuilder cron = new StringBuilder();

	ThisSecond(Minute minute, int second) {
		CalendarAssert.checkSecond(second);
		this.minute = minute;
		siblings = new TreeMap<Integer, Calendar>();
		Calendar calendar = CalendarUtils.setField(minute.getTime(), Calendar.SECOND, second);
		siblings.put(second, calendar);
		this.second = calendar;
		this.lastSecond = second;
		this.cron.append(second);
	}

	public ThisSecond andSecond(int second) {
		return andSecond(second, true);
	}

	private ThisSecond andSecond(int second, boolean writeCron) {
		CalendarAssert.checkSecond(second);
		Calendar calendar = CalendarUtils.setField(minute.getTime(), Calendar.SECOND, second);
		siblings.put(second, calendar);
		this.lastSecond = second;
		if (writeCron) {
			this.cron.append(",").append(second);
		}
		return this;
	}

	public ThatSecond toSecond(int second, int interval) {
		CalendarAssert.checkSecond(second);
		if (interval < 0) {
			throw new IllegalArgumentException("Invalid interval: " + interval);
		}
		for (int i = lastSecond + interval; i < second; i += interval) {
			andSecond(i, false);
		}
		if (interval > 1) {
			this.cron.append("/").append(interval);
		} else {
			this.cron.append("-").append(second);
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
	
	public CronExpression getParent() {
		return minute;
	}

	public String toCronString() {
		return this.cron.toString();
	}

}
