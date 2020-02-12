package com.github.paganini2008.springworld.crontab;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.SchedulingException;

import com.github.paganini2008.devtools.Observable;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.scheduler.TaskExecutor;
import com.github.paganini2008.devtools.scheduler.TaskExecutor.TaskDetail;
import com.github.paganini2008.devtools.scheduler.TaskExecutor.TaskFuture;
import com.github.paganini2008.devtools.scheduler.TaskInterceptorHandler;
import com.github.paganini2008.devtools.scheduler.ThreadPoolTaskExecutor;

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
	private final Map<String, Job> store = new ConcurrentHashMap<String, Job>();
	private final TaskExecutor taskExecutor;

	public MemoryJobManager() {
		this(8);
	}

	public MemoryJobManager(int nThreads) {
		taskExecutor = new ThreadPoolTaskExecutor(nThreads, "crontab");
		taskExecutor.setTaskInterceptorHandler(this);
	}

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Value("${spring.application.name}")
	private String applicationName;

	public void schedule(final Job job) {
		checkJobNameIfBlank(job);
		observable.addObserver((ob, arg) -> {
			if (!hasScheduled(job)) {
				taskExecutor.schedule(job, job.getCronExpression());
				log.info("Schedule job '" + job.getName() + "' ok. Currently job's size is " + countOfJobs());
			}
		});
	}

	public void unscheduleJob(Job job) {
		if (hasScheduled(job)) {
			taskExecutor.removeSchedule(job);
		}
	}

	public void pauseJob(Job job) {
		if (hasScheduled(job)) {
			taskExecutor.getTaskFuture(job).pause();
		}
	}

	public void resumeJob(Job job) {
		if (hasScheduled(job)) {
			taskExecutor.getTaskFuture(job).resume();
		}
	}

	public boolean hasScheduled(Job job) {
		return taskExecutor.hasScheduled(job);
	}

	public void runNow() {
		observable.notifyObservers();
		log.info("Run all jobs now.");
	}

	public int countOfJobs() {
		return taskExecutor.taskCount();
	}

	public String[] jobNames() {
		return store.keySet().toArray(new String[0]);
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

	public void beforeJobExecution(TaskFuture future) {
		final Job job = (Job) future.getDetail().getTaskObject();
		final TaskDetail taskDetail = future.getDetail();

		JobInfo jobInfo = new JobInfo();
		jobInfo.setJobName(job.getName());
		jobInfo.setDescription(job.getDescription());
		jobInfo.setRunning(taskDetail.isRunning());
		jobInfo.setCompletedCount(taskDetail.completedCount());
		jobInfo.setFailedCount(taskDetail.failedCount());
		jobInfo.setLastExecuted(new Date(taskDetail.lastExecuted()));
		jobInfo.setNextExecuted(new Date(taskDetail.nextExecuted()));

		String key = String.format("crontab:%s:%s", applicationName);
		redisTemplate.opsForHash().put(key, jobInfo.getJobName(), jobInfo);
	}

	public void afterJobExecution(TaskFuture future) {
		beforeJobExecution(future);
	}

}
