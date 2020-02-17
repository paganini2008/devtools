package com.github.paganini2008.devtools.beans.streaming;

import java.util.Map;
import java.util.function.Function;

import com.github.paganini2008.devtools.converter.ConvertUtils;

/**
 * 
 * MappedBy
 * 
 * @author Fred Feng
 * 
 * @version 1.0
 */
public class MappedBy<T> implements Function<Map<String, Object>, T> {

	private final String name;
	private final Class<T> requiredType;

	MappedBy(String name, Class<T> requiredType) {
		this.name = name;
		this.requiredType = requiredType;
	}

	public T apply(Map<String, Object> m) {
		Object value = m.get(name);
		try {
			return requiredType.cast(value);
		} catch (RuntimeException e) {
			return ConvertUtils.convertValue(value, requiredType);
		}
	}

	public static <T> MappedBy<T> forName(String name, Class<T> requiredType) {
		return new MappedBy<T>(name, requiredType);
	}

}
