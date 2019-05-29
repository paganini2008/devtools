package com.github.paganini2008.devtools.converter;

import java.util.HashMap;
import java.util.Map;

/**
 * BaseConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public abstract class BaseConverter<T> implements Converter<Object, T>, ConverterManager<T> {

	private final Map<Class<?>, Converter<?, T>> converterRegistry = new HashMap<Class<?>, Converter<?, T>>();
	protected Config config = new Config();

	public void setConfig(Config config) {
		this.config = config;
	}

	public Config getConfig() {
		return config;
	}

	public void put(Class<?> requiredType, Converter<?, T> converter) {
		converterRegistry.put(requiredType, converter);
	}

	public void remove(Class<?> requiredType) {
		converterRegistry.remove(requiredType);
	}

	public Converter<?, T> lookup(Class<?> requiredType) {
		return converterRegistry.get(requiredType);
	}

	public boolean contains(Class<?> requiredType) {
		return converterRegistry.containsKey(requiredType);
	}

	@SuppressWarnings("unchecked")
	public T getValue(Object value, T defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		final Class<?> match = getAssignableClass(value.getClass());
		if (match != null) {
			Converter<Object, T> converter = (Converter<Object, T>) lookup(match);
			return converter.getValue(value, defaultValue);
		}
		return defaultValue;
	}

	protected Class<?> getAssignableClass(Class<?> actual) {
		for (Class<?> type : converterRegistry.keySet()) {
			if (type.equals(actual) || type.isAssignableFrom(actual)) {
				return type;
			}
		}
		return null;
	}

}
