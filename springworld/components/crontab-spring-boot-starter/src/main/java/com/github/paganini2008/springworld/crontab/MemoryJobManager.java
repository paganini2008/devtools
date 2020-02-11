package com.github.paganini2008.springworld.crontab;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.SchedulingException;

import com.github.paganini2008.devtools.Observable;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.scheduler.GenericTaskExecutor;
import com.github.paganini2008.devtools.scheduler.TaskExecutor;
import com.github.paganini2008.devtools.scheduler.TaskInterceptorHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * MemoryJobManager
 *
 * @author Fred Feng
 * @created 2018-03
 * @revised 2019-08
 * @version 1.0
 */
@Slf4j
public class MemoryJobManager implements JobManager, TaskInterceptorHandler {

	private final Observable observable = Observable.unrepeatable();
	private final Map<Job, TaskExecutor.TaskFuture> store = new ConcurrentHashMap<Job, TaskExecutor.TaskFuture>();
	private final TaskExecutor taskExecutor;

	public MemoryJobManager() {
		this(8);
	}

	public MemoryJobManager(int nThreads) {
		taskExecutor = new GenericTaskExecutor(nThreads, "crontab");
		taskExecutor.setTaskInterceptorHandler(this);
	}

	public void schedule(final Job... jobs) {
		for (Job job : jobs) {
			checkJobNameIfBlank(job);
			if (!store.containsKey(job)) {
				observable.addObserver((ob, arg) -> {
					store.put(job, taskExecutor.schedule(job, job.getCronExpression()));
					log.info("Start to run job: " + job.getName());
				});
				log.info("Schedule job: " + job.getClass().getName() + ", current job size: " + countOfJobs());
			}
		}
	}

	public void deleteJob(Job job) {
		if (hasJob(job)) {
			taskExecutor.removeSchedule(job);
			store.remove(job);
		}
	}

	public void pauseJob(Job job) {
		if (hasJob(job)) {
			store.get(job).pause();
		}
	}

	public void resumeJob(Job job) {
		if (hasJob(job)) {
			store.get(job).resume();
		}
	}

	public boolean hasJob(Job job) {
		return store.containsKey(job);
	}

	public void runNow() {
		observable.notifyObservers();
		log.info("Run all jobs now.");
	}

	public int countOfJobs() {
		return store.size();
	}

	public String[] jobNames() {
		List<String> names = new ArrayList<String>(store.size());
		for (Job job : store.keySet()) {
			names.add(job.getName());
		}
		return names.toArray(new String[0]);
	}

	private void checkJobNameIfBlank(Job job) {
		if (StringUtils.isBlank(job.getName())) {
			throw new SchedulingException("Job name is blank for class: " + job.getClass().getName());
		}
	}

	public void close() {
		if (taskExecutor != null) {
			taskExecutor.close();
		}
	}
}
