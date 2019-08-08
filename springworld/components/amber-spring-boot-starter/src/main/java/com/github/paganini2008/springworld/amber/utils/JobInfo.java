package com.github.paganini2008.springworld.amber.utils;

import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 
 * JobInfo
 * 
 * @author Fred Feng
 * @created 2019-04
 */
public class JobInfo {

	private String name;
	private String groupName;
	private String description;
	private Map<String, Object> kwargs;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Map<String, Object> getKwargs() {
		return kwargs;
	}

	public void setKwargs(Map<String, Object> kwargs) {
		this.kwargs = kwargs;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
