package com.github.paganini2008.devtools.cron4j.cron;

import java.util.Iterator;
import java.util.function.Function;

/**
 * 
 * Hour
 *
 * @author Jimmy Hoff
 * 
 * 
 * @version 1.0
 */
public interface Hour extends Iterator<Hour>, CronExpression {

	int getYear();

	int getMonth();

	int getDay();

	int getHour();

	default Minute everyMinute() {
		return everyMinute(1);
	}

	default Minute everyMinute(int interval) {
		return everyMinute(0, 59, interval);
	}

	default Minute everyMinute(int from, int to, int interval) {
		return everyMinute(h -> from, h -> to, interval);
	}

	TheMinute minute(int minute);

	default TheSecond at(int minute, int second) {
		return minute(minute).second(second);
	}

	Minute everyMinute(Function<Hour, Integer> from, Function<Hour, Integer> to, int interval);

}
