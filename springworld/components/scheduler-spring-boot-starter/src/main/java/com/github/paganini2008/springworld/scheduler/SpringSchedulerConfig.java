package com.github.paganini2008.springworld.scheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 
 * SpringSchedulerConfig
 * 
 * @author Fred Feng
 * @created 2019-11
 * @revised 2019-12
 * @version 1.0
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.task-scheduler", name = "engine", havingValue = "default", matchIfMissing = true)
public class SpringSchedulerConfig {

	@Value("${spring.task-scheduler.poolSize:8}")
	private int poolSize;

	@Bean
	@ConditionalOnMissingBean(TaskScheduler.class)
	public TaskScheduler taskScheduler() {
		ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
		taskScheduler.setPoolSize(poolSize);
		taskScheduler.setThreadNamePrefix("task-scheduler-");
		taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
		taskScheduler.setAwaitTerminationSeconds(60);
		return taskScheduler;
	}

	@Bean
	@ConditionalOnMissingBean(JobManager.class)
	public JobManager jobManager() {
		return new MemoryJobManager();
	}

}
