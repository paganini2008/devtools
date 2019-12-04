package com.github.paganini2008.springworld.scheduler;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;

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
public class JobBeanProxy implements Runnable, JobRunningControl {

	private final Object jobBean;
	private final String jobBeanName;
	private final int retries;

	public JobBeanProxy(Object jobBean, String jobBeanName, int retries) {
		this.jobBean = jobBean;
		this.jobBeanName = jobBeanName;
		this.retries = retries;
		this.running = new AtomicBoolean(true);
		reflectJobBean();
	}

	@Autowired
	private JobManager jobManager;

	private Method onStart;
	private Method onEnd;
	private Method onError;
	private Method executable;
	private final AtomicBoolean running;

	private void reflectJobBean() {
		try {
			executable = MethodUtils.getDeclaredMethodsWithAnnotation(jobBean.getClass(), Executable.class).get(0);
		} catch (RuntimeException e) {
			throw new IllegalStateException("Executable method is required.", e);
		}
		try {
			onStart = MethodUtils.getDeclaredMethodsWithAnnotation(jobBean.getClass(), OnStart.class).get(0);
		} catch (RuntimeException ignored) {
		}
		try {
			onEnd = MethodUtils.getDeclaredMethodsWithAnnotation(jobBean.getClass(), OnEnd.class).get(0);
		} catch (RuntimeException ignored) {
		}
		try {
			onError = MethodUtils.getDeclaredMethodsWithAnnotation(jobBean.getClass(), OnError.class).get(0);
		} catch (RuntimeException ignored) {
		}
	}

	public boolean isRunning() {
		return running.get();
	}

	public void pause() {
		running.set(false);
		log.trace("Pause job: " + jobBeanName + "/" + jobBean.getClass().getName());
	}

	public void resume() {
		running.set(true);
		log.trace("Resume job: " + jobBeanName + "/" + jobBean.getClass().getName());
	}

	public String getJobName() {
		return jobBeanName;
	}

	public Object getJobBean() {
		return jobBean;
	}

	@Override
	public void run() {
		if (!isRunning()) {
			return;
		}
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
