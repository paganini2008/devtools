package com.github.paganini2008.springworld.scheduler;

/**
 * S
 * JobDetail
 * 
 * @author Fred Feng
 * @created 2019-11
 * @version 1.0
 */
public interface JobDetail {

	void setRetries(int retries);

	boolean isRunning();

	void pause();

	void resume();

	String getJobName();

	long getLastExecuted();

	long getSuccessCount();

	long getFailedCount();

}