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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.ClassUtils;

/**
 * 
 * ConstructorUtils
 * 
 * @author Fred Feng
 * @version 1.0
 */
public abstract class ConstructorUtils {

	public static <T> T invokeConstructor(Class<T> cl, Object... arugments) {
		if (arugments == null) {
			arugments = ArrayUtils.EMPTY_OBJECT_ARRAY;
		}
		Class<?>[] parameterTypes = new Class<?>[arugments.length];
		for (int i = 0; i < parameterTypes.length; i++) {
			if (arugments[i] == null) {
				throw new IllegalArgumentException("arugments[" + i + "] is a null parameter");
			}
			parameterTypes[i] = arugments[i].getClass();
		}
		return invokeConstructor(cl, parameterTypes, arugments);
	}

	public static <T> T invokeConstructor(Class<T> cl, Class<?>[] parameterTypes, Object... arugments) {
		Constructor<T> method = getConstructor(cl, parameterTypes);
		return invokeConstructor(method, arugments);
	}

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

	public static <T> Constructor<T> getConstructorIfAbsent(Class<T> type, Class<?>... parameterTypes) {
		try {
			return getConstructor(type, parameterTypes);
		} catch (RuntimeException e) {
			return null;
		}
	}

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
			type.getConstructor(parameterTypes);
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
				if (ClassUtils.isAssignable(constructor.getParameterTypes(), parameterTypes)) {
					candidates.add((Constructor<T>) constructor);
				}
			}
			if (!candidates.isEmpty()) {
				Constructor<T> bestMatch = candidates.get(0);
				for (Constructor<T> constructor : candidates) {
					if (ClassUtils.equals(constructor.getParameterTypes(), parameterTypes)) {
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
