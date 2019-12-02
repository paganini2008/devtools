package com.github.paganini2008.springworld.quartz;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * 
 * JobBeanAware
 * 
 * @author Fred Feng
 * @created 2019-11
 * @version 1.0
 */
@Component
public class JobBeanAware implements BeanPostProcessor{

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

	}

}
