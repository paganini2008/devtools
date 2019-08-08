package com.github.paganini2008.springworld.amber.config;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * LoggingSchedulerListener
 * 
 * @author Fred Feng
 * @create 2018-03
 */
public class LoggingSchedulerListener implements SchedulerListener {

	private static final Logger logger = LoggerFactory.getLogger(LoggingSchedulerListener.class);

	public void jobScheduled(Trigger trigger) {
		logger.info("Job is scheduled. Trigger: " + trigger);
	}

	public void jobUnscheduled(TriggerKey triggerKey) {
		logger.info("Job is unscheduled. TriggerKey: " + triggerKey);
	}

	public void triggerFinalized(Trigger trigger) {
		logger.info("Trigger is finalized. Trigger: " + trigger);
	}

	public void triggerPaused(TriggerKey triggerKey) {
		logger.info("Trigger is paused. TriggerKey: " + triggerKey);
	}

	public void triggersPaused(String triggerGroup) {
		logger.info("Triggers are paused. TriggerGroup: " + triggerGroup);
	}

	public void triggerResumed(TriggerKey triggerKey) {
		logger.info("Trigger is resumed. TriggerKey: " + triggerKey);
	}

	public void triggersResumed(String triggerGroup) {
		logger.info("Triggers are resumed. TriggerGroup: " + triggerGroup);
	}

	public void jobAdded(JobDetail jobDetail) {
		logger.info("Job is added. JobDetail: " + jobDetail);
	}

	public void jobDeleted(JobKey jobKey) {
		logger.info("Job is deleted. JobKey: " + jobKey);
	}

	public void jobPaused(JobKey jobKey) {
		logger.info("Job is paused. JobKey: " + jobKey);
	}

	public void jobsPaused(String jobGroup) {
		logger.info("Jobs are paused. JobGroup: " + jobGroup);
	}

	public void jobResumed(JobKey jobKey) {
		logger.info("Job is resumed. JobKey: " + jobKey);
	}

	public void jobsResumed(String jobGroup) {
		logger.info("Jobs are resumed. JobGroup: " + jobGroup);
	}

	public void schedulerError(String msg, SchedulerException cause) {
		logger.error(msg, cause);
	}

	public void schedulerInStandbyMode() {
		logger.info("Scheduler is in standby mode now.");
	}

	public void schedulerStarted() {
		logger.info("Scheduler is started now.");
	}

	public void schedulerStarting() {
		logger.info("Scheduler is starting ...");
	}

	public void schedulerShutdown() {
		logger.info("Scheduler is shut down now.");
	}

	public void schedulerShuttingdown() {
		logger.info("Scheduler is shutting down ...");
	}

	public void schedulingDataCleared() {
		logger.info("SchedulingData has been cleared now.");
	}

}
