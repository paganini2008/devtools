package com.github.paganini2008.springworld.scheduler;

/**
 * 
 * JobManager
 * 
 * @author Fred Feng
 * @created 2019-11
 * @revised 2019-12
 * @version 1.0
 */
public interface JobManager {

	void scheduleJob(Object jobBean, String jobBeanName, String description, String cronExpression) throws Exception;

	void unscheduleJob(String jobBeanName) throws Exception;

	void pauseJob(String jobBeanName) throws Exception;

	void resumeJob(String jobBeanName) throws Exception;

	boolean hasScheduled(String jobBeanName) throws Exception;

	int countOfJobs() throws Exception;

	void runNow();

}
