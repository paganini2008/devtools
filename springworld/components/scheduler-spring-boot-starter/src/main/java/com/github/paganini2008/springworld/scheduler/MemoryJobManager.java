package com.github.paganini2008.springworld.scheduler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import com.github.paganini2008.devtools.Observable;
import com.github.paganini2008.springworld.cluster.utils.ApplicationContextUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * MemoryJobManager
 * 
 * @author Fred Feng
 * @created 2019-11
 * @revised 2019-12
 * @version 1.0
 */
@Slf4j
public class MemoryJobManager implements JobManager {

	private final Observable observable = Observable.unrepeatable();
	private final Map<String, Runnable> jobBeanCache = new ConcurrentHashMap<String, Runnable>();
	private final Map<String, ScheduledFuture<?>> jobFutureCache = new ConcurrentHashMap<String, ScheduledFuture<?>>();

	@Autowired
	private TaskScheduler taskScheduler;

	@Value("${spring.task-scheduler.failedjob.retries:0}")
	private int retries;

	@Value("${spring.task-scheduler.loadbalance.enabled:false}")
	private boolean loadbalanced;

	@Override
	public void scheduleJob(final Object jobBean, String jobBeanName, String description, String cronExpression) {
		if (hasScheduled(jobBeanName)) {
			throw new IllegalStateException("Job: " + jobBeanName + " has scheduled now.");
		}
		observable.addObserver((ob, arg) -> {
			jobBeanCache.put(jobBeanName, ApplicationContextUtils.autowireBean(getJobBean(jobBean, jobBeanName)));
			jobFutureCache.put(jobBeanName, taskScheduler.schedule(jobBeanCache.get(jobBeanName), new CronTrigger(cronExpression)));
			log.info("Start to schedule job: " + jobBeanName + "/" + jobBean.getClass().getName());
		});
		log.info("Add job: " + jobBeanName + "/" + jobBean.getClass().getName() + " to schedule.");
	}

	protected Runnable getJobBean(Object jobBean, String jobBeanName) {
		return loadbalanced ? new LoadBalancedJobBeanProxy(jobBean.getClass(), jobBeanName)
				: new JobBeanProxy(jobBean, jobBeanName, retries);
	}

	@Override
	public void unscheduleJob(String jobBeanName) {
		if (hasScheduled(jobBeanName)) {
			Object jobBean = jobBeanCache.remove(jobBeanName);
			ScheduledFuture<?> scheduledFuture = jobFutureCache.remove(jobBeanName);
			if (scheduledFuture != null) {
				scheduledFuture.cancel(false);
			}
			log.info("Remove job: " + jobBeanName + "/" + jobBean.getClass().getName() + " from context.");
		}
	}

	@Override
	public boolean hasScheduled(String jobBeanName) {
		return jobBeanCache.containsKey(jobBeanName) && jobFutureCache.containsKey(jobBeanName);
	}

	@Override
	public void pauseJob(String jobBeanName) {
		if (hasScheduled(jobBeanName)) {
			((JobRunningControl) jobBeanCache.get(jobBeanName)).pause();
		}
	}

	@Override
	public void resumeJob(String jobBeanName) {
		if (hasScheduled(jobBeanName)) {
			((JobRunningControl) jobBeanCache.get(jobBeanName)).resume();
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
