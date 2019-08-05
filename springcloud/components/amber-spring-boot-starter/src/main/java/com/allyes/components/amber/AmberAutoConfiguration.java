package com.allyes.components.amber;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.allyes.components.amber.config.DbConfig;
import com.allyes.components.amber.config.JobBeanFactory;
import com.allyes.components.amber.config.JobManager;
import com.allyes.components.amber.config.NoDispatchingConfig;
import com.allyes.components.amber.config.SchedulerConfig;
import com.allyes.components.amber.rabbitmq.RabbitmqConfig;
import com.allyes.components.amber.redis.JobServerEventListener;
import com.allyes.components.amber.redis.RedisConfig;
import com.allyes.components.amber.test.HealthJobBean;
import com.allyes.components.amber.utils.JobAdminController;
import com.allyes.components.amber.utils.JobAdminService;

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
