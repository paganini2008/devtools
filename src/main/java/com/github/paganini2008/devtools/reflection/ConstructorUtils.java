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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.ClassUtils;

/**
 * Utilities for locating and invoking constructors (including non-public ones) by argument types.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class ConstructorUtils {

	/**
	 * Invokes the constructor best matching the runtime classes of the arguments. A null argument matches any reference
	 * parameter type.
	 * 
	 * @throws ReflectionException if no constructor matches or the invocation fails
	 */
	public static <T> T invokeConstructor(Class<T> cl, Object... arugments) {
		if (arugments == null) {
			arugments = ArrayUtils.EMPTY_OBJECT_ARRAY;
		}
		Class<?>[] parameterTypes = new Class<?>[arugments.length];
		for (int i = 0; i < parameterTypes.length; i++) {
			parameterTypes[i] = arugments[i] != null ? arugments[i].getClass() : null;
		}
		return invokeConstructor(cl, parameterTypes, arugments);
	}

	/**
	 * Invokes the constructor best matching the given parameter types.
	 * 
	 * @throws ReflectionException if no constructor matches or the invocation fails
	 */
	public static <T> T invokeConstructor(Class<T> cl, Class<?>[] parameterTypes, Object... arugments) {
		Constructor<T> method = getConstructor(cl, parameterTypes);
		return invokeConstructor(method, arugments);
	}

	/**
	 * Invokes the constructor, making it accessible first.
	 * 
	 * @throws ReflectionException if the invocation fails
	 */
	public static <T> T invokeConstructor(Constructor<T> method, Object... arugments) {
		if (arugments == null) {
			arugments = ArrayUtils.EMPTY_OBJECT_ARRAY;
		}
		try {
			method.setAccessible(true);
			return method.newInstance(arugments);
		} catch (Exception e) {
			throw new ReflectionException("Invoke constructor failed by name: " + method.getName(), e);
		}
	}

	/**
	 * Same as {@link #getConstructor(Class, Class...)} but returns null instead of throwing.
	 */
	public static <T> Constructor<T> getConstructorIfAbsent(Class<T> type, Class<?>... parameterTypes) {
		try {
			return getConstructor(type, parameterTypes);
		} catch (RuntimeException e) {
			return null;
		}
	}

	/**
	 * Finds the declared constructor with exactly the given parameter types, or else the most specific one accepting
	 * arguments of these types (boxing aware).
	 * 
	 * @throws IllegalArgumentException if the type is null
	 * @throws ReflectionException if no constructor matches
	 */
	public static <T> Constructor<T> getConstructor(Class<T> type, Class<?>... parameterTypes) {
		Assert.isNull(type, "Class must not be null.");
		if (parameterTypes == null) {
			parameterTypes = ClassUtils.EMPTY_ARRAY;
		}
		Throwable cause;
		try {
			return type.getDeclaredConstructor(parameterTypes);
		} catch (Exception e) {
			cause = e;
		}
		if (cause instanceof NoSuchMethodException) {
			try {
				return searchConstructor(type, type.getDeclaredConstructors(), parameterTypes);
			} catch (NoSuchMethodException e) {
				cause = e;
			}
		}
		try {
			return type.getConstructor(parameterTypes);
		} catch (Exception e) {
			cause = e;
		}
		if (cause instanceof NoSuchMethodException) {
			try {
				return searchConstructor(type, type.getConstructors(), parameterTypes);
			} catch (NoSuchMethodException e) {
				cause = e;
			}
		}
		throw new ReflectionException(cause);
	}

	@SuppressWarnings("unchecked")
	private static <T> Constructor<T> searchConstructor(Class<T> cls, Constructor<?>[] methods, Class<?>[] parameterTypes)
			throws NoSuchMethodException {
		if (methods != null) {
			List<Constructor<T>> candidates = new ArrayList<Constructor<T>>();
			for (Constructor<?> constructor : methods) {
				if (MethodUtils.isCompatible(constructor.getParameterTypes(), parameterTypes)) {
					candidates.add((Constructor<T>) constructor);
				}
			}
			if (!candidates.isEmpty()) {
				Constructor<T> bestMatch = candidates.get(0);
				for (Constructor<T> constructor : candidates) {
					if (ClassUtils.equals(constructor.getParameterTypes(), parameterTypes)) {
						return constructor;
					}
					if (MethodUtils.isMoreSpecific(constructor.getParameterTypes(), bestMatch.getParameterTypes())) {
						bestMatch = constructor;
					}
				}
				return bestMatch;
			}
		}
		throw new NoSuchMethodException("No matched constructor: " + cls.getSimpleName() + "(" + ArrayUtils.toString(parameterTypes)
				+ ") on class: " + cls.getName());
	}

}
