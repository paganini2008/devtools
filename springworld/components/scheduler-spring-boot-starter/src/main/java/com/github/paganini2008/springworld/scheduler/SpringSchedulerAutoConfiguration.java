package com.github.paganini2008.springworld.scheduler;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.github.paganini2008.springworld.scheduler.quartz.QuartzConfig;

/**
 * 
 * SpringSchedulerAutoConfiguration
 * 
 * @author Fred Feng
 * @created 2019-11
 * @version 1.0
 */
@Configuration
@Import({ SpringSchedulerConfig.class, QuartzConfig.class, JobBeanAwareProcessor.class, JobExecutionAware.class })
public class SpringSchedulerAutoConfiguration {
}
