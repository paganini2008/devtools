package com.github.paganini2008.springworld.scheduler;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.paganini2008.devtools.reflection.MethodUtils;
import com.github.paganini2008.springworld.scheduler.JobAnnotations.Executable;
import com.github.paganini2008.springworld.scheduler.JobAnnotations.OnEnd;
import com.github.paganini2008.springworld.scheduler.JobAnnotations.OnError;
import com.github.paganini2008.springworld.scheduler.JobAnnotations.OnStart;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * JobBeanProxy
 * 
 * @author Fred Feng
 * @created 2019-11
 * @version 1.0
 */
@Slf4j
public class JobBeanProxy implements Runnable {

	private final Object bean;
	private final String jobName;
	private final JobManager jobManager;
	private final AtomicBoolean running = new AtomicBoolean(true);
	private final int retries;

	JobBeanProxy(Object bean, String jobName, int retries, JobManager jobManager) {
		this.bean = bean;
		this.jobName = jobName;
		this.retries = retries;
		this.jobManager = jobManager;

		reflectJobBean();
	}

	private Method onStart;
	private Method onEnd;
	private Method onError;
	private Method executable;

	private void reflectJobBean() {
		try {
			executable = MethodUtils.getDeclaredMethodsWithAnnotation(bean.getClass(), Executable.class).get(0);
		} catch (RuntimeException e) {
			throw new IllegalStateException("Executable method is required.", e);
		}
		try {
			onStart = MethodUtils.getDeclaredMethodsWithAnnotation(bean.getClass(), OnStart.class).get(0);
		} catch (RuntimeException ignored) {
		}
		try {
			onEnd = MethodUtils.getDeclaredMethodsWithAnnotation(bean.getClass(), OnEnd.class).get(0);
		} catch (RuntimeException ignored) {
		}
		try {
			onError = MethodUtils.getDeclaredMethodsWithAnnotation(bean.getClass(), OnError.class).get(0);
		} catch (RuntimeException ignored) {
		}
	}

	public boolean isRunning() {
		return running.get();
	}

	public void pause() {
		running.set(false);
		log.trace("Pause job: " + jobName + "/" + bean.getClass().getName());
	}

	public void resume() {
		running.set(true);
		log.trace("Resume job: " + jobName + "/" + bean.getClass().getName());
	}

	public String getJobName() {
		return jobName;
	}

	public Object getJobBean() {
		return bean;
	}

	@Override
	public void run() {
		if (!isRunning()) {
			return;
		}
		int n = 0;
		log.trace("Execute job: " + bean.getClass().getName());
		if (onStart != null) {
			try {
				MethodUtils.invokeMethod(bean, onStart);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		Object result = null;
		Throwable cause = null;
		do {
			if (n > 0) {
				log.trace("Retry this job: " + jobName + "/" + bean.getClass().getName());
			}
			try {
				result = MethodUtils.invokeMethod(bean, executable);
				break;
			} catch (Exception e) {
				cause = e;
				log.error(e.getMessage(), e);
			}
		} while (n++ < retries);

		if (result instanceof Boolean && !(Boolean) result) {
			try {
				jobManager.unscheduleJob(jobName);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}

		if (cause != null) {
			if (onError != null) {
				try {
					MethodUtils.invokeMethod(bean, onError, cause);
				} catch (Exception e) {
					if (e instanceof CancellationException) {
						try {
							jobManager.unscheduleJob(jobName);
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
				MethodUtils.invokeMethod(bean, onEnd);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		log.trace("Finish job: " + bean.getClass().getName());
	}

}
