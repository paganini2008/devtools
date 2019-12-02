package com.github.paganini2008.springworld.amber.config;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.spi.MutableTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

/**
 * 
 * JobManager
 * 
 * @author Fred Feng
 * @create 2018-03
 */
@Component
public class JobManager {

	private static final Logger logger = LoggerFactory.getLogger(JobManager.class);

	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;

	@Value("${amber.job.groupName:amber-job-group-0}")
	private String defaultJobGroupName;

	@Value("${amber.job.trigger.groupName:amber-trigger-group-0}")
	private String defaultTriggerGroupName;

	public Scheduler getScheduler() {
		return schedulerFactoryBean.getScheduler();
	}

	public void addJob(String name, Class<?> jobClass, String description, long delayInMs, long intervalInMs, int repeatCount,
			Map<String, Object> kwargs) throws Exception {
		JobDetail jobDetail = JobBuilder.newJob(DispatcherJobBean.class).withIdentity(name, defaultJobGroupName)
				.withDescription(description).build();
		JobDataMap dataMap = jobDetail.getJobDataMap();
		JobParameter jobParameter = new JobParameterImpl(name, jobClass, description, kwargs);
		dataMap.put("parameter", jobParameter);
		MutableTrigger trigger = SimpleScheduleBuilder.simpleSchedule().withRepeatCount(repeatCount)
				.withIntervalInMilliseconds(intervalInMs).build();
		trigger.setKey(TriggerKey.triggerKey(name, defaultTriggerGroupName));
		trigger.setStartTime(new Date(System.currentTimeMillis() + delayInMs));
		getScheduler().scheduleJob(jobDetail, trigger);
		logger.info("Add job '{}' successfully.", name);
	}

	public void addDelayedJob(String name, Class<?> jobClass, String description, long delayInMs, Map<String, Object> kwargs) throws Exception {
		JobDetail jobDetail = JobBuilder.newJob(DispatcherJobBean.class).withIdentity(name, defaultJobGroupName)
				.withDescription(description).build();
		JobDataMap dataMap = jobDetail.getJobDataMap();
		JobParameter jobParameter = new JobParameterImpl(name, jobClass, description, kwargs);
		dataMap.put("parameter", jobParameter);
		MutableTrigger trigger = SimpleScheduleBuilder.simpleSchedule().withRepeatCount(0).withIntervalInMilliseconds(delayInMs).build();
		trigger.setKey(TriggerKey.triggerKey(name, defaultTriggerGroupName));
		trigger.setStartTime(new Date(System.currentTimeMillis() + delayInMs));
		getScheduler().scheduleJob(jobDetail, trigger);
		logger.info("Add job '{}' successfully.", name);
	}

	public void addCronJob(String name, Class<?> jobClass, String description, String cron, Map<String, Object> kwargs) throws Exception {
		JobDetail jobDetail = JobBuilder.newJob(DispatcherJobBean.class).withIdentity(name, defaultJobGroupName)
				.withDescription(description).build();
		JobDataMap dataMap = jobDetail.getJobDataMap();
		JobParameter jobParameter = new JobParameterImpl(name, jobClass, description, kwargs);
		dataMap.put("parameter", jobParameter);
		CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron).withMisfireHandlingInstructionDoNothing();
		CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(name, defaultTriggerGroupName).withSchedule(scheduleBuilder).build();
		getScheduler().scheduleJob(jobDetail, trigger);
		logger.info("Add job '{}' successfully.", name);
	}

	public void modifyJobTime(String name, String cron) throws Exception {
		TriggerKey triggerKey = new TriggerKey(name, defaultTriggerGroupName);
		CronTrigger trigger = (CronTrigger) getScheduler().getTrigger(triggerKey);
		if (trigger != null) {
			String oldCron = trigger.getCronExpression();
			if (!oldCron.equalsIgnoreCase(cron)) {
				CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
				CronTrigger cronTrigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
				getScheduler().rescheduleJob(triggerKey, cronTrigger);
			}
		}
	}

	public void pauseJob(String name) throws Exception {
		TriggerKey triggerKey = TriggerKey.triggerKey(name, defaultTriggerGroupName);
		if (getScheduler().checkExists(triggerKey)) {
			getScheduler().pauseTrigger(triggerKey);
		}
		JobKey jobKey = JobKey.jobKey(name, defaultJobGroupName);
		if (getScheduler().checkExists(jobKey)) {
			getScheduler().pauseJob(jobKey);
		}
	}

	public void pauseAll() throws Exception {
		getScheduler().pauseAll();
	}

	public boolean checkExists(String name) throws Exception {
		JobKey jobKey = JobKey.jobKey(name, defaultJobGroupName);
		TriggerKey triggerKey = TriggerKey.triggerKey(name, defaultTriggerGroupName);
		return getScheduler().checkExists(jobKey) && getScheduler().checkExists(triggerKey);
	}

	public void deleteJob(String name) throws Exception {
		TriggerKey triggerKey = TriggerKey.triggerKey(name, defaultTriggerGroupName);
		if (getScheduler().checkExists(triggerKey)) {
			getScheduler().pauseTrigger(triggerKey);
			getScheduler().unscheduleJob(triggerKey);
		}
		JobKey jobKey = JobKey.jobKey(name, defaultJobGroupName);
		if (getScheduler().checkExists(jobKey)) {
			getScheduler().deleteJob(jobKey);
		}
	}

	public void deleteAll() throws Exception {
		Set<JobKey> jobKeys = getScheduler().getJobKeys(GroupMatcher.jobGroupEquals(defaultJobGroupName));
		getScheduler().deleteJobs(new ArrayList<JobKey>(jobKeys));
	}

	public void resumeAll() throws Exception {
		getScheduler().resumeAll();
	}

	public void resumeJob(String name) throws Exception {
		TriggerKey triggerKey = TriggerKey.triggerKey(name, defaultTriggerGroupName);
		if (getScheduler().checkExists(triggerKey)) {
			getScheduler().resumeTrigger(triggerKey);
		}
		JobKey jobKey = JobKey.jobKey(name, defaultJobGroupName);
		if (getScheduler().checkExists(jobKey)) {
			getScheduler().resumeJob(jobKey);
		}
	}

	public void runJob(String name, Map<String, Object> kwargs) throws Exception {
		JobKey jobKey = new JobKey(name, defaultJobGroupName);
		if (getScheduler().checkExists(jobKey)) {
			if (kwargs != null) {
				JobDetail jobDetail = getScheduler().getJobDetail(jobKey);
				jobDetail.getJobDataMap().putAll(kwargs);
			}
			getScheduler().triggerJob(jobKey);
		}
	}

	public void standby() throws Exception {
		getScheduler().standby();
	}

	public void start() throws Exception {
		getScheduler().start();
	}

	public void stop() throws Exception {
		getScheduler().shutdown(true);
	}

	public String getJobGroupName() {
		return defaultJobGroupName;
	}

	public void setJobGroupName(String jobGroupName) {
		this.defaultJobGroupName = jobGroupName;
	}

	public String getTriggerGroupName() {
		return defaultTriggerGroupName;
	}

	public void setTriggerGroupName(String triggerGroupName) {
		this.defaultTriggerGroupName = triggerGroupName;
	}

}
