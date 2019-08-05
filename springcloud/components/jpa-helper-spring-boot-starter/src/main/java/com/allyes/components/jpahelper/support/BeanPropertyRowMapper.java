package com.allyes.components.jpahelper.support;

import com.allyes.developer.utils.collection.Tuple;

/**
 * 
 * BeanPropertyRowMapper
 * 
 * @author Fred Feng
 * @created 2019-04
 */
public class BeanPropertyRowMapper<T> implements RowMapper<T> {

	private final BeanReflection<T> beanReflection;

	public BeanPropertyRowMapper(Class<T> resultClass, String... includedProperties) {
		this.beanReflection = new BeanReflection<T>(resultClass, includedProperties);
	}

	public T mapRow(Tuple tuple) {
		T instance = beanReflection.instantiateBean();
		for (String propertyName : tuple.keySet()) {
			beanReflection.setProperty(instance, propertyName, tuple.get(propertyName));
		}
		return instance;
	}
}
