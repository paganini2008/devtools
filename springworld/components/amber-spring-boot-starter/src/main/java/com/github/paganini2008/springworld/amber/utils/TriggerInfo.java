package com.github.paganini2008.springworld.amber.utils;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.quartz.Trigger.TriggerState;

import com.github.paganini2008.devtools.date.DateUtils;

/**
 * 
 * TriggerInfo
 * 
 * @author Fred Feng
 * @created 2019-04
 */
public class TriggerInfo {

	private String name;
	private String groupName;
	private String jobName;
	private String jobGroupName;
	private TriggerState state;
	private Date startTime;
	private Date endTime;
	private Date nextFireTime;
	private Date previousFireTime;
	private Date finalFireTime;
	private String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobGroupName() {
		return jobGroupName;
	}

	public void setJobGroupName(String jobGroupName) {
		this.jobGroupName = jobGroupName;
	}

	public String getState() {
		return state != null ? state.name() : "-";
	}

	public void setState(TriggerState state) {
		this.state = state;
	}

	public String getStartTime() {
		return startTime != null ? DateUtils.format(startTime) : "-";
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime != null ? DateUtils.format(endTime) : "-";
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getNextFireTime() {
		return nextFireTime != null ? DateUtils.format(nextFireTime) : "-";
	}

	public void setNextFireTime(Date nextFireTime) {
		this.nextFireTime = nextFireTime;
	}

	public String getPreviousFireTime() {
		return previousFireTime != null ? DateUtils.format(previousFireTime) : "-";
	}

	public void setPreviousFireTime(Date previousFireTime) {
		this.previousFireTime = previousFireTime;
	}

	public String getFinalFireTime() {
		return finalFireTime != null ? DateUtils.format(finalFireTime) : "-";
	}

	public void setFinalFireTime(Date finalFireTime) {
		this.finalFireTime = finalFireTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
