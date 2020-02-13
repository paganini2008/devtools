package com.github.paganini2008.devtools.scheduler.cron;

import java.util.Iterator;
import java.util.function.Function;

/**
 * 
 * Hour
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public interface Hour extends Iterator<Hour>, CronExpression {

	int getYear();

	int getMonth();

	int getDay();

	int getHour();

	default Minute everyMinute(int interval) {
		return everyMinute(0, 59, interval);
	}

	default Minute everyMinute(int from, int to, int interval) {
		return everyMinute(h -> from, h -> to, interval);
	}
	
	OneMinute minute(int minute);

	Minute everyMinute(Function<Hour, Integer> from, Function<Hour, Integer> to, int interval);

}
