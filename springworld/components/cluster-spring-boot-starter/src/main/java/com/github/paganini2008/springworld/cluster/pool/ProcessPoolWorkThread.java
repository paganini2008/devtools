package com.github.paganini2008.springworld.cluster.pool;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.paganini2008.devtools.ClassUtils;
import com.github.paganini2008.devtools.reflection.MethodUtils;
import com.github.paganini2008.springworld.cluster.multicast.ContextMulticastEventHandler;
import com.github.paganini2008.springworld.cluster.utils.ApplicationContextUtils;
import com.github.paganini2008.springworld.redis.concurrents.SharedLatch;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * ProcessPoolWorkThread
 *
 * @author Fred Feng
 * @created 2020-01
 * @revised 2020-02
 * @version 1.0
 */
@Slf4j
public class ProcessPoolWorkThread implements ContextMulticastEventHandler {

	@Autowired
	private WorkQueue workQueue;

	@Autowired
	private ProcessPool processPool;

	@Autowired
	private SharedLatch sharedLatch;

	@Autowired
	private InvocationResult invocationResult;

	@Override
	public void onMessage(String clusterId, Object message) {
		Object bean = null;
		Object result = null;
		Signature signature = null;
		try {
			signature = (Signature) message;
			bean = ApplicationContextUtils.getBean(signature.getBeanName(), ClassUtils.forName(signature.getBeanClassName()));
			if (bean != null) {
				invocationResult.setCompleted();
				result = MethodUtils.invokeMethod(bean, signature.getMethodName(), signature.getArguments());
				MethodUtils.invokeMethodWithAnnotation(bean, OnSuccess.class, signature, result);
			} else {
				log.warn("No bean registered in spring context to call the signature: " + signature);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			MethodUtils.invokeMethodWithAnnotation(bean, OnFailure.class, signature, e);
		} finally {
			sharedLatch.release();

			signature = workQueue.pop();
			if (signature != null) {
				processPool.submit(signature.getBeanName(), ClassUtils.forName(signature.getBeanClassName()), signature.getMethodName(),
						signature.getArguments());
			}
		}
	}

	@Override
	public String getTopic() {
		return ProcessPool.TOPIC_IDENTITY;
	}

}
