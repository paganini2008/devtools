package com.github.paganini2008.springworld.amber.utils;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.paganini2008.springworld.amber.config.JobManager;
import com.github.paganini2008.springworld.amber.test.OneJobBean;

/**
 * 
 * JobAdminController
 * 
 * @author Fred Feng
 * @created 2018-03
 */
@RestController
@RequestMapping("/job-admin")
public class JobAdminController {

	@Autowired
	private JobManager jobManager;

	@Autowired
	private JobAdminService jobAdminService;

	@GetMapping("/getJobInfos")
	public ResultEntity<List<JobInfo>> getJobInfos() throws Exception {
		return ResultEntity.onSuccess(jobAdminService.getJobInfos());
	}

	@GetMapping("/getTriggerInfos")
	public ResultEntity<List<TriggerInfo>> getTriggerInfos() throws Exception {
		return ResultEntity.onSuccess(jobAdminService.getTriggerInfos());
	}

	@GetMapping("/resumeJob")
	public ResultEntity<String> resumeJob(@RequestParam("jobName") String jobName) throws Exception {
		jobManager.resumeJob(jobName);
		return ResultEntity.onSuccess("Resume Job '" + jobName + "' OK");
	}

	@GetMapping("/pauseJob")
	public ResultEntity<String> pauseJob(@RequestParam("jobName") String jobName) throws Exception {
		jobManager.pauseJob(jobName);
		return ResultEntity.onSuccess("Pause Job '" + jobName + "' OK");
	}

	@GetMapping("/deleteJob")
	public ResultEntity<String> deleteJob(@RequestParam("jobName") String jobName) throws Exception {
		jobManager.deleteJob(jobName);
		return ResultEntity.onSuccess("Delete Job '" + jobName + "' OK");
	}

	@GetMapping("/test/addJob")
	public String addJob(@RequestParam("jobName") String jobName, @RequestParam("cron") String cron) throws Exception {
		if (!jobManager.checkExists(jobName)) {
			jobManager.addCronJob(jobName, OneJobBean.class, "Test non-singleton job bean.", cron.trim(), null);
		}
		return "ok";
	}

}
