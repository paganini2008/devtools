package com.github.paganini2008.springworld.amber.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.paganini2008.springworld.amber.config.JobManager;

/**
 * 
 * JobAdminService
 * 
 * @author Fred Feng
 * @created 2019-04
 */
@Component
public class JobAdminService {

	@Autowired
	private JobManager jobManager;

	public List<JobInfo> getJobInfos() throws Exception {
		List<JobInfo> results = new ArrayList<JobInfo>();
		Set<JobKey> jobKeys = jobManager.getScheduler().getJobKeys(GroupMatcher.jobGroupEquals(jobManager.getJobGroupName()));
		if (jobKeys != null) {
			for (JobKey jobKey : jobKeys) {
				JobDetail jobDetail = jobManager.getScheduler().getJobDetail(jobKey);
				JobInfo jobInfo = new JobInfo();
				jobInfo.setName(jobDetail.getKey().getName());
				jobInfo.setGroupName(jobDetail.getKey().getGroup());
				jobInfo.setDescription(jobDetail.getDescription());
				jobInfo.setKwargs(new HashMap<String, Object>(jobDetail.getJobDataMap()));
				results.add(jobInfo);
			}
		}
		return results;
	}

	public List<TriggerInfo> getTriggerInfos() throws Exception {
		List<TriggerInfo> results = new ArrayList<TriggerInfo>();
		Set<TriggerKey> triggerKeys = jobManager.getScheduler()
				.getTriggerKeys(GroupMatcher.triggerGroupEquals(jobManager.getTriggerGroupName()));
		if (triggerKeys != null) {
			for (TriggerKey triggerKey : triggerKeys) {
				Trigger trigger = jobManager.getScheduler().getTrigger(triggerKey);
				TriggerState triggerState = jobManager.getScheduler().getTriggerState(triggerKey);
				TriggerInfo triggerInfo = new TriggerInfo();
				triggerInfo.setName(trigger.getKey().getName());
				triggerInfo.setGroupName(trigger.getKey().getGroup());
				triggerInfo.setState(triggerState);
				triggerInfo.setStartTime(trigger.getStartTime());
				triggerInfo.setEndTime(trigger.getEndTime());
				triggerInfo.setNextFireTime(trigger.getNextFireTime());
				triggerInfo.setPreviousFireTime(trigger.getPreviousFireTime());
				triggerInfo.setFinalFireTime(trigger.getFinalFireTime());
				triggerInfo.setDescription(trigger.getDescription());
				results.add(triggerInfo);
			}
		}
		return results;
	}

}
