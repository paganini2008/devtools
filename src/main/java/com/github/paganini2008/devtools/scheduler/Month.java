package com.github.paganini2008.devtools.scheduler;

import java.util.Iterator;
import java.util.function.Function;

/**
 * 
 * Month
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public interface Month extends Iterator<Month>, CurrentTime {

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

	ConcreteDay day(int day);

	default ConcreteDay firstDay() {
		return day(1);
	}

	default ConcreteDay lastDay() {
		return day(getLasyDay());
	}

	Day everyDay(Function<Month, Integer> from, Function<Month, Integer> to, int interval);

	ConcreteWeek week(int week);

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