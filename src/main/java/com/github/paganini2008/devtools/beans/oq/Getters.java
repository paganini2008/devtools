package com.github.paganini2008.devtools.beans.oq;

import java.util.Map;

import com.github.paganini2008.devtools.beans.Getter;
import com.github.paganini2008.devtools.beans.PropertyUtils;
import com.github.paganini2008.devtools.converter.ConvertUtils;

/**
 * Extractors
 * 
 * @author Fred Feng
 * @version 1.0
 */
@SuppressWarnings("all")
public class Getters {

	private Getters() {
	}

	public static <E, T> Getter<E, T> forObject(final String property) {
		return new Getter<E, T>() {
			public T apply(E entity) {
				return (T) PropertyUtils.getProperty(entity, property);
			}
		};
	}

	public static <E, T> Getter<E, T> forObject(final String property, final Class<T> requiredType) {
		return new Getter<E, T>() {
			public T apply(E entity) {
				Object result = PropertyUtils.getProperty(entity, property);
				return ConvertUtils.convertValue(result, requiredType);
			}
		};
	}

	public static <E> Getter<E, Object[]> forObject(final String[] properties) {
		return new Getter<E, Object[]>() {
			public Object[] apply(E entity) {
				Object[] values = new Object[properties.length];
				for (int i = 0; i < values.length; i++) {
					values[i] = PropertyUtils.getProperty(entity, properties[i]);
				}
				return values;
			}
		};
	}

	public static Getter<Map, Object> forMap(final Object key) {
		return new Getter<Map, Object>() {
			public Object apply(Map entity) {
				return entity.get(key);
			}
		};
	}

	public static <T> Getter<Map, T> forMap(final Object key, final Class<T> requiredType) {
		return new Getter<Map, T>() {
			public T apply(Map entity) {
				Object result = entity.get(key);
				return ConvertUtils.convertValue(result, requiredType);
			}
		};
	}

	public static Getter<Map, Object[]> forMap(final Object[] keys) {
		return new Getter<Map, Object[]>() {
			public Object[] apply(Map entity) {
				Object[] values = new Object[keys.length];
				for (int i = 0; i < values.length; i++) {
					values[i] = entity.get(keys[i]);
				}
				return values;
			}
		};
	}

}
