package com.github.paganini2008.devtools.cron4j.cron;

/**
 * 
 * CronStringBuilder
 *
 * @author Fred Feng
 *
 * @since 1.0
 */
public interface CronStringBuilder {

	CronExpression getParent();

	String toCronString();

}
