package com.github.paganini2008.springboot.amber;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.github.paganini2008.springboot.amber.config.DbConfig;
import com.github.paganini2008.springboot.amber.config.JobBeanFactory;
import com.github.paganini2008.springboot.amber.config.JobManager;
import com.github.paganini2008.springboot.amber.config.NoDispatchingConfig;
import com.github.paganini2008.springboot.amber.config.SchedulerConfig;
import com.github.paganini2008.springboot.amber.rabbitmq.RabbitmqConfig;
import com.github.paganini2008.springboot.amber.redis.JobServerEventListener;
import com.github.paganini2008.springboot.amber.redis.RedisConfig;
import com.github.paganini2008.springboot.amber.test.HealthJobBean;
import com.github.paganini2008.springboot.amber.utils.JobAdminController;
import com.github.paganini2008.springboot.amber.utils.JobAdminService;

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
