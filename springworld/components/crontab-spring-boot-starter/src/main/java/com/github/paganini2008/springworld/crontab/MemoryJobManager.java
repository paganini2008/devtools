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
	private final Map<Job, TaskExecutor.TaskFuture> taskFutures = new ConcurrentHashMap<Job, TaskExecutor.TaskFuture>();
	private final TaskExecutor taskExecutor;

	public MemoryJobManager() {
		this(8);
	}

	public MemoryJobManager(int nThreads) {
		taskExecutor = new GenericTaskExecutor(nThreads, "crontab");
		taskExecutor.setTaskInterceptorHandler(this);
	}

	public void schedule(final Job job) {
		checkJobNameIfBlank(job);
		observable.addObserver((ob, arg) -> {
			if (!hasScheduled(job)) {
				taskFutures.put(job, taskExecutor.schedule(job, job.getCronExpression()));
				log.info("Schedule job '" + job.getName() + "' ok. Currently job's size is " + countOfJobs());
			}
		});
	}

	public void unscheduleJob(Job job) {
		if (hasScheduled(job)) {
			taskExecutor.removeSchedule(job);
			taskFutures.remove(job);
		}
	}

	public void pauseJob(Job job) {
		if (hasScheduled(job)) {
			taskFutures.get(job).pause();
		}
	}

	public void resumeJob(Job job) {
		if (hasScheduled(job)) {
			taskFutures.get(job).resume();
		}
	}

	public boolean hasScheduled(Job job) {
		return taskFutures.containsKey(job);
	}

	public void runNow() {
		observable.notifyObservers();
		log.info("Run all jobs now.");
	}

	public int countOfJobs() {
		return taskFutures.size();
	}

	public String[] jobNames() {
		List<String> names = new ArrayList<String>(taskFutures.size());
		for (Job job : taskFutures.keySet()) {
			names.add(job.getName());
		}
		return names.toArray(new String[0]);
	}

	private static void checkJobNameIfBlank(Job job) {
		if (StringUtils.isBlank(job.getName())) {
			throw new SchedulingException("Job name is not blank for class: " + job.getClass().getName());
		}
	}

	public void close() {
		if (taskExecutor != null) {
			taskExecutor.close();
		}
	}
}
