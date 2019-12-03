package com.github.paganini2008.springworld.scheduler.quartz;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.reflection.MethodUtils;
import com.github.paganini2008.springworld.scheduler.CancellationException;
import com.github.paganini2008.springworld.scheduler.JobAnnotations.Executable;
import com.github.paganini2008.springworld.scheduler.JobAnnotations.OnEnd;
import com.github.paganini2008.springworld.scheduler.JobAnnotations.OnError;
import com.github.paganini2008.springworld.scheduler.JobAnnotations.OnStart;
import com.github.paganini2008.springworld.scheduler.JobManager;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * QuartzJobBeanProxy
 * 
 * @author Fred Feng
 * @created 2018-03
 * @revised 2019-11
 * @version 1.0
 */
@Slf4j
public class QuartzJobBeanProxy implements Job {

	private static final Map<String, Type> jobClassCache = Collections.synchronizedMap(new HashMap<String, Type>());
	private static final Map<Type, Method[]> jobClassMetaDataCache = Collections.synchronizedMap(new HashMap<Type, Method[]>());

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

	private static Method[] getJobMethods(Class<?> jobClass) {
		Method[] methods = jobClassMetaDataCache.get(jobClass);
		if (methods == null) {
			jobClassMetaDataCache.put(jobClass, getMethods(jobClass));
			methods = jobClassMetaDataCache.get(jobClass);
		}
		return methods;
	}

	private static Method[] getMethods(Class<?> jobClass) {
		Method onStart = null, executable, onEnd = null, onError = null;
		try {
			executable = MethodUtils.getDeclaredMethodsWithAnnotation(jobClass, Executable.class).get(0);
		} catch (RuntimeException e) {
			throw new IllegalStateException("Executable method is required.", e);
		}
		try {
			onStart = MethodUtils.getDeclaredMethodsWithAnnotation(jobClass, OnStart.class).get(0);
		} catch (RuntimeException ignored) {
		}
		try {
			onEnd = MethodUtils.getDeclaredMethodsWithAnnotation(jobClass, OnEnd.class).get(0);
		} catch (RuntimeException ignored) {
		}
		try {
			onError = MethodUtils.getDeclaredMethodsWithAnnotation(jobClass, OnError.class).get(0);
		} catch (RuntimeException ignored) {
		}
		return new Method[] { onStart, executable, onEnd, onError };
	}

	private ApplicationContext applicationContext;

	@Value("${spring.task-scheduler.failedjob.retries:0}")
	private int retries;

	@Autowired
	private JobManager jobManager;

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

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
		Method[] jobMethods = getJobMethods(jobClass);
		Method onStart = jobMethods[0];
		Method executable = jobMethods[1];
		Method onEnd = jobMethods[2];
		Method onError = jobMethods[3];

		int n = 0;
		log.trace("Execute job: " + jobBean.getClass().getName());
		if (onStart != null) {
			try {
				MethodUtils.invokeMethod(jobBean, onStart);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		Object result = null;
		Throwable cause = null;
		do {
			if (n > 0) {
				log.trace("Retry this job: " + jobBeanName + "/" + jobBean.getClass().getName());
			}
			try {
				result = MethodUtils.invokeMethod(jobBean, executable);
				break;
			} catch (Exception e) {
				cause = e;
				log.error(e.getMessage(), e);
			}
		} while (n++ < retries);

		if (result instanceof Boolean && !(Boolean) result) {
			try {
				jobManager.unscheduleJob(jobBeanName);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}

		if (cause != null) {
			if (onError != null) {
				try {
					MethodUtils.invokeMethod(jobBean, onError, cause);
				} catch (Exception e) {
					if (e instanceof CancellationException) {
						try {
							jobManager.unscheduleJob(jobBeanName);
						} catch (Exception ee) {
							log.error(ee.getMessage(), ee);
						}
					} else {
						log.error(e.getMessage(), e);
					}
				}
			}
		}
		if (onEnd != null) {
			try {
				MethodUtils.invokeMethod(jobBean, onEnd);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		log.trace("Finish job: " + jobBean.getClass().getName());
	}

}