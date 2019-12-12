package com.github.paganini2008.springworld.scheduler.quartz;

import java.util.Set;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.github.paganini2008.devtools.Observable;
import com.github.paganini2008.springworld.scheduler.JobManager;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * QuartzJobManager
 * 
 * @author Fred Feng
 * @created 2018-03
 * @revised 2019-11
 * @version 1.0
 */
@Slf4j
public class QuartzJobManager implements JobManager {

	private final Observable observable = Observable.unrepeatable();

	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;

	@Value("${spring.quartz.job.groupName:defaultJobGroup}")
	private String defaultJobGroupName;

	@Value("${spring.quartz.job.trigger.groupName:defaultTriggerGroup}")
	private String defaultTriggerGroupName;

	@Value("${spring.task-scheduler.loadbalance.enabled:false}")
	private boolean loadbalanced;

	public Scheduler getScheduler() {
		return schedulerFactoryBean.getScheduler();
	}

	@Override
	public void scheduleJob(Object bean, String jobName, String description, String cronExpression) throws SchedulerException {
		observable.addObserver((ob, arg) -> {
			boolean jobExists;
			try {
				JobKey jobKey = JobKey.jobKey(jobName, defaultJobGroupName);
				jobExists = getScheduler().checkExists(jobKey);
			} catch (SchedulerException e) {
				jobExists = false;
			}
			if (jobExists) {
				log.info("Recover to schedule job: {}/{}" + jobName, bean.getClass().getName());
			} else {
				JobDetail jobDetail = JobBuilder.newJob(loadbalanced ? LoadBalancedQuartzJobBeanProxy.class : QuartzJobBeanProxy.class)
						.withIdentity(jobName, defaultJobGroupName).withDescription(description).build();
				JobDataMap dataMap = jobDetail.getJobDataMap();
				dataMap.put("jobBeanName", jobName);
				dataMap.put("jobBeanClassName", bean.getClass().getName());
				CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression)
						.withMisfireHandlingInstructionDoNothing();
				CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName, defaultTriggerGroupName)
						.withSchedule(scheduleBuilder).build();
				try {
					getScheduler().scheduleJob(jobDetail, trigger);
					log.info("Start to schedule job: {}/{}" + jobName, bean.getClass().getName());
				} catch (SchedulerException e) {
					log.error(e.getMessage(), e);
				}
			}
		});
		log.info("Add job: {}/{} to schedule.", jobName, bean.getClass().getName());

	}

	@Override
	public void unscheduleJob(String jobName) throws SchedulerException {
		TriggerKey triggerKey = TriggerKey.triggerKey(jobName, defaultTriggerGroupName);
		if (getScheduler().checkExists(triggerKey)) {
			getScheduler().pauseTrigger(triggerKey);
			getScheduler().unscheduleJob(triggerKey);
		}
		JobKey jobKey = JobKey.jobKey(jobName, defaultJobGroupName);
		if (getScheduler().checkExists(jobKey)) {
			getScheduler().deleteJob(jobKey);
			log.info("Remove job: {} from schedule.", jobName);
		}
	}

	@Override
	public void pauseJob(String jobName) throws SchedulerException {
		TriggerKey triggerKey = TriggerKey.triggerKey(jobName, defaultTriggerGroupName);
		if (getScheduler().checkExists(triggerKey)) {
			getScheduler().pauseTrigger(triggerKey);
		}
		JobKey jobKey = JobKey.jobKey(jobName, defaultJobGroupName);
		if (getScheduler().checkExists(jobKey)) {
			getScheduler().pauseJob(jobKey);
			log.info("Pause job: {} from schedule.", jobName);
		}
	}

	@Override
	public void resumeJob(String jobName) throws SchedulerException {
		TriggerKey triggerKey = TriggerKey.triggerKey(jobName, defaultTriggerGroupName);
		if (getScheduler().checkExists(triggerKey)) {
			getScheduler().resumeTrigger(triggerKey);
		}
		JobKey jobKey = JobKey.jobKey(jobName, defaultJobGroupName);
		if (getScheduler().checkExists(jobKey)) {
			getScheduler().resumeJob(jobKey);
		}
	}

	@Override
	public boolean hasScheduled(String jobName) throws SchedulerException {
		JobKey jobKey = JobKey.jobKey(jobName, defaultJobGroupName);
		TriggerKey triggerKey = TriggerKey.triggerKey(jobName, defaultTriggerGroupName);
		return getScheduler().checkExists(jobKey) && getScheduler().checkExists(triggerKey);
	}

	@Override
	public int countOfJobs() throws SchedulerException {
		Set<JobKey> jobKeys = getScheduler().getJobKeys(GroupMatcher.jobGroupEquals(defaultJobGroupName));
		return jobKeys != null ? jobKeys.size() : 0;
	}

	@Override
	public void runNow() {
		observable.notifyObservers();
	}

}
