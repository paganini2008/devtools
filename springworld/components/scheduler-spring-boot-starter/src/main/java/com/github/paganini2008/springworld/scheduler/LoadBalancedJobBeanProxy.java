package com.github.paganini2008.springworld.scheduler;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.paganini2008.springworld.cluster.multicast.ContextMulticastGroup;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * LoadBalancedJobBeanProxy
 * 
 * @author Fred Feng
 * @created 2019-11
 * @revised 2019-12
 * @version 1.0
 */
@Slf4j
public class LoadBalancedJobBeanProxy implements Runnable, JobRunningControl {

	@Autowired
	private ContextMulticastGroup multicastGroup;

	private final Class<?> jobBeanClass;
	private final String jobBeanName;
	private final AtomicBoolean running;

	public LoadBalancedJobBeanProxy(Class<?> jobBeanClass, String jobBeanName) {
		this.jobBeanClass = jobBeanClass;
		this.jobBeanName = jobBeanName;
		this.running = new AtomicBoolean(true);
	}

	@Override
	public void run() {
		if (!isRunning()) {
			return;
		}
		String sending = jobBeanClass.getName().concat("#").concat(jobBeanName);
		multicastGroup.unicast("loadbalance", sending);
	}

	public boolean isRunning() {
		return running.get();
	}

	public void pause() {
		running.set(false);
		log.trace("Pause job: " + jobBeanName + "/" + jobBeanClass.getName());
	}

	public void resume() {
		running.set(true);
		log.trace("Resume job: " + jobBeanName + "/" + jobBeanClass.getName());
	}
}
