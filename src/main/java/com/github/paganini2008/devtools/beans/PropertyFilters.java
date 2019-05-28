package com.github.paganini2008.devtools.beans;

import java.beans.PropertyDescriptor;

import com.github.paganini2008.devtools.ArrayUtils;

/**
 * PropertyFilters
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class PropertyFilters {

	private PropertyFilters() {
	}

	public static <T> PropertyFilter isAssignableFrom(final Class<T> requiredType) {
		return new PropertyFilter() {
			public boolean accept(String name, PropertyDescriptor descriptor) {
				return descriptor.getPropertyType().isAssignableFrom(requiredType);
			}
		};
	}

	public static PropertyFilter include(final String[] propertyNames) {
		return new PropertyFilter() {
			public boolean accept(String name, PropertyDescriptor descriptor) {
				return ArrayUtils.contains(propertyNames, name);
			}
		};
	}

	public static PropertyFilter exclude(final String[] propertyNames) {
		return new PropertyFilter() {
			public boolean accept(String name, PropertyDescriptor descriptor) {
				return ArrayUtils.notContains(propertyNames, name);
			}
		};
	}

	public static PropertyFilter matches(final String regex) {
		return new PropertyFilter() {
			public boolean accept(String name, PropertyDescriptor descriptor) {
				return name.matches(regex);
			}
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
		return new PropertyFilter() {
			public boolean accept(String name, PropertyDescriptor descriptor) {
				return name.endsWith(suffix);
			}
		};
	}

	public static PropertyFilter contains(final String substr) {
		return new PropertyFilter() {
			public boolean accept(String name, PropertyDescriptor descriptor) {
				return name.contains(substr);
			}
		};
	}

}
