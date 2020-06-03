package com.github.paganini2008.devtools.cron4j.cron;

import java.util.Iterator;

/**
 * 
 * Second
 *
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public interface Second extends Iterator<Second>, CronExpression {

	int getYear();

	int getMonth();

	int getDay();

	int getHour();

	int getMinute();

	int getSecond();

}
