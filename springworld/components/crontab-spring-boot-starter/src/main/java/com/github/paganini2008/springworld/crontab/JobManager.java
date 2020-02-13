package com.github.paganini2008.springworld.crontab;

import java.util.Date;

import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

/**
 * 
 * JobManager
 *
 * @author Fred Feng
 * @created 2018-03
 * @revised 2019-08
 * @version 1.0
 */
public interface JobManager {

	void schedule(Job job);

	boolean hasScheduled(Job job);

	void pauseJob(Job job);

	void resumeJob(Job job);

	void unscheduleJob(Job job);

	void runNow();

	int countOfScheduling();

	Date getStartDate();
	
	void close();
	
	ResultSetSlice<JobInfo> getJobInfos();

}