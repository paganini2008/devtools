package com.github.paganini2008.devtools.cron4j.parser;

import com.github.paganini2008.devtools.cron4j.cron.CronExpression;

/**
 * 
 * CronOption
 *
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public interface CronOption {

	CronExpression join(CronExpression cronExpression);
	
}
