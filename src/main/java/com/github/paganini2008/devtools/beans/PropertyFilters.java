package com.github.paganini2008.devtools.beans;

import java.lang.annotation.Annotation;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.ClassUtils;
import com.github.paganini2008.devtools.MatchMode;

/**
 * 
 * PropertyFilters
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @created 2013-03
 * @version 1.0
 */
public abstract class PropertyFilters {

	public static PropertyFilter isAnnotationPresent(final Class<? extends Annotation>[] annotationClasses) {
		return (name, descriptor) -> {
			for (Class<? extends Annotation> annotationClass : annotationClasses) {
				if (descriptor.getWriteMethod().isAnnotationPresent(annotationClass)
						|| descriptor.getReadMethod().isAnnotationPresent(annotationClass)) {
					return true;
				}
			}
			return false;
		};
	}

	public static PropertyFilter isAnnotationPresent(final Class<? extends Annotation> annotationClass) {
		return (name, descriptor) -> {
			return descriptor.getWriteMethod().isAnnotationPresent(annotationClass)
					|| descriptor.getReadMethod().isAnnotationPresent(annotationClass);
		};
	}

	public static PropertyFilter isAssignable(final Class<?>[] requiredTypes) {
		return (name, descriptor) -> {
			return ClassUtils.isAssignable(requiredTypes, descriptor.getPropertyType());
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

	public static PropertyFilter matches(final String substr, final MatchMode matchMode) {
		return (name, descriptor) -> {
			return matchMode.matches(name, substr);
		};
	}

	public static PropertyFilter propertyTypeContains(final Class<?>[] optional) {
		return (name, descriptor) -> {
			return ArrayUtils.contains(optional, descriptor.getPropertyType());
		};
	}

}
