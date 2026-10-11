/**
* Copyright 2017-2026 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.paganini2008.devtools.beans;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.ClassUtils;
import com.github.paganini2008.devtools.MatchMode;
import com.github.paganini2008.devtools.reflection.FieldUtils;

/**
 * Factory methods of commonly used {@link PropertyFilter}s.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class PropertyFilters {

	/**
	 * Accepts properties whose backing field is annotated with any of the given annotations. Properties without a
	 * backing field are rejected.
	 */
	public static PropertyFilter isAnnotationPresent(final Class<? extends Annotation>[] annotationClasses) {
		return (type, name, descriptor) -> {
			Field field = FieldUtils.getFieldIfAbsent(type, name);
			if (field == null) {
				return false;
			}
			for (Class<? extends Annotation> annotationClass : annotationClasses) {
				if (field.isAnnotationPresent(annotationClass)) {
					return true;
				}
			}
			return false;
		};
	}

	/**
	 * Accepts properties whose backing field is annotated with the given annotation.
	 */
	public static PropertyFilter isAnnotationPresent(final Class<? extends Annotation> annotationClass) {
		return (type, name, descriptor) -> {
			Field field = FieldUtils.getFieldIfAbsent(type, name);
			return field != null && field.isAnnotationPresent(annotationClass);
		};
	}

	/**
	 * Accepts properties whose type is assignable to any of the given types (boxing aware).
	 */
	public static PropertyFilter isAssignable(final Class<?>[] requiredTypes) {
		return (type, name, descriptor) -> {
			return ClassUtils.isAssignable(requiredTypes, descriptor.getPropertyType());
		};
	}

	/**
	 * Accepts properties whose type is assignable to the given type (boxing aware).
	 */
	public static <T> PropertyFilter isAssignable(final Class<T> requiredType) {
		return (type, name, descriptor) -> {
			return ClassUtils.isAssignable(requiredType, descriptor.getPropertyType());
		};
	}

	/**
	 * Accepts only the properties with the given names.
	 */
	public static PropertyFilter includedProperties(final String[] propertyNames) {
		return (type, name, descriptor) -> {
			return ArrayUtils.contains(propertyNames, name);
		};
	}

	/**
	 * Accepts all properties except the ones with the given names.
	 */
	public static PropertyFilter excludedProperties(final String[] propertyNames) {
		return (type, name, descriptor) -> {
			return ArrayUtils.notContains(propertyNames, name);
		};
	}

	/**
	 * Accepts properties whose name matches the given string in the given {@link MatchMode}.
	 */
	public static PropertyFilter matches(final String substr, final MatchMode matchMode) {
		return (type, name, descriptor) -> {
			return matchMode.matches(name, substr);
		};
	}

	/**
	 * Accepts properties whose type is exactly one of the given types.
	 */
	public static PropertyFilter propertyTypeContains(final Class<?>[] optional) {
		return (type, name, descriptor) -> {
			return ArrayUtils.contains(optional, descriptor.getPropertyType());
		};
	}

}
