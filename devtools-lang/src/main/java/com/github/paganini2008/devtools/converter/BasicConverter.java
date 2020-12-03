package com.github.paganini2008.devtools.converter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * BasicConverter
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public abstract class BasicConverter<T> implements Converter<Object, T> {

	private final Map<Class<?>, Converter<?, T>> converterRegistry = new ConcurrentHashMap<Class<?>, Converter<?, T>>();

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
		final Class<?> matches = getAssignableClass(value.getClass());
		if (matches != null) {
			Converter<Object, T> converter = (Converter<Object, T>) lookupType(matches);
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
