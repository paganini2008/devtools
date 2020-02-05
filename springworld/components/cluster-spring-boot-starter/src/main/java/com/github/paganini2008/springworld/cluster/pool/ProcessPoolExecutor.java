package com.github.paganini2008.springworld.cluster.pool;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.paganini2008.springworld.cluster.multicast.ContextMulticastGroup;

/**
 * 
 * ProcessPoolExecutor
 *
 * @author Fred Feng
 * @created 2020-01
 * @revised 2020-02
 * @version 1.0
 */
public class ProcessPoolExecutor implements ProcessPool {

	@Autowired
	private ClusterLatch clusterLatch;

	@Autowired
	private ContextMulticastGroup contextMulticastGroup;

	@Autowired
	private ProcessPoolProperties poolConfig;

	@Autowired
	private WorkQueue workQueue;

	private final AtomicBoolean running = new AtomicBoolean(true);

	@Override
	public void submit(String beanName, Class<?> beanClass, String methodName, Object... arguments) {
		if (!running.get()) {
			throw new IllegalStateException("ProcessPool is shutdown now.");
		}
		SignatureInfo signature = new SignatureInfo(beanName, beanClass.getName(), methodName);
		if (arguments != null) {
			signature.setArguments(arguments);
		}
		boolean acquired = poolConfig.getTimeout() > 0 ? clusterLatch.acquire(poolConfig.getTimeout(), TimeUnit.SECONDS)
				: clusterLatch.acquire();
		if (acquired) {
			contextMulticastGroup.unicast(TOPIC_IDENTITY, signature);
		} else {
			workQueue.push(signature);
		}

	}

	@Override
	public void shutdown() {
		if (!running.get()) {
			return;
		}
		running.set(false);
		workQueue.cleaningForTermination();
		clusterLatch.join();
		
	}

}
