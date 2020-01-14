package com.github.paganini2008.springworld.crontab;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.github.paganini2008.springworld.cluster.ContextClusterConfig;

/**
 * 
 * CrontabAutoConfiguration
 *
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
@Configuration
@ConditionalOnProperty(name = "spring.task-scheduler.engine", havingValue = "crontab")
@ConditionalOnBean(ContextClusterConfig.class)
@Import({ JobBeanPostProcessor.class, JobServerStandbyAware.class, MemoryUsageCheckJob.class })
public class CrontabAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(JobManager.class)
	public JobManager jobManager() {
		return new MemoryJobManager();
	}

}
