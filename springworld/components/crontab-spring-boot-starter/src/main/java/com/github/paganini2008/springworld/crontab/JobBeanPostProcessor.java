package com.github.paganini2008.springworld.crontab;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * 
 * JobBeanPostProcessor
 *
 * @author Fred Feng
 * @created 2018-03
 * @revised 2019-08
 * @version 1.0
 */
@Component
public class JobBeanPostProcessor implements BeanPostProcessor {

	@Autowired
	private JobManager jobManager;

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof Job) {
			jobManager.schedule((Job) bean);
		}
		return bean;
	}

}
