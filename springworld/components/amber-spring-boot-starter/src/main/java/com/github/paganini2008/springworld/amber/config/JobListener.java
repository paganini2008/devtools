package com.github.paganini2008.springworld.amber.config;

/**
 * 
 * JobListener
 * 
 * @author Fred Feng
 * @create 2018-03
 */
public interface JobListener {

	void beforeExecution(JobParameter parameter);

	void afterExecution(JobParameter parameter, Object result);

	void onError(JobParameter parameter, Throwable cause);

	final static JobListener DEFAULT = new JobListener() {

		public void onError(JobParameter parameter, Throwable cause) {

		}

		public void beforeExecution(JobParameter parameter) {

		}

		public void afterExecution(JobParameter parameter, Object result) {

		}
	};

}
