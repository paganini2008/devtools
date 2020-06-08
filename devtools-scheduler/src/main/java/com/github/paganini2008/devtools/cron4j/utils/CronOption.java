package com.github.paganini2008.devtools.cron4j.utils;

import com.github.paganini2008.devtools.cron4j.cron.CronExpression;

/**
 * 
 * CronOption
 *
 * @author Fred Feng
 *
 * @since 1.0
 */
public interface CronOption {

	CronExpression join(CronExpression cronExpression);
	
}
