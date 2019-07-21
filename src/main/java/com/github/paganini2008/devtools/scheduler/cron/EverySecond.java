package com.github.paganini2008.devtools.scheduler.cron;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;

import com.github.paganini2008.devtools.date.DateUtils;

/**
 * 
 * EverySecond
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public class EverySecond implements Second, Serializable {

	private static final long serialVersionUID = -2606684197757806223L;
	private Minute minute;
	private final Calendar second;
	private final int fromSecond;
	private final int toSecond;
	private final int interval;
	private boolean state;
	private boolean forward = true;

	EverySecond(Minute minute, Function<Minute, Integer> from, Function<Minute, Integer> to, int interval) {
		this.minute = minute;
		this.fromSecond = from.apply(minute);
		CalendarAssert.checkSecond(fromSecond);
		this.second = CalendarUtils.setField(minute.getTime(), Calendar.SECOND, fromSecond);
		this.interval = interval;
		this.state = true;
		this.toSecond = to.apply(minute);
		CalendarAssert.checkSecond(toSecond);
	}

	public boolean hasNext() {
		boolean next = state || second.get(Calendar.SECOND) + interval <= toSecond;
		if (!next) {
			if (minute.hasNext()) {
				minute = minute.next();
				second.set(Calendar.YEAR, minute.getYear());
				second.set(Calendar.MONTH, minute.getMonth());
				second.set(Calendar.DAY_OF_MONTH, minute.getDay());
				second.set(Calendar.HOUR_OF_DAY, minute.getHour());
				second.set(Calendar.MINUTE, minute.getMinute());
				second.set(Calendar.SECOND, fromSecond);
				forward = false;
				next = true;
			}
		}
		return next;
	}

	public Second next() {
		if (state) {
			state = false;
		} else {
			if (forward) {
				second.add(Calendar.SECOND, interval);
			} else {
				forward = true;
			}
		}
		return this;
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

	public Date getTime() {
		return second.getTime();
	}

	public long getTimeInMillis() {
		return second.getTimeInMillis();
	}

	public static void main(String[] args) {
		Year everyYear = Crons.everyYear(2019, 2021, 1);
		Month everyMonth = everyYear.everyMonth(5, 10, 2);
		Day everyDay = everyMonth.everyDay(1, 15, 3);
		Hour everyHour = everyDay.everyHour(4);
		Minute everyMinute = everyHour.everyMinute(20);
		Second everySecond = everyMinute.everySecond(40, 52, 3);
		while (everySecond.hasNext()) {
			Second second = everySecond.next();
			System.out.println(DateUtils.format(second.getTime()));
		}
	}

}