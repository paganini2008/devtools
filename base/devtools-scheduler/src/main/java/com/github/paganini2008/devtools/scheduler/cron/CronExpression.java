package com.github.paganini2008.devtools.scheduler.cron;

import java.util.Date;

/**
 * 
 * CronExpression
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-07
 * @version 1.0
 */
public interface CronExpression {

	Date getTime();

	long getTimeInMillis();
}
