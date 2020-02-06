package com.github.paganini2008.springworld.cluster.pool;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.paganini2008.devtools.ClassUtils;
import com.github.paganini2008.devtools.reflection.MethodUtils;
import com.github.paganini2008.springworld.cluster.multicast.ContextMulticastEventHandler;
import com.github.paganini2008.springworld.cluster.utils.ApplicationContextUtils;

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
	private ClusterLatch clusterLatch;
	
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
				if (bean instanceof FutureCallback) {
					((FutureCallback) bean).onSuccess(signature,result);
				}
			} else {
				log.warn("No bean registered in spring context to call the signature: " + signature);
			}
		} catch (Exception e) {
			if (bean instanceof FutureCallback) {
				((FutureCallback) bean).onFailure(signature,e);
			} else {
				if (e instanceof NoSuchMethodException) {
					log.warn("No method for name " + signature.getMethodName()
							+ ", please add a new method, which from the original method and start with 'do' to invoke. ");
				} else {
					log.error(e.getMessage(), e);
				}
			}
		} finally {
			clusterLatch.release();

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
