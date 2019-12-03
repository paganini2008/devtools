package com.github.paganini2008.springworld.scheduler;

/**
 * 
 * JobManager
 * 
 * @author Fred Feng
 * @created 2019-11
 * @version 1.0
 */
public interface JobManager {

	void scheduleJob(Object bean, String jobName, String description, String cronExpression) throws Exception;

	void unscheduleJob(String jobName) throws Exception;

	void pauseJob(String jobName) throws Exception;

	void resumeJob(String jobName) throws Exception;

	boolean hasScheduled(String jobName) throws Exception;

	int countOfJobs() throws Exception;

	void runNow();

}
