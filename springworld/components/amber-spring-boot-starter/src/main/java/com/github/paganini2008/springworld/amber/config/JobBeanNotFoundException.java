package com.github.paganini2008.springworld.amber.config;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;

/**
 * 
 * JobBeanNotFoundException
 * 
 * @author Fred Feng
 * @create 2018-03
 */
public class JobBeanNotFoundException extends NoSuchBeanDefinitionException {

	private static final long serialVersionUID = -5524855755241738741L;

	public JobBeanNotFoundException(Class<?> type) {
		super(type);
	}

}
