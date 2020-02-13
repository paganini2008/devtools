package com.github.paganini2008.springworld.crontab;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.paganini2008.devtools.io.FileUtils;
import com.github.paganini2008.devtools.scheduler.cron.CronBuilder;
import com.github.paganini2008.devtools.scheduler.cron.CronExpression;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * HealthCheckJob
 *
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */

@Slf4j
@Component
public class HealthCheckJob implements CronJob {

	@Autowired
	private JobManager jobManager;

	public boolean execute() {
		if (log.isTraceEnabled()) {
			log.trace("Current job count: {}, memory: {}", jobManager.countOfScheduling(), getMemoryUsage());
		}
		return true;
	}

	private String getMemoryUsage() {
		long max = Runtime.getRuntime().maxMemory();
		long used = max - Runtime.getRuntime().freeMemory();
		return FileUtils.formatSize(used) + "/" + FileUtils.formatSize(max);
	}

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

	@Override
	public CronExpression getCronExpression() {
		return CronBuilder.everySecond(5);
	}

}
