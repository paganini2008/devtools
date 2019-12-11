package com.github.paganini2008.springworld.cluster.implementor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * 
 * ContextMulticastEventHandlerBeanProcessor
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
@Component
public class ContextMulticastEventHandlerBeanProcessor implements BeanPostProcessor {

	@Autowired
	private ContextMulticastEventListener multicastEventListener;

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof ContextMulticastEventHandler) {
			multicastEventListener.addHandler((ContextMulticastEventHandler) bean);
		}
		return bean;
	}

}
