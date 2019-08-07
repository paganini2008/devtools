package com.github.paganini2008.devtools.scheduler.cron;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * 
 * EveryMinute
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public class EveryMinute implements Minute, Serializable {

	private static final long serialVersionUID = -7939881133025374416L;
	private Hour hour;
	private final Calendar minute;
	private final int fromMinute;
	private final int toMinute;
	private final int interval;
	private boolean state;
	private boolean forward = true;

	EveryMinute(Hour hour, Function<Hour, Integer> from, Function<Hour, Integer> to, int interval) {
		this.hour = hour;
		this.fromMinute = from.apply(hour);
		this.minute = CalendarUtils.setField(hour.getTime(), Calendar.MINUTE, fromMinute);
		CalendarAssert.checkMinute(fromMinute);
		this.interval = interval;
		this.state = true;
		this.toMinute = to.apply(hour);
		CalendarAssert.checkMinute(toMinute);
	}

	public boolean hasNext() {
		boolean next = state || minute.get(Calendar.MINUTE) + interval <= toMinute;
		if (!next) {
			if (hour.hasNext()) {
				hour = hour.next();
				minute.set(Calendar.YEAR, hour.getYear());
				minute.set(Calendar.MONTH, hour.getMonth());
				minute.set(Calendar.DAY_OF_MONTH, hour.getDay());
				minute.set(Calendar.HOUR_OF_DAY, hour.getHour());
				minute.set(Calendar.MINUTE, fromMinute);
				forward = false;
				next = true;
			}
		}
		return next;
	}

	public Minute next() {
		if (state) {
			state = false;
		} else {
			if (forward) {
				minute.add(Calendar.MINUTE, interval);
			} else {
				forward = true;
			}
		}
		return this;
	}

	public int getYear() {
		return minute.get(Calendar.YEAR);
	}

	public int getMonth() {
		return minute.get(Calendar.MONTH);
	}

	public int getDay() {
		return minute.get(Calendar.DAY_OF_MONTH);
	}

	public int getHour() {
		return minute.get(Calendar.HOUR_OF_DAY);
	}

	public int getMinute() {
		return minute.get(Calendar.MINUTE);
	}

	public Date getTime() {
		return minute.getTime();
	}

	public long getTimeInMillis() {
		return minute.getTimeInMillis();
	}

	public ConcreteSecond second(int second) {
		return new SingleSecond(CollectionUtils.getFirst(this), second);
	}

	public Second everySecond(Function<Minute, Integer> from, Function<Minute, Integer> to, int interval) {
		return new EverySecond(CollectionUtils.getFirst(this), from, to, interval);
	}

}