package com.github.paganini2008.devtools.cron4j.cron;

/**
 * 
 * CronStringBuilder
 *
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public interface CronStringBuilder {

	default boolean supportCronString() {
		return true;
	}

	CronExpression getParent();

	default String toCronString() {
		throw new UnsupportedOperationException();
	}

	String toString();

}
