package com.github.paganini2008.springworld.quartz;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.reflection.MethodUtils;
import com.github.paganini2008.springworld.scheduler.JobDetail;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * QuartzJobBeanProxy
 * 
 * @author Fred Feng
 * @created 2018-03
 * @version 1.0
 */
@Slf4j
public class QuartzJobBeanProxy implements Job, JobDetail {

	private static final Map<String, Type> jobClassCache = Collections.synchronizedMap(new HashMap<String, Type>());

	private static Class<?> getJobClassIfAvailable(String className) {
		Type type = jobClassCache.get(className);
		if (type == null) {
			try {
				jobClassCache.put(className, Class.forName(className));
				type = jobClassCache.get(className);
			} catch (ClassNotFoundException e) {
				log.error("Job class: {} is required.", className);
			}
		}
		return (Class<?>) type;
	}

	private ApplicationContext applicationContext;

	public void configure(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	private RedisAtomicInteger 

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		String jobBeanName = (String) dataMap.get("jobBeanName");
		String jobBeanClassName = (String) dataMap.get("jobBeanClassName");
		Class<?> jobClass = null;
		if (StringUtils.isNotBlank(jobBeanClassName)) {
			jobClass = getJobClassIfAvailable(jobBeanClassName);
		}
		if (jobClass == null) {
			return;
		}
			Object jobBean = applicationContext.getBean(jobBeanName, jobClass);
			Object result = MethodUtils.invokeMethod(jobBean, "execute", this);
			lastExecuted = System.currentTimeMillis();
			int n = 0;
			log.trace("Execute job: " + jobBean.getClass().getName());
			do {
				if (n > 0) {
					log.trace("Retry this job: " + jobName + "/" + bean.getClass().getName());
				}
				Object result;
				try {
					result = MethodUtils.invokeMethod(bean, "execute", this);
					if (result instanceof Boolean && !(Boolean) result) {
						jobManager.unscheduleJob(jobName);
					}
					if (n > 0) {
						n = 0;
					}
					break;
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			} while (n++ < retries);

			if (n > 0) {
				failedCount.incrementAndGet();
			} else {
				successCount.incrementAndGet();
			}
	}

}
