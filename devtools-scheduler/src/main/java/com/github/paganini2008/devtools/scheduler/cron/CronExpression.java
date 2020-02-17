package com.github.paganini2008.devtools.scheduler.cron;

import java.util.Date;

/**
 * 
 * CronExpression
 *
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public interface CronExpression {

	Date getTime();

	long getTimeInMillis();
}
