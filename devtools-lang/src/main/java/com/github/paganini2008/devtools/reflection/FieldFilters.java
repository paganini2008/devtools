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
 * 
 * 
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
