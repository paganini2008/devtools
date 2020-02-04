package com.github.paganini2008.springworld.cluster.pool;

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

	@Override
	public void onMessage(String clusterId, Object message) {
		SignatureInfo signatureInfo = (SignatureInfo) message;
		Object bean = ApplicationContextUtils.getBean(signatureInfo.getBeanName(), ClassUtils.forName(signatureInfo.getBeanClassName()));
		if (bean == null) {
			log.warn("No bean registered in spring context to call the signature: " + signatureInfo);
			return;
		}
		Object result = MethodUtils.invokeMethod(bean, signatureInfo.getMethodName(), signatureInfo.getArguments());
		log.info("result: " + result);
	}

	@Override
	public String getTopic() {
		return ProcessPoolExecutor.TOPIC_IDENTITY;
	}

}
