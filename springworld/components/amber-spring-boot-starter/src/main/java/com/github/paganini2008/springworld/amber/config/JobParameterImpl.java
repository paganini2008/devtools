package com.github.paganini2008.springworld.amber.config;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.github.paganini2008.devtools.collection.Tuple;

/**
 * 
 * JobParameterImpl
 * 
 * @author Fred Feng
 * @create 2018-03
 */
public class JobParameterImpl implements JobParameter, Serializable {

	private static final long serialVersionUID = 6610621563383319197L;
	private final String name;
	private final Class<?> jobClass;
	private final String description;
	private final Tuple tuple;

	public JobParameterImpl(String name, Class<?> jobClass, String description, Map<String, Object> kwargs) {
		this.name = name;
		this.jobClass = jobClass;
		this.description = description;
		this.tuple = kwargs != null ? Tuple.wrap(kwargs) : Tuple.newTuple();
	}

	public String getJobName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Tuple getTuple() {
		return tuple;
	}

	public String getName() {
		return name;
	}

	public Class<?> getJobClass() {
		return jobClass;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
