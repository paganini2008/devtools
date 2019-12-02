package com.github.paganini2008.springworld.quartz;

import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 
 * QuartzConfig
 * 
 * @author Fred Feng
 * @created 2018-03
 */
@Configuration
public class QuartzConfig {

	public static final String QUARTZ_PROPERTIES_PATH = "/quartz.properties";

	@Bean
	public JobFactory jobFactory(ApplicationContext applicationContext) {
		AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
		jobFactory.setApplicationContext(applicationContext);
		return jobFactory;
	}

	@Bean(destroyMethod = "destroy")
	public SchedulerFactoryBean schedulerFactoryBean(JobFactory jobFactory, DataSource dataSource,
			PlatformTransactionManager transactionManager) throws Exception {
		SchedulerFactoryBean factory = new SchedulerFactoryBean();
		factory.setOverwriteExistingJobs(true);
		factory.setAutoStartup(true);
		factory.setStartupDelay(10);
		factory.setJobFactory(jobFactory);
		factory.setDataSource(dataSource);
		factory.setTransactionManager(transactionManager);
		factory.setQuartzProperties(quartzProperties());
		factory.setWaitForJobsToCompleteOnShutdown(true);
		return factory;
	}

	@Bean
	public Properties quartzProperties() throws IOException {
		PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
		propertiesFactoryBean.setLocation(new ClassPathResource(QUARTZ_PROPERTIES_PATH));
		propertiesFactoryBean.afterPropertiesSet();
		return propertiesFactoryBean.getObject();
	}

	public static class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory implements ApplicationContextAware {

		private ApplicationContext applicationContext;
		private AutowireCapableBeanFactory beanFactory;

		AutowiringSpringBeanJobFactory() {
		}

		public void setApplicationContext(final ApplicationContext context) {
			this.applicationContext = context;
			this.beanFactory = context.getAutowireCapableBeanFactory();
		}

		protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
			final Object jobBean = super.createJobInstance(bundle);
			beanFactory.autowireBean(jobBean);
			return jobBean;
		}

		public ApplicationContext getApplicationContext() {
			return applicationContext;
		}

	}

}
