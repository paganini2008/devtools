package com.github.paganini2008.devtools.beans;

import java.beans.PropertyDescriptor;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.ClassUtils;

/**
 * PropertyFilters
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class PropertyFilters {

	private PropertyFilters() {
	}

	public static PropertyFilter isAssignable(final Class<?>[] optional) {
		return (name, descriptor) -> {
			return ClassUtils.isAssignable(optional, descriptor.getPropertyType());
		};
	}

	public static <T> PropertyFilter isAssignable(final Class<T> requiredType) {
		return (name, descriptor) -> {
			return ClassUtils.isAssignable(requiredType, descriptor.getPropertyType());
		};
	}

	public static PropertyFilter include(final String[] propertyNames) {
		return (name, descriptor) -> {
			return ArrayUtils.contains(propertyNames, name);
		};
	}

	public static PropertyFilter exclude(final String[] propertyNames) {
		return (name, descriptor) -> {
			return ArrayUtils.notContains(propertyNames, name);
		};
	}

	public static PropertyFilter matches(final String regex) {
		return (name, descriptor) -> {
			return name.matches(regex);
		};
	}

	public static PropertyFilter startsWith(final String prefix) {
		return new PropertyFilter() {
			public boolean accept(String name, PropertyDescriptor descriptor) {
				return name.startsWith(prefix);
			}
		};
	}

	public static PropertyFilter endsWith(final String suffix) {
		return (name, descriptor) -> {
			return name.endsWith(suffix);
		};
	}

	public static PropertyFilter substr(final String substr) {
		return (name, descriptor) -> {
			return name.contains(substr);
		};
	}

	public static PropertyFilter contains(final Class<?>[] optional) {
		return (name, descriptor) -> {
			return ArrayUtils.contains(optional, descriptor.getPropertyType());
		};
	}

}
