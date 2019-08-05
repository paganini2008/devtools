package com.allyes.components.amber.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.allyes.components.amber.annotation.CronJob;
import com.allyes.components.amber.config.JobParameter;

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
