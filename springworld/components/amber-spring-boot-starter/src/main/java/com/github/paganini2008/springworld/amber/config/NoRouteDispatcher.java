package com.github.paganini2008.springworld.amber.config;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * NoRouteDispatcher
 * 
 * @author Fred Feng
 * @created 2018-03
 */
public class NoRouteDispatcher implements JobDispatcher {

	private static final Logger logger = LoggerFactory.getLogger(NoRouteDispatcher.class);

	@Autowired
	private JobBeanFactory jobBeanFactory;

	public void dispatch(JobParameter parameter) {
		final Object target = jobBeanFactory.getBean(parameter.getJobClass());
		final JobListener listener = target instanceof JobListener ? (JobListener) target : JobListener.DEFAULT;
		listener.beforeExecution(parameter);
		Object result = null;
		try {
			result = MethodUtils.invokeMethod(target, DEFAULT_JOB_INVOCATION, parameter);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			listener.onError(parameter, e);
		} finally {
			listener.afterExecution(parameter, result);
		}
	}

}
