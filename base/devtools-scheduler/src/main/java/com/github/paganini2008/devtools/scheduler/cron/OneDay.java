package com.github.paganini2008.devtools.scheduler.cron;

/**
 * 
 * OneDay
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public interface OneDay extends Day {

	OneDay andDay(int day);

	default OneDay andNextDay() {
		return andNextDays(1);
	}

	OneDay andNextDays(int days);

	default OneDay toDay(int day) {
		return toDay(day, 1);
	}

	OneDay toDay(int day, int interval);

}
