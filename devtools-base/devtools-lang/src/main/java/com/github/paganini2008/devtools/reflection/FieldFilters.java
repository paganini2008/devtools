package com.github.paganini2008.devtools.reflection;

import java.lang.annotation.Annotation;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.ClassUtils;
import com.github.paganini2008.devtools.MatchMode;

/**
 * 
 * FieldFilters
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-02
 * @version 1.0
 */
public abstract class FieldFilters {

	public static FieldFilter isAnnotationPresent(final Class<? extends Annotation>[] annotationClasses) {
		return (name, field) -> {
			for (Class<? extends Annotation> annotationClass : annotationClasses) {
				if (field.isAnnotationPresent(annotationClass)) {
					return true;
				}
			}
			return false;
		};
	}

	public static FieldFilter isAnnotationPresent(final Class<? extends Annotation> annotationClass) {
		return (name, field) -> {
			return field.isAnnotationPresent(annotationClass);
		};
	}

	public static FieldFilter isAssignable(final Class<?>[] optional) {
		return (name, field) -> {
			return ClassUtils.isAssignable(optional, field.getType());
		};
	}

	public static FieldFilter isAssignable(final Class<?> requiredType) {
		return (name, field) -> {
			return ClassUtils.isAssignable(requiredType, field.getType());
		};
	}

	public static FieldFilter include(final String[] propertyNames) {
		return (name, field) -> {
			return ArrayUtils.contains(propertyNames, name);
		};
	}

	public static FieldFilter exclude(final String[] propertyNames) {
		return (name, field) -> {
			return ArrayUtils.notContains(propertyNames, name);
		};
	}

	public static FieldFilter matches(final String substr, MatchMode matchMode) {
		return (name, field) -> {
			return matchMode.matches(name, substr);
		};
	}

	public static FieldFilter typeContains(final Class<?>[] optional) {
		return (name, field) -> {
			return ArrayUtils.contains(optional, field.getType());
		};
	}
}
