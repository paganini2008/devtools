package com.github.paganini2008.springworld.crontab;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * JobStat
 *
 * @author Fred Feng
 * @created 2020-01
 * @revised 2020-02
 * @version 1.0
 */
@Getter
@Setter
public class JobStat implements Serializable {

	private static final long serialVersionUID = -3310884557055143589L;

	private String applicationName;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date startDate;
	private int jobCount;
	private List<JobInfo> jobInfos;

}
