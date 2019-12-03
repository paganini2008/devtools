package com.github.paganini2008.springworld.scheduler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import com.github.paganini2008.devtools.Observable;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * MemoryJobManager
 * 
 * @author Fred Feng
 * @created 2019-11
 * @version 1.0
 */
@Slf4j
public class MemoryJobManager implements JobManager {

	private final Observable observable = Observable.unrepeatable();
	private final Map<String, Runnable> jobBeanCache = new ConcurrentHashMap<String, Runnable>();
	private final Map<String, ScheduledFuture<?>> jobFutureCache = new ConcurrentHashMap<String, ScheduledFuture<?>>();

	@Autowired
	private ThreadPoolTaskScheduler taskScheduler;

	@Value("${spring.task-scheduler.failedjob.retries:0}")
	private int retries;

	@Override
	public void scheduleJob(final Object bean, String jobName, String description, String cronExpression) {
		if (hasScheduled(jobName)) {
			throw new IllegalStateException("Job: " + jobName + " has scheduled now.");
		}
		observable.addObserver((ob, arg) -> {
			jobBeanCache.put(jobName, new JobBeanProxy(bean, jobName, retries, this));
			jobFutureCache.put(jobName, taskScheduler.schedule(jobBeanCache.get(jobName), new CronTrigger(cronExpression)));
			log.info("Start to schedule job: " + jobName + "/" + bean.getClass().getName());
		});
		log.info("Add job: " + jobName + "/" + bean.getClass().getName() + " to schedule.");
	}

	@Override
	public void unscheduleJob(String jobName) {
		if (hasScheduled(jobName)) {
			Object jobBean = jobBeanCache.remove(jobName);
			ScheduledFuture<?> scheduledFuture = jobFutureCache.remove(jobName);
			if (scheduledFuture != null) {
				scheduledFuture.cancel(false);
			}
			log.info("Remove job: " + jobName + "/" + jobBean.getClass().getName() + " from context.");
		}
	}

	@Override
	public boolean hasScheduled(String jobName) {
		return jobBeanCache.containsKey(jobName) && jobFutureCache.containsKey(jobName);
	}

	@Override
	public void pauseJob(String jobName) {
		if (hasScheduled(jobName)) {
			((JobBeanProxy) jobBeanCache.get(jobName)).pause();
		}
	}

	@Override
	public void resumeJob(String jobName) {
		if (hasScheduled(jobName)) {
			((JobBeanProxy) jobBeanCache.get(jobName)).resume();
		}
	}

	@Override
	public int countOfJobs() {
		return jobBeanCache.size();
	}

	@Override
	public void runNow() {
		observable.notifyObservers();
	}

}
