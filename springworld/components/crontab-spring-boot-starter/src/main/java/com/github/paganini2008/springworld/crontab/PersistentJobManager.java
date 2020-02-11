package com.github.paganini2008.springworld.crontab;

/**
 * 
 * PersistentJobManager
 *
 * @author Fred Feng
 * @created 2020-01
 * @revised 2020-02
 * @version 1.0
 */
public interface PersistentJobManager extends JobManager {
	
	void save(Job job);

	void deleteJob(Job job);
	
	boolean hasJob(Job job);
}
