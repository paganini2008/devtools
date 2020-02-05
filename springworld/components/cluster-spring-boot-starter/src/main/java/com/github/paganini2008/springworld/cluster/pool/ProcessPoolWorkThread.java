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

	@Override
	public void onMessage(String clusterId, Object message) {
		Signature signature;
		try {
			signature = (Signature) message;
			Object bean = ApplicationContextUtils.getBean(signature.getBeanName(), ClassUtils.forName(signature.getBeanClassName()));
			if (bean == null) {
				log.warn("No bean registered in spring context to call the signature: " + signature);
				return;
			}
			Object result = MethodUtils.invokeMethod(bean, signature.getMethodName(), signature.getArguments());
			log.info("result: " + result);
		} finally {
			signature = workQueue.pop();
			processPool.execute(signature.getBeanName(), ClassUtils.forName(signature.getBeanClassName()), signature.getMethodName(),
					signature.getArguments());
		}
	}

	@Override
	public String getTopic() {
		return ProcessPool.TOPIC_IDENTITY;
	}

}
