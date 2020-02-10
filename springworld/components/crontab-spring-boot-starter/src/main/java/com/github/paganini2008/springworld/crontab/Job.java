package com.github.paganini2008.springworld.crontab;

import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.scheduler.cron.CronExpression;

/**
 * 
 * Job
 * 
 * @author Fred Feng
 * @created 2018-03
 * @revised 2019-08
 * @version 1.0
 */
public interface Job extends Executable {

	default String name() {
		return "";
	}

	CronExpression cron();

	default String description() {
		return "";
	}

}
