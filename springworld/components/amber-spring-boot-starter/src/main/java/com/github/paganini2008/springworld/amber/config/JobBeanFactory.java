package com.github.paganini2008.springworld.amber.config;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 
 * JobBeanFactory
 * 
 * @author Fred Feng
 * @create 2018-03
 */
@Component
public class JobBeanFactory implements ApplicationContextAware {

	private ApplicationContext ctx;

	private AutowireCapableBeanFactory beanFactory;

	public Object getBean(Class<?> representation) {
		try {
			return ctx.getBean(representation);
		} catch (BeansException e) {
			Object target;
			try {
				target = ConstructorUtils.invokeConstructor(representation);
			} catch (Exception ignored) {
				throw new JobBeanNotFoundException(representation);
			}
			beanFactory.autowireBean(target);
			return target;
		}
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.ctx = applicationContext;
		this.beanFactory = applicationContext.getAutowireCapableBeanFactory();
	}
}
