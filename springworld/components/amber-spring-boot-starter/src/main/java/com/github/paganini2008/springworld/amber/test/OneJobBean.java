package com.github.paganini2008.springworld.amber.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.paganini2008.springworld.amber.config.JobParameter;

/**
 * 
 * OneJobBean
 * 
 * @author Fred Feng
 * @create 2018-03
 */
public class OneJobBean {

	private static final Logger logger = LoggerFactory.getLogger(OneJobBean.class);

	public Object execute(JobParameter parameter) {
		if (logger.isInfoEnabled()) {
			logger.info(parameter.toString());
		}
		return parameter;
	}

}
