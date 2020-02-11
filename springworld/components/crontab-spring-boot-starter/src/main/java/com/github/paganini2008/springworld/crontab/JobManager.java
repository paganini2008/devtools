package com.github.paganini2008.springworld.crontab;

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

	void schedule(Job... jobs);

	boolean hasJob(Job job);

	void pauseJob(Job job);

	void resumeJob(Job job);

	void deleteJob(Job job);

	void runNow();

	int countOfJobs();

	String[] jobNames();

	void close();

}