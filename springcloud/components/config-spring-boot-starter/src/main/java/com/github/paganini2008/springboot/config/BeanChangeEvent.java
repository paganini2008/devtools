package com.github.paganini2008.springboot.config;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.context.ApplicationEvent;

/**
 * 
 * BeanChangeEvent
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-03
 * @version 1.0
 */
public class BeanChangeEvent extends ApplicationEvent {

	private static final long serialVersionUID = 4317527666421114782L;

	public BeanChangeEvent(Object bean, String beanName) {
		super(bean);
		this.beanName = beanName;
	}

	private final String beanName;

	public String getBeanName() {
		return beanName;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
