package com.github.paganini2008.devtools.beans.streaming;

import java.util.Map;

import com.github.paganini2008.devtools.beans.BeanUtils;
import com.github.paganini2008.devtools.beans.PropertyFilter;
import com.github.paganini2008.devtools.beans.PropertyFilters;
import com.github.paganini2008.devtools.beans.PropertyUtils;

/**
 * 
 * Transformers
 * 
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public abstract class Transformers {

	public static <E> Transformer<E, Map<String, Object>> asMap() {
		return asMap((PropertyFilter) null);
	}

	public static <E> Transformer<E, Map<String, Object>> asMap(String[] propertyNames) {
		return asMap(PropertyFilters.includedProperties(propertyNames));
	}

	public static <E> Transformer<E, Map<String, Object>> asMap(PropertyFilter propertyFilter) {
		return e -> {
			return PropertyUtils.convertToMap(e, null, propertyFilter);
		};
	}

	public static <E, T> Transformer<E, T> asBean(Class<T> requiredType, String[] propertyNames) {
		return asBean(requiredType, PropertyFilters.includedProperties(propertyNames));
	}

	public static <E, T> Transformer<E, T> asBean(Class<T> requiredType) {
		return asBean(requiredType, (PropertyFilter) null);
	}

	public static <E, T> Transformer<E, T> asBean(Class<T> requiredType, PropertyFilter propertyFilter) {
		return e -> {
			return BeanUtils.copy(e, requiredType, propertyFilter);
		};
	}

}
