package com.github.paganini2008.devtools.converter;

import java.util.HashMap;
import java.util.Map;

/**
 * BasicConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public abstract class BasicConverter<T> implements Converter<Object, T> {

	private final Map<Class<?>, Converter<?, T>> converterRegistry = new HashMap<Class<?>, Converter<?, T>>();
	protected ConverterConfig config = new ConverterConfig();

	public void setConfig(ConverterConfig config) {
		this.config = config;
	}

	public ConverterConfig getConfig() {
		return config;
	}

	public void registerType(Class<?> javaType, Converter<?, T> converter) {
		converterRegistry.put(javaType, converter);
	}

	public void removeType(Class<?> javaType) {
		converterRegistry.remove(javaType);
	}

	public Converter<?, T> lookupType(Class<?> javaType) {
		return converterRegistry.get(javaType);
	}

	public boolean hasType(Class<?> javaType) {
		return converterRegistry.containsKey(javaType);
	}

	@SuppressWarnings("unchecked")
	public T convertValue(Object value, T defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		final Class<?> match = getAssignableClass(value.getClass());
		if (match != null) {
			Converter<Object, T> converter = (Converter<Object, T>) lookupType(match);
			return converter.convertValue(value, defaultValue);
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
