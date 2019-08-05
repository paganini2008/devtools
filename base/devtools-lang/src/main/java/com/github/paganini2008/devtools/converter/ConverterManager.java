package com.github.paganini2008.devtools.converter;

/**
 * ConverterManager
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface ConverterManager<T> {
	
	void setConfig(Config config);
	
	Config getConfig();

	void put(Class<?> requiredType, Converter<?, T> converter);

	void remove(Class<?> requiredType);

	Converter<?, T> lookup(Class<?> requiredType);

	boolean contains(Class<?> requiredType);

}