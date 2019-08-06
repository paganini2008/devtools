package com.github.paganini2008.devtools.beans;

import com.github.paganini2008.devtools.converter.ConvertUtils;
import com.github.paganini2008.devtools.reflection.ConstructorUtils;

/**
 * 
 * BeanUtils
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @created 2012-08
 * @version 1.0
 */
public abstract class BeanUtils {

	public static <T> T copy(Object original) {
		return copy(original, (PropertyFilter) null);
	}

	@SuppressWarnings("unchecked")
	public static <T> T copy(Object original, PropertyFilter propertyFilter) {
		return (T) copy(original, original.getClass(), propertyFilter);
	}

	public static <T> T copy(Object original, Class<T> requiredType, PropertyFilter propertyFilter) {
		final T destination = instantiate(requiredType);
		copyProperties(original, destination, propertyFilter);
		return destination;
	}

	public static void copyProperties(Object original, Object destination) {
		copyProperties(original, destination, null);
	}

	public static void copyProperties(Object original, Object destination, PropertyFilter propertyFilter) {
		PropertyUtils.copyProperties(original, destination, propertyFilter);
	}

	public static void setProperty(Object bean, String propertyName, Object value) {
		PropertyUtils.setProperty(bean, propertyName, value);
	}

	public static <T> T getProperty(Object bean, String propertyName, Class<T> requiredType) {
		return getProperty(bean, propertyName, requiredType, null);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getProperty(Object bean, String propertyName, Class<T> requiredType, T defaultValue) {
		Object rawValue = PropertyUtils.getProperty(bean, propertyName);
		if (requiredType != null) {
			try {
				return requiredType.cast(rawValue);
			} catch (RuntimeException e) {
				return ConvertUtils.convertValue(rawValue, requiredType, defaultValue);
			}
		}
		return (T) rawValue;
	}

	public static <T> T instantiate(String className) {
		return instantiate(className, (Object[]) null);
	}

	public static <T> T instantiate(String className, Object... arguments) {
		return instantiate(className, Thread.currentThread().getContextClassLoader(), arguments);
	}

	@SuppressWarnings("unchecked")
	public static <T> T instantiate(String className, ClassLoader classLoader, Object... arguments) {
		Class<?> requiredType;
		try {
			requiredType = Class.forName(className, true, classLoader);
		} catch (ClassNotFoundException e) {
			throw new BeanInstantiationException(e.getMessage(), e);
		}
		return (T) instantiate(requiredType, arguments);
	}

	public static <T> T instantiate(Class<T> requiredType, Object... arguments) {
		try {
			return ConstructorUtils.invokeConstructor(requiredType, arguments);
		} catch (Exception e) {
			throw new BeanInstantiationException(e.getMessage(), e);
		}
	}

}
