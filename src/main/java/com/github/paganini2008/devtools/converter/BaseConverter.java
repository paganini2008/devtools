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

	private static final Config defaultConfig = new Config();
	private final Map<Class<?>, Converter<?, T>> convertRegistry = new HashMap<Class<?>, Converter<?, T>>();
	protected Config config = defaultConfig;

	public void setConfig(Config config) {
		this.config = config;
	}

	public void put(Class<?> requiredType, Converter<?, T> converter) {
		convertRegistry.put(requiredType, converter);
	}

	public void remove(Class<?> requiredType) {
		convertRegistry.remove(requiredType);
	}

	public Converter<?, T> lookup(Class<?> requiredType) {
		return convertRegistry.get(requiredType);
	}

	public boolean contains(Class<?> requiredType) {
		return convertRegistry.containsKey(requiredType);
	}

	@SuppressWarnings("unchecked")
	public T getValue(Object value, T defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		Class<?> match = getAssignableClass(value.getClass());
		if (match != null) {
			Converter<Object, T> converter = (Converter<Object, T>) lookup(match);
			return converter.getValue(value, defaultValue);
		}
		return defaultValue;
	}

	protected Class<?> getAssignableClass(Class<?> src) {
		for (Class<?> type : convertRegistry.keySet()) {
			if (type.equals(src) || type.isAssignableFrom(src)) {
				return type;
			}
		}
		return null;
	}

}
