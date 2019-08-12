package com.github.paganini2008.springworld.fastjpa.support;

/**
 * 
 * InjectionHandler
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-03
 * @version 1.0
 */
public interface InjectionHandler {

	Object inject(Object original, String targetProperty, Class<?> targetPropertyType);

}
