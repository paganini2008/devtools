/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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
 * MethodFilters
 * 
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public abstract class MethodFilters {

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

	public static MethodFilter isAnnotationPresent(final Class<? extends Annotation> annotationClass) {
		return (name, method) -> {
			return method.isAnnotationPresent(annotationClass);
		};
	}

	public static MethodFilter isAssignable(final Class<?>[] returnTypes) {
		return (name, method) -> {
			return ClassUtils.isAssignable(returnTypes, method.getReturnType());
		};
	}

	public static MethodFilter isAssignable(final Class<?> returnType) {
		return (name, method) -> {
			return ClassUtils.isAssignable(returnType, method.getReturnType());
		};
	}

	public static MethodFilter isMatched(final Class<?>[] parameterTypes) {
		return (name, method) -> {
			return ClassUtils.isAssignable(method.getParameterTypes(), parameterTypes);
		};
	}

	public static MethodFilter include(final String[] propertyNames) {
		return (name, method) -> {
			return ArrayUtils.contains(propertyNames, name);
		};
	}

	public static MethodFilter exclude(final String[] propertyNames) {
		return (name, method) -> {
			return ArrayUtils.notContains(propertyNames, name);
		};
	}

	public static MethodFilter matches(final String substr, MatchMode matchMode) {
		return (name, method) -> {
			return matchMode.matches(name, substr);
		};
	}

	public static MethodFilter returnTypeContains(final Class<?>[] returnTypes) {
		return (name, method) -> {
			return ArrayUtils.contains(returnTypes, method.getReturnType());
		};
	}

}
