package com.github.paganini2008.springworld.crontab;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * JobInfo
 *
 * @author Fred Feng
 * @created 2020-01
 * @revised 2020-02
 * @version 1.0
 */
@Setter
@Getter
@ToString
public class JobInfo implements Serializable {

	private static final long serialVersionUID = 5741263651318840914L;

	private String jobName;
	private String jobClass;
	private String description;
	private boolean running;
	private int completedCount;
	private int failedCount;
	private Date lastExecuted;
	private Date nextExecuted;

}
