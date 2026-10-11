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

import static com.github.paganini2008.devtools.ArrayUtils.EMPTY_OBJECT_ARRAY;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.ClassUtils;
import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * Utilities for locating and invoking methods via reflection: methods declared by the class itself of any visibility
 * (including private and static ones) and public methods inherited from super classes. Method
 * resolution supports boxing/unboxing, sub types and null arguments, and prefers the most specific overload.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class MethodUtils {

	/**
	 * Invokes the method best matching the runtime classes of the arguments. A null argument matches any reference
	 * parameter type.
	 * 
	 * @throws IllegalArgumentException if the object is null
	 * @throws ReflectionException if no method matches or the invocation fails
	 */
	public static Object invokeMethod(Object object, String methodName, Object... arguments) {
		if (arguments == null) {
			arguments = EMPTY_OBJECT_ARRAY;
		}
		Class<?>[] parameterTypes = new Class<?>[arguments.length];
		for (int i = 0; i < parameterTypes.length; i++) {
			parameterTypes[i] = arguments[i] != null ? arguments[i].getClass() : null;
		}
		return invokeMethod(object, methodName, parameterTypes, arguments);
	}

	/**
	 * Invokes the method best matching the given parameter types.
	 */
	public static Object invokeMethod(Object object, String methodName, Class<?>[] parameterTypes, Object... arguments) {
		Assert.isNull(object, "Source object must not be null.");
		if (parameterTypes == null) {
			parameterTypes = ClassUtils.EMPTY_ARRAY;
		}
		Method method = getMethod(object.getClass(), methodName, parameterTypes);
		return invokeMethod(object, method, arguments);
	}

	/**
	 * Invokes the static method best matching the runtime classes of the arguments.
	 * 
	 * @throws ReflectionException if no method matches or the invocation fails
	 */
	public static Object invokeStaticMethod(Class<?> type, String methodName, Object... arguments) {
		if (arguments == null) {
			arguments = EMPTY_OBJECT_ARRAY;
		}
		Class<?>[] parameterTypes = new Class<?>[arguments.length];
		for (int i = 0; i < parameterTypes.length; i++) {
			parameterTypes[i] = arguments[i] != null ? arguments[i].getClass() : null;
		}
		return invokeStaticMethod(type, methodName, parameterTypes, arguments);
	}

	/**
	 * Invokes the static method best matching the given parameter types.
	 */
	public static Object invokeStaticMethod(Class<?> type, String methodName, Class<?>[] parameterTypes, Object... arguments) {
		if (arguments == null) {
			arguments = EMPTY_OBJECT_ARRAY;
		}
		if (parameterTypes == null) {
			parameterTypes = ClassUtils.EMPTY_ARRAY;
		}
		Method method = getMethod(type, methodName, parameterTypes);
		return invokeStaticMethod(method, arguments);
	}

	/**
	 * Same as {@link #getMethod(Class, String, Class...)} but returns null instead of throwing.
	 */
	public static Method getMethodIfAbsent(Class<?> type, String methodName, Class<?>... parameterTypes) {
		try {
			return getMethod(type, methodName, parameterTypes);
		} catch (RuntimeException e) {
			return null;
		}
	}

	/**
	 * Finds the method declared in the class with exactly the given parameter types, or else the most specific declared
	 * one accepting arguments of these types (boxing aware), or else a matching public (possibly inherited) method.
	 * 
	 * @throws IllegalArgumentException if the type is null or the method name is blank
	 * @throws ReflectionException if no method matches
	 */
	public static Method getMethod(Class<?> type, String methodName, Class<?>... parameterTypes) {
		Assert.isNull(type, "Class must not be null.");
		Assert.hasNoText(methodName, "Method name must not be null or empty.");
		if (parameterTypes == null) {
			parameterTypes = ClassUtils.EMPTY_ARRAY;
		}
		Throwable cause = null;
		try {
			return type.getDeclaredMethod(methodName, parameterTypes);
		} catch (Exception e) {
			cause = e;
		}
		if (cause instanceof NoSuchMethodException) {
			try {
				return searchMethod(type, type.getDeclaredMethods(), methodName, parameterTypes);
			} catch (NoSuchMethodException e) {
				cause = e;
			}
		}
		try {
			return type.getMethod(methodName, parameterTypes);
		} catch (Exception e) {
			cause = e;
		}
		if (cause instanceof NoSuchMethodException) {
			try {
				return searchMethod(type, type.getMethods(), methodName, parameterTypes);
			} catch (NoSuchMethodException e) {
				cause = e;
			}
		}
		throw new ReflectionException(cause);
	}

	private static Method searchMethod(Class<?> type, Method[] methods, String methodName, Class<?>... parameterTypes)
			throws NoSuchMethodException {
		if (methods != null) {
			List<Method> candidates = new ArrayList<Method>();
			for (Method method : methods) {
				if (method.getName().equals(methodName)) {
					if (isCompatible(method.getParameterTypes(), parameterTypes)) {
						candidates.add(method);
					}
				}
			}
			if (!candidates.isEmpty()) {
				Method bestMatch = candidates.get(0);
				for (Method method : candidates) {
					if (ClassUtils.equals(method.getParameterTypes(), parameterTypes)) {
						return method;
					}
					if (isMoreSpecific(method.getParameterTypes(), bestMatch.getParameterTypes())) {
						bestMatch = method;
					}
				}
				return bestMatch;
			}
		}
		throw new NoSuchMethodException("No matched method: " + methodName + "(" + ArrayUtils.toString(parameterTypes) + ")");
	}

	/**
	 * Check whether the actual argument types can be applied to the declared parameter types. A null actual type
	 * (null argument) matches any non-primitive parameter type.
	 */
	static boolean isCompatible(Class<?>[] declaredTypes, Class<?>[] actualTypes) {
		if (declaredTypes.length != actualTypes.length) {
			return false;
		}
		for (int i = 0; i < declaredTypes.length; i++) {
			if (actualTypes[i] == null) {
				if (declaredTypes[i].isPrimitive()) {
					return false;
				}
			} else if (!ClassUtils.isAssignable(declaredTypes[i], actualTypes[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Check whether the parameter types on the left are more specific than the ones on the right.
	 */
	static boolean isMoreSpecific(Class<?>[] left, Class<?>[] right) {
		return !ClassUtils.equals(left, right) && ClassUtils.isAssignable(right, left);
	}

	/**
	 * Invokes the method on the object, making it accessible first.
	 * 
	 * @throws IllegalArgumentException if the object or method is null
	 * @throws ReflectionException if the invocation fails
	 */
	public static Object invokeMethod(Object object, Method method, Object... arguments) {
		Assert.isNull(object, "Source object must not be null.");
		Assert.isNull(method, "Method must not be null.");
		if (arguments == null) {
			arguments = EMPTY_OBJECT_ARRAY;
		}
		try {
			method.setAccessible(true);
			return method.invoke(object, arguments);
		} catch (Exception e) {
			throw new ReflectionException("Failed to invoke method: " + method.getName(), e);
		}
	}

	/**
	 * Invokes the static method, making it accessible first.
	 * 
	 * @throws ReflectionException if the invocation fails (e.g. the method is not static)
	 */
	public static Object invokeStaticMethod(Method method, Object... arguments) {
		Assert.isNull(method, "Method must not be null.");
		if (arguments == null) {
			arguments = EMPTY_OBJECT_ARRAY;
		}
		try {
			method.setAccessible(true);
			return method.invoke(null, arguments);
		} catch (Exception e) {
			throw new ReflectionException("Failed to invoke method: " + method.getName(), e);
		}
	}

	/**
	 * Returns the methods declared by the class and its directly implemented interfaces carrying the annotation.
	 */
	public static List<Method> getDeclaredMethodsWithAnnotation(Class<?> cls, final Class<? extends Annotation> annotationClass) {
		return getDeclaredMethods(cls, (name, method) -> {
			return method.isAnnotationPresent(annotationClass);
		});
	}

	/**
	 * Returns the methods declared by the class and its directly implemented interfaces accepted by the filter (all if
	 * null).
	 */
	public static List<Method> getDeclaredMethods(Class<?> cls, MethodFilter methodFilter) {
		List<Method> methods = new ArrayList<Method>();
		for (Method method : CollectionUtils.forEach(new DeclaredMethodIterator(cls))) {
			if (methodFilter == null || methodFilter.accept(method.getName(), method)) {
				methods.add(method);
			}
		}
		return methods;
	}

	/**
	 * Returns the methods declared by the class, all its super classes (except {@link Object}) and all its interfaces,
	 * accepted by the filter (all if null). Each method appears once.
	 */
	public static List<Method> getMethods(Class<?> cls, MethodFilter methodFilter) {
		List<Method> methods = new ArrayList<Method>();
		for (Method method : CollectionUtils.forEach(new MethodIterator(cls))) {
			if (methodFilter == null || methodFilter.accept(method.getName(), method)) {
				methods.add(method);
			}
		}
		return methods;
	}

	/**
	 * Returns the methods of the whole hierarchy carrying the annotation.
	 */
	public static List<Method> getMethodsWithAnnotation(Class<?> cls, final Class<? extends Annotation> annotationClass) {
		return getMethods(cls, (name, method) -> {
			return method.isAnnotationPresent(annotationClass);
		});
	}

	/**
	 * Invokes the first method of the hierarchy carrying the annotation, or returns null if there is none.
	 */
	public static Object invokeMethodWithAnnotation(Object object, Class<? extends Annotation> annotationClass, Object... arguments) {
		List<Method> methods = getMethodsWithAnnotation(object.getClass(), annotationClass);
		Method matched = CollectionUtils.getFirst(methods);
		if (matched != null) {
			return invokeMethod(object, matched, arguments);
		}
		return null;
	}

	/**
	 * Invokes every method of the hierarchy carrying the annotation and returns their results, or null if there is none.
	 */
	public static Object[] invokeMethodsWithAnnotation(Object object, Class<? extends Annotation> annotationClass, Object... arguments) {
		List<Method> methods = getMethodsWithAnnotation(object.getClass(), annotationClass);
		if (CollectionUtils.isNotEmpty(methods)) {
			Object[] results = new Object[methods.size()];
			int i = 0;
			for (Method method : methods) {
				results[i++] = invokeMethod(object, method, arguments);
			}
			return results;
		}
		return null;
	}

	/**
	 * Iterates the methods declared by a class followed by the methods of its directly implemented interfaces.
	 * 
	 * @author Fred Feng
	 * 
	 * 
	 * @since 2.0.1
	 */
	public static class DeclaredMethodIterator implements Iterator<Method> {

		DeclaredMethodIterator(Class<?> type) {
			Method[] methods = type.getDeclaredMethods();
			this.methods = methods != null ? CollectionUtils.iterator(methods) : CollectionUtils.emptyIterator();
			Class<?>[] types = type.getInterfaces();
			this.interfaces = types != null ? CollectionUtils.iterator(types) : CollectionUtils.emptyIterator();
		}

		private Iterator<Class<?>> interfaces;
		private Iterator<Method> methods;

		public boolean hasNext() {
			boolean next;
			while (!(next = canContinue()) && interfaces.hasNext()) {
				methods = CollectionUtils.iterator(interfaces.next().getDeclaredMethods());
			}
			return next;
		}

		private boolean canContinue() {
			return methods != null && methods.hasNext();
		}

		public Method next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			return methods.next();
		}
	}

	/**
	 * Iterates the methods declared by a class, its super classes and all its interfaces.
	 * 
	 * @author Fred Feng
	 * 
	 * 
	 * @since 2.0.1
	 */
	public static class MethodIterator implements Iterator<Method> {

		private final Iterator<Class<?>> superClassesAndInterfaces;
		private Iterator<Method> methods;

		MethodIterator(Class<?> type) {
			this.methods = CollectionUtils.iterator(type.getDeclaredMethods());
			this.superClassesAndInterfaces = ClassUtils.getAllSuperClassesAndInterfaces(type).iterator();
		}

		public boolean hasNext() {
			boolean next;
			while (!(next = canContinue()) && superClassesAndInterfaces.hasNext()) {
				methods = CollectionUtils.iterator(superClassesAndInterfaces.next().getDeclaredMethods());
			}
			return next;
		}

		private boolean canContinue() {
			return methods != null && methods.hasNext();
		}

		public Method next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			return methods.next();
		}

	}
}
