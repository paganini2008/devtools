package com.github.paganini2008.springworld.amber;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.github.paganini2008.springworld.amber.config.DbConfig;
import com.github.paganini2008.springworld.amber.config.JobBeanFactory;
import com.github.paganini2008.springworld.amber.config.JobManager;
import com.github.paganini2008.springworld.amber.config.NoDispatchingConfig;
import com.github.paganini2008.springworld.amber.config.SchedulerConfig;
import com.github.paganini2008.springworld.amber.rabbitmq.RabbitmqConfig;
import com.github.paganini2008.springworld.amber.redis.JobServerEventListener;
import com.github.paganini2008.springworld.amber.redis.RedisConfig;
import com.github.paganini2008.springworld.amber.test.HealthJobBean;
import com.github.paganini2008.springworld.amber.utils.JobAdminController;
import com.github.paganini2008.springworld.amber.utils.JobAdminService;

/**
 * 
 * AmberAutoConfiguration
 * 
 * @author Fred Feng
 * @create 2018-03
 */
@Configuration
@Import({ AmberConfig.class,
		  DbConfig.class, 
	      JobBeanFactory.class, 
	      JobManager.class, 
	      JobAdminService.class,
	      NoDispatchingConfig.class,
	      RedisConfig.class, 
	      JobServerEventListener.class,
		  RabbitmqConfig.class, 
		  SchedulerConfig.class,
		  JobAdminController.class ,
		  HealthJobBean.class})
public class AmberAutoConfiguration {
}
