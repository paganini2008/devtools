package com.github.paganini2008.springworld.crontab;

import java.util.Date;

import org.springframework.scheduling.SchedulingException;

import com.github.paganini2008.devtools.Observable;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.date.DateUtils;
import com.github.paganini2008.devtools.scheduler.TaskExecutor;
import com.github.paganini2008.devtools.scheduler.TaskInterceptorHandler;
import com.github.paganini2008.devtools.scheduler.ThreadPoolTaskExecutor;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * AbstractJobManager
 *
 * @author Fred Feng
 * @created 2020-01
 * @revised 2020-02
 * @version 1.0
 */
@Slf4j
public abstract class AbstractJobManager implements JobManager, TaskInterceptorHandler {

	private final Observable observable = Observable.unrepeatable();
	protected final Date startDate;
	protected final TaskExecutor taskExecutor;

	protected AbstractJobManager(int nThreads) {
		taskExecutor = new ThreadPoolTaskExecutor(nThreads, "crontab");
		taskExecutor.setTaskInterceptorHandler(this);
		startDate = new Date();
	}

	public void schedule(final Job job) {
		checkJobNameIfBlank(job);
		observable.addObserver((ob, arg) -> {
			if (!hasScheduled(job)) {
				if (job instanceof CronJob) {
					taskExecutor.schedule(job, ((CronJob) job).getCronExpression());
				} else if (job instanceof SimpleJob) {
					SimpleJob simpleJob = (SimpleJob) job;
					long delay = DateUtils.convertToMillis(simpleJob.getDelay(), simpleJob.getDelayTimeUnit());
					long period = DateUtils.convertToMillis(simpleJob.getPeriod(), simpleJob.getPeriodTimeUnit());
					switch (simpleJob.getRunMode()) {
					case FIXED_DELAY:
						taskExecutor.scheduleWithFixedDelay(job, delay, period);
						break;
					case FIXED_RATE:
						taskExecutor.scheduleAtFixedRate(job, delay, period);
						break;
					default:
						throw new UnsupportedOperationException();
					}
				} else {
					throw new SchedulingException("Please define a concrete job for CronJob or SimpleJob.");
				}
				log.info("Schedule job '" + job.getName() + "' ok. Currently scheduling's size is " + countOfScheduling());
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

	public int countOfScheduling() {
		return taskExecutor.taskCount();
	}

	public Date getStartDate() {
		return startDate;
	}

	static void checkJobNameIfBlank(Job job) {
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
