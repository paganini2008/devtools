package com.github.paganini2008.devtools.reflection;

import java.lang.reflect.Constructor;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.ClassUtils;

/**
 * ConstructorUtils
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class ConstructorUtils {

	private ConstructorUtils() {
	}

	public static <T> T invokeConstructor(Class<T> cl, Object... arugments) {
		if (arugments == null) {
			arugments = ArrayUtils.EMPTY_OBJECT_ARRAY;
		}
		Class<?>[] parameterTypes = new Class<?>[arugments.length];
		for (int i = 0; i < parameterTypes.length; i++) {
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

	public static <T> Constructor<T> getConstructor(Class<T> type, Class<?>... parameterTypes) {
		Assert.isNull(type, "Class must not be null.");
		if (parameterTypes == null) {
			parameterTypes = ClassUtils.EMPTY_ARRAY;
		}
		try {
			return type.getDeclaredConstructor(parameterTypes);
		} catch (NoSuchMethodException e) {
		}
		try {
			return searchDeclaredConstructor(type, parameterTypes);
		} catch (NoSuchMethodException e) {
		}
		throw new ReflectionException("No such accessible constructor on object: " + type.getName());
	}

	@SuppressWarnings("unchecked")
	private static <T> Constructor<T> searchDeclaredConstructor(Class<T> cl, Class<?>[] parameterTypes) throws NoSuchMethodException {
		Constructor<?>[] methods = cl.getDeclaredConstructors();
		for (Constructor<?> method : methods) {
			if (ClassUtils.isAssignable(method.getParameterTypes(), parameterTypes)) {
				return (Constructor<T>) method;
			}
		}
		throw new NoSuchMethodException("No such accessible constructor on object: " + cl.getName());
	}

}
