package com.github.paganini2008.springworld.crontab;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.paganini2008.devtools.jdbc.PageRequest;
import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

/**
 * 
 * JobManagerController
 *
 * @author Fred Feng
 * @created 2020-01
 * @revised 2020-02
 * @version 1.0
 */
@RestController
@RequestMapping("/job")
public class JobManagerController {

	@Value("${spring.application.name}")
	private String applicationName;

	@Autowired
	private JobManager jobManager;

	@GetMapping("/stat")
	public ResponseEntity<JobStat> jobStat(@RequestParam(value = "page", defaultValue = "1", required = false) int page,
			@CookieValue(value = "PAGE_SIZE", required = false, defaultValue = "10") int size) {
		JobStat jobStat = new JobStat();
		jobStat.setApplicationName(applicationName);
		jobStat.setStartDate(jobManager.getStartDate());

		ResultSetSlice<JobInfo> resultSetSlice = jobManager.getJobInfos();
		jobStat.setJobInfos(resultSetSlice.list(PageRequest.of(page, size)).getContent());
		jobStat.setJobCount(resultSetSlice.totalCount());
		return new ResponseEntity<JobStat>(jobStat, HttpStatus.OK);
	}

}
