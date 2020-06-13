package com.github.paganini2008.devtools.cron4j.cron;

/**
 * 
 * TheDayOfWeekInMonth
 *
 * @author Fred Feng
 *
 * @since 1.0
 */
public interface TheDayOfWeekInMonth extends Day {

	TheDayOfWeekInMonth and(int week, int dayOfWeek);

	TheDayOfWeekInMonth andLast(int datOfWeek);
}
