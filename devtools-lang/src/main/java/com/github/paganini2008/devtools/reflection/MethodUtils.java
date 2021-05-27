package com.github.paganini2008.devtools.reflection;

import static com.github.paganini2008.devtools.ArrayUtils.EMPTY_OBJECT_ARRAY;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.ClassUtils;
import com.github.paganini2008.devtools.collection.CollectionUtils;

/**
 * 
 * MethodUtils
 * 
 * @author Fred Feng
 * @version 1.0
 */
public abstract class MethodUtils {

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

	public static Method getMethodIfAbsent(Class<?> type, String methodName, Class<?>... parameterTypes) {
		try {
			return getMethod(type, methodName, parameterTypes);
		} catch (RuntimeException e) {
			return null;
		}
	}

	public static Method getMethod(Class<?> type, String methodName, Class<?>... parameterTypes) {
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
					if (ClassUtils.isAssignable(method.getParameterTypes(), parameterTypes)) {
						candidates.add(method);
					}
				}
			}
			if (!candidates.isEmpty()) {
				Method bestMatch = candidates.get(0);
				for (Method method : candidates) {
					if (ClassUtils.equals(method.getParameterTypes(), parameterTypes)) {
						bestMatch = method;
					}
				}
				return bestMatch;
			}
		}
		throw new NoSuchMethodException("No matched method: " + methodName + "(" + ArrayUtils.toString(parameterTypes) + ")");
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
			throw new ReflectionException("Failed to invoke method: " + method.getName(), e);
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
			throw new ReflectionException("Failed to invoke method: " + method.getName(), e);
		}
	}

	public static List<Method> getDeclaredMethodsWithAnnotation(Class<?> cls, final Class<? extends Annotation> annotationClass) {
		return getDeclaredMethods(cls, (name, method) -> {
			return method.isAnnotationPresent(annotationClass);
		});
	}

	public static List<Method> getDeclaredMethods(Class<?> cls, MethodFilter methodFilter) {
		List<Method> methods = new ArrayList<Method>();
		for (Method method : CollectionUtils.forEach(new DeclaredMethodIterator(cls))) {
			if (methodFilter == null || methodFilter.accept(method.getName(), method)) {
				methods.add(method);
			}
		}
		return methods;
	}

	public static List<Method> getMethods(Class<?> cls, MethodFilter methodFilter) {
		List<Method> methods = new ArrayList<Method>();
		for (Method method : CollectionUtils.forEach(new MethodIterator(cls))) {
			if (methodFilter == null || methodFilter.accept(method.getName(), method)) {
				methods.add(method);
			}
		}
		return methods;
	}

	public static List<Method> getMethodsWithAnnotation(Class<?> cls, final Class<? extends Annotation> annotationClass) {
		return getMethods(cls, (name, method) -> {
			return method.isAnnotationPresent(annotationClass);
		});
	}

	public static Object invokeMethodWithAnnotation(Object object, Class<? extends Annotation> annotationClass, Object... arguments) {
		List<Method> methods = getMethodsWithAnnotation(object.getClass(), annotationClass);
		Method matched = CollectionUtils.getFirst(methods);
		if (matched != null) {
			return invokeMethod(object, matched, arguments);
		}
		return null;
	}

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
	 * 
	 * DeclaredMethodIterator
	 * 
	 * @author Fred Feng
	 * 
	 * 
	 * @version 1.0
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
			if (!(next = canContinue())) {
				methods = interfaces.hasNext() ? CollectionUtils.iterator(interfaces.next().getDeclaredMethods()) : null;
				next = canContinue();
			}
			return next;
		}

		private boolean canContinue() {
			return methods != null && methods.hasNext();
		}

		public Method next() {
			return methods.next();
		}
	}

	/**
	 * 
	 * MethodIterator
	 * 
	 * @author Fred Feng
	 * 
	 * 
	 * @version 1.0
	 */
	public static class MethodIterator implements Iterator<Method> {

		private final Iterator<Class<?>> superClassesAndInterfaces;
		private Iterator<Method> methods;

		MethodIterator(Class<?> type) {
			this.methods = new DeclaredMethodIterator(type);
			this.superClassesAndInterfaces = ClassUtils.getAllSuperClassesAndInterfaces(type).iterator();
		}

		public boolean hasNext() {
			boolean next;
			if (!(next = canContinue())) {
				methods = superClassesAndInterfaces.hasNext() ? new DeclaredMethodIterator(superClassesAndInterfaces.next()) : null;
				next = canContinue();
			}
			return next;
		}

		private boolean canContinue() {
			return methods != null && methods.hasNext();
		}

		public Method next() {
			return methods.next();
		}

	}
}
