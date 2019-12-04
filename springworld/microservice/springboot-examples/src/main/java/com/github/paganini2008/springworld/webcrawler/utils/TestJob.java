package com.github.paganini2008.springworld.webcrawler.utils;

import com.github.paganini2008.devtools.date.DateUtils;
import com.github.paganini2008.springworld.scheduler.Job;
import com.github.paganini2008.springworld.scheduler.JobAnnotations.Executable;

import lombok.extern.slf4j.Slf4j;

/**
 * TestJob
 */
@Slf4j
@Job(cron = "*/3 * * * * ?")
public class TestJob {

	@Executable
	public void run() {
		log.info("Test: " + DateUtils.format(System.currentTimeMillis()));
	}

}
