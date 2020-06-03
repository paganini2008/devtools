package com.github.paganini2008.devtools.cron4j.cron;

import java.util.Iterator;
import java.util.function.Function;

/**
 * 
 * Month
 *
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public interface Month extends Iterator<Month>, CronExpression {

	int getYear();

	int getMonth();

	int getLasyDay();

	int getWeekCount();

	default Day everyDay() {
		return everyDay(1);
	}

	default Day everyDay(int interval) {
		return everyDay(m -> 1, m -> {
			return m.getLasyDay();
		}, interval);
	}

	default Day everyDay(int from, int to, int interval) {
		return everyDay(m -> from, m -> to, interval);
	}

	OneDay day(int day);

	Day lastDay();

	Day everyDay(Function<Month, Integer> from, Function<Month, Integer> to, int interval);

	OneWeek week(int week);
	
	Week lastWeek();

	default Week everyWeek() {
		return everyWeek(1);
	}

	default Week everyWeek(int interval) {
		return everyWeek(m -> 1, m -> {
			return m.getWeekCount();
		}, interval);
	}

	default Week everyWeek(int from, int to, int interval) {
		return everyWeek(m -> from, m -> to, interval);
	}

	Week everyWeek(Function<Month, Integer> from, Function<Month, Integer> to, int interval);

}
