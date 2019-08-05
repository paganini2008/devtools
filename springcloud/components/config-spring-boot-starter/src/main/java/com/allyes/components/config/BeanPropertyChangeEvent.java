package com.allyes.components.config;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.context.ApplicationEvent;

/**
 * 
 * BeanPropertyChangeEvent
 * 
 * @author Fred Feng
 * @created 2019-03
 */
public class BeanPropertyChangeEvent extends ApplicationEvent {

	private static final long serialVersionUID = 8898079156176355924L;

	public BeanPropertyChangeEvent(Object bean, String beanName, String propertyName, Object previousValue, Object currentValue) {
		super(bean);
		this.beanName = beanName;
		this.propertyName = propertyName;
		this.previousValue = previousValue;
		this.currentValue = currentValue;
	}

	private final String beanName;
	private final String propertyName;
	private final Object previousValue;
	private final Object currentValue;

	public String getBeanName() {
		return beanName;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public Object getPreviousValue() {
		return previousValue;
	}

	public Object getCurrentValue() {
		return currentValue;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
