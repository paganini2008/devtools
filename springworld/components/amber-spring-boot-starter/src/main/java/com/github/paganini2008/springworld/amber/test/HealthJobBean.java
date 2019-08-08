package com.github.paganini2008.springworld.amber.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.paganini2008.springworld.amber.annotation.CronJob;
import com.github.paganini2008.springworld.amber.config.JobParameter;

/**
 * 
 * HealthJobBean
 * 
 * @author Fred Feng
 * @created 2018-03
 */
@CronJob(cron = "*/5 * * * * ?", description = "Health Checking.")
public class HealthJobBean {

	private static final Logger logger = LoggerFactory.getLogger(HealthJobBean.class);

	public Object execute(JobParameter parameter) {
		if (logger.isDebugEnabled()) {
			logger.debug(parameter.toString());
		}
		return parameter;
	}

}
