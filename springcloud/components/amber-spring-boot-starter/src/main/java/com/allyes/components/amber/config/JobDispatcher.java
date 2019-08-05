package com.allyes.components.amber.config;

/**
 * 
 * JobDispatcher
 * 
 * @author Fred Feng
 * @create 2018-03
 */
public interface JobDispatcher {

	static final String DEFAULT_JOB_INVOCATION = "execute";

	void dispatch(JobParameter jobParameter);

}
