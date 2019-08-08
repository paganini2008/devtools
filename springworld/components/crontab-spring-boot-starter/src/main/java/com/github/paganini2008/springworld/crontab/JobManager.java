package com.github.paganini2008.springworld.crontab;

import com.github.paganini2008.devtools.scheduler.TaskExecutor;

/**
 * 
 * JobManager
 *
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
public interface JobManager {

	void setTaskExecutor(TaskExecutor taskExecutor);

	void schedule(Job job);
	
	boolean hasJob(Job job);

	void runNow();
	
	int countOfJobs();
	
	String[] jobNames();
	
	void close();

}