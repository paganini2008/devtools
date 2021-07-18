/**
* Copyright 2018-2021 Fred Feng (paganini.fy@gmail.com)

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
 * 
 * PropertyFilters
 * 
 * @author Fred Feng
 * @version 1.0
 */
public abstract class PropertyFilters {

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

	public static PropertyFilter isAnnotationPresent(final Class<? extends Annotation> annotationClass) {
		return (type, name, descriptor) -> {
			Field field = FieldUtils.getFieldIfAbsent(type, name);
			return field != null && field.isAnnotationPresent(annotationClass);
		};
	}

	public static PropertyFilter isAssignable(final Class<?>[] requiredTypes) {
		return (type, name, descriptor) -> {
			return ClassUtils.isAssignable(requiredTypes, descriptor.getPropertyType());
		};
	}

	public static <T> PropertyFilter isAssignable(final Class<T> requiredType) {
		return (type, name, descriptor) -> {
			return ClassUtils.isAssignable(requiredType, descriptor.getPropertyType());
		};
	}

	public static PropertyFilter includedProperties(final String[] propertyNames) {
		return (type, name, descriptor) -> {
			return ArrayUtils.contains(propertyNames, name);
		};
	}

	public static PropertyFilter excludedProperties(final String[] propertyNames) {
		return (type, name, descriptor) -> {
			return ArrayUtils.notContains(propertyNames, name);
		};
	}

	public static PropertyFilter matches(final String substr, final MatchMode matchMode) {
		return (type, name, descriptor) -> {
			return matchMode.matches(name, substr);
		};
	}

	public static PropertyFilter propertyTypeContains(final Class<?>[] optional) {
		return (type, name, descriptor) -> {
			return ArrayUtils.contains(optional, descriptor.getPropertyType());
		};
	}

}
