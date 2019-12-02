package com.github.paganini2008.springworld.scheduler;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.data.redis.support.atomic.RedisAtomicInteger;

import com.github.paganini2008.devtools.reflection.MethodUtils;

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

	JobBeanProxy(Object bean, String jobName,int retries, JobManager jobManager) {
		this.bean = bean;
		this.jobName = jobName;
		this.retries = retries;
		this.jobManager = jobManager;
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

	@Override
	public void run() {
		if (!isRunning()) {
			return;
		}
		lastExecuted = System.currentTimeMillis();
		int n = 0;
		log.trace("Execute job: " + bean.getClass().getName());
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
		log.trace("Finish job: " + bean.getClass().getName());
	}

}
