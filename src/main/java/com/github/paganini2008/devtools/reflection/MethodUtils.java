package com.github.paganini2008.devtools.reflection;

import static com.github.paganini2008.devtools.ArrayUtils.EMPTY_OBJECT_ARRAY;

import java.lang.reflect.Method;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.ClassUtils;

/**
 * 
 * MethodUtils
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
public class MethodUtils {

	private MethodUtils() {
	}

	public static Object invokeMethod(Object object, String methodName, Object... arguments) {
		if (arguments == null) {
			arguments = EMPTY_OBJECT_ARRAY;
		}
		Class<?>[] parameterTypes = new Class<?>[arguments.length];
		for (int i = 0; i < parameterTypes.length; i++) {
			parameterTypes[i] = arguments[i].getClass();
		}
		return invokeMethod(object, methodName, parameterTypes, arguments);
	}

	public static Object invokeMethod(Object object, String methodName, Class<?>[] parameterTypes, Object... arguments) {
		Assert.isNull(object, "Source object must not be null.");
		if (parameterTypes == null) {
			parameterTypes = ClassUtils.EMPTY_ARRAY;
		}
		Method method = getMethod(object.getClass(), methodName, parameterTypes);
		return invokeMethod(object, method, arguments);
	}

	public static Object invokeStaticMethod(Class<?> type, String methodName, Object... arguments) {
		if (arguments == null) {
			arguments = EMPTY_OBJECT_ARRAY;
		}
		Class<?>[] parameterTypes = new Class<?>[arguments.length];
		for (int i = 0; i < parameterTypes.length; i++) {
			parameterTypes[i] = arguments[i].getClass();
		}
		return invokeStaticMethod(type, methodName, parameterTypes, arguments);
	}

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

	public static Method getMethod(Class<?> type, String methodName, Class<?>... parameterTypes) {
		try {
			return type.getDeclaredMethod(methodName, parameterTypes);
		} catch (NoSuchMethodException e) {
		}
		try {
			return searchDeclaredMethod(type, methodName, parameterTypes);
		} catch (NoSuchMethodException e) {
		}
		try {
			return type.getMethod(methodName, parameterTypes);
		} catch (NoSuchMethodException e) {
		}
		try {
			return searchMethod(type, methodName, parameterTypes);
		} catch (NoSuchMethodException e) {
		}
		throw new ReflectionException("No such accessible method: " + methodName + "() on object: " + type.getName());
	}

	private static Method searchMethod(Class<?> type, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
		final Method[] methods = type.getMethods();
		if (methods != null) {
			for (Method method : methods) {
				if (method.getName().equals(methodName)) {
					if (ClassUtils.isAssignable(method.getParameterTypes(), parameterTypes)) {
						return method;
					}
				}
			}
		}
		throw new NoSuchMethodException("No such accessible method: " + methodName + "() on object: " + type.getName());
	}

	private static Method searchDeclaredMethod(Class<?> type, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
		final Method[] methods = type.getDeclaredMethods();
		if (methods != null) {
			for (Method method : methods) {
				if (method.getName().equals(methodName)) {
					if (ClassUtils.isAssignable(method.getParameterTypes(), parameterTypes)) {
						return method;
					}
				}
			}
		}
		throw new NoSuchMethodException("No such accessible method: " + methodName + "() on object: " + type.getName());
	}

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
			throw new ReflectionException("Invoke method failed by name: " + method.getName(), e);
		}
	}

	public static Object invokeStaticMethod(Method method, Object... arguments) {
		Assert.isNull(method, "Method must not be null.");
		if (arguments == null) {
			arguments = EMPTY_OBJECT_ARRAY;
		}
		try {
			method.setAccessible(true);
			return method.invoke(null, arguments);
		} catch (Exception e) {
			throw new ReflectionException("Invoke method failed by name: " + method.getName(), e);
		}
	}
}
