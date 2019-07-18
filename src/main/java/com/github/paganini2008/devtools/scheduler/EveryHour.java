package com.github.paganini2008.devtools.scheduler;

import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;

import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * 
 * EveryHour
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public class EveryHour implements Hour {

	private Day day;
	private final Calendar hour;
	private final int fromHour;
	private final int toHour;
	private final int interval;
	private boolean state;
	private boolean flag = true;

	EveryHour(Day day, Function<Day, Integer> from, Function<Day, Integer> to, int interval) {
		this.day = day;
		this.fromHour = from.apply(day);
		this.hour = CalendarUtils.setField(day.getTime(), Calendar.HOUR_OF_DAY, fromHour);
		this.toHour = to.apply(day);
		this.interval = interval;
		this.state = true;
	}

	public boolean hasNext() {
		boolean next = state || hour.get(Calendar.HOUR_OF_DAY) + interval <= toHour;
		if (!next) {
			if (day.hasNext()) {
				day = day.next();
				hour.set(Calendar.YEAR, day.getYear());
				hour.set(Calendar.MONTH, day.getMonth());
				hour.set(Calendar.DAY_OF_MONTH, day.getDay());
				hour.set(Calendar.HOUR_OF_DAY, fromHour);
				flag = false;
				next = true;
			}
		}
		return next;
	}

	public Hour next() {
		if (state) {
			state = false;
		} else {
			if (flag) {
				hour.add(Calendar.HOUR_OF_DAY, interval);
			} else {
				flag = true;
			}
		}
		return this;
	}

	public int getYear() {
		return hour.get(Calendar.YEAR);
	}

	public int getMonth() {
		return hour.get(Calendar.MONTH);
	}

	public int getDay() {
		return hour.get(Calendar.DAY_OF_MONTH);
	}

	public int getHour() {
		return hour.get(Calendar.HOUR_OF_DAY);
	}

	public Date getTime() {
		return hour.getTime();
	}

	public long getTimeInMillis() {
		return hour.getTimeInMillis();
	}

	public ConcreteMinute minute(int minute) {
		return new SingleMinute(CollectionUtils.getFirst(this), minute);
	}

	public Minute everyMinute(Function<Hour, Integer> from, Function<Hour, Integer> to, int interval) {
		return new EveryMinute(CollectionUtils.getFirst(this), from, to, interval);
	}

}
