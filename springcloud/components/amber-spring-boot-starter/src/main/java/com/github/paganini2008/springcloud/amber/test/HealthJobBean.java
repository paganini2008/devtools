package com.github.paganini2008.springcloud.amber.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.paganini2008.springcloud.amber.annotation.CronJob;
import com.github.paganini2008.springcloud.amber.config.JobParameter;

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
