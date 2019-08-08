package com.github.paganini2008.springworld.crontab;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.paganini2008.devtools.Observable;
import com.github.paganini2008.devtools.scheduler.GenericTaskExecutor;
import com.github.paganini2008.devtools.scheduler.TaskExecutor;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * MemoryJobManager
 *
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
@Slf4j
public class MemoryJobManager implements JobManager {

	private final Observable observable = Observable.unrepeatable();
	private final Map<String, Job> store = new ConcurrentHashMap<String, Job>();
	private TaskExecutor taskExecutor = new GenericTaskExecutor(8, "crontab");

	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	public void schedule(final Job job) {
		if (!store.containsValue(job)) {
			observable.addObserver((ob, arg) -> {
				taskExecutor.schedule(job, job.cron());
				log.info("Start to run job: " + job.name());
			});
			store.put(job.name(), job);
			log.info("Schedule job: " + job.getClass().getName() + ", current job size: " + countOfJobs());
		}
	}
	
	public boolean hasJob(Job job) {
		return store.containsValue(job);
	}

	public void runNow() {
		observable.notifyObservers();
		log.info("Run all jobs now.");
	}

	public int countOfJobs() {
		return store.size();
	}
	
	public String[] jobNames() {
		return store.keySet().toArray(new String[0]);
	}

	public void close() {
		if (taskExecutor != null) {
			taskExecutor.close();
		}
	}
}
