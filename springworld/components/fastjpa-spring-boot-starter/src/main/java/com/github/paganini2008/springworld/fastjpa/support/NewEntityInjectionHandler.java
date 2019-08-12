package com.github.paganini2008.springworld.fastjpa.support;

import com.github.paganini2008.devtools.reflection.ConstructorUtils;

/**
 * 
 * NewEntityInjectionHandler
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-03
 * @version 1.0
 */
public class NewEntityInjectionHandler implements InjectionHandler {

	public Object inject(Object original, String targetProperty, Class<?> targetPropertyType) {
		final Long id = (Long) original;
		return id != null ? instantiateBean(targetPropertyType, id) : null;
	}

	protected Object instantiateBean(Class<?> targetPropertyType, Long id) {
		return ConstructorUtils.invokeConstructor(targetPropertyType, id);
	}

}
