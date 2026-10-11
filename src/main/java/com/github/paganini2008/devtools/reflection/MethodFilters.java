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
package com.github.paganini2008.devtools.reflection;

import java.lang.annotation.Annotation;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.ClassUtils;
import com.github.paganini2008.devtools.MatchMode;

/**
 * Factory methods of commonly used {@link MethodFilter}s.
 * 
 * @author Fred Feng
 * @date 2019-07-11
 * 
 * 
 * @since 2.0.1
 */
public abstract class MethodFilters {

	/**
	 * Selects methods annotated with any of the given annotations.
	 */
	public static MethodFilter isAnnotationPresent(final Class<? extends Annotation>[] annotationClasses) {
		return (name, method) -> {
			for (Class<? extends Annotation> annotationClass : annotationClasses) {
				if (method.isAnnotationPresent(annotationClass)) {
					return true;
				}
			}
			return false;
		};
	}

	/**
	 * Selects methods annotated with the given annotation.
	 */
	public static MethodFilter isAnnotationPresent(final Class<? extends Annotation> annotationClass) {
		return (name, method) -> {
			return method.isAnnotationPresent(annotationClass);
		};
	}

	/**
	 * Selects methods whose return type is assignable to any of the given types (boxing aware).
	 */
	public static MethodFilter isAssignable(final Class<?>[] returnTypes) {
		return (name, method) -> {
			return ClassUtils.isAssignable(returnTypes, method.getReturnType());
		};
	}

	/**
	 * Selects methods whose return type is assignable to the given type (boxing aware).
	 */
	public static MethodFilter isAssignable(final Class<?> returnType) {
		return (name, method) -> {
			return ClassUtils.isAssignable(returnType, method.getReturnType());
		};
	}

	/**
	 * Selects methods that accept arguments of the given parameter types.
	 */
	public static MethodFilter isMatched(final Class<?>[] parameterTypes) {
		return (name, method) -> {
			return ClassUtils.isAssignable(method.getParameterTypes(), parameterTypes);
		};
	}

	/**
	 * Selects only the methods with the given names.
	 */
	public static MethodFilter include(final String[] propertyNames) {
		return (name, method) -> {
			return ArrayUtils.contains(propertyNames, name);
		};
	}

	/**
	 * Selects all methods except the ones with the given names.
	 */
	public static MethodFilter exclude(final String[] propertyNames) {
		return (name, method) -> {
			return ArrayUtils.notContains(propertyNames, name);
		};
	}

	/**
	 * Selects methods whose name matches the given string in the given {@link MatchMode}.
	 */
	public static MethodFilter matches(final String substr, MatchMode matchMode) {
		return (name, method) -> {
			return matchMode.matches(name, substr);
		};
	}

	/**
	 * Selects methods whose return type is exactly one of the given types.
	 */
	public static MethodFilter returnTypeContains(final Class<?>[] returnTypes) {
		return (name, method) -> {
			return ArrayUtils.contains(returnTypes, method.getReturnType());
		};
	}

}
