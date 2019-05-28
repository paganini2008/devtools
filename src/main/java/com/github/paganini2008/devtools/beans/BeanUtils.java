package com.github.paganini2008.devtools.beans;

import com.github.paganini2008.devtools.converter.BaseConverter;
import com.github.paganini2008.devtools.converter.StandardTypeConverter;
import com.github.paganini2008.devtools.converter.TypeConverter;
import com.github.paganini2008.devtools.reflection.ConstructorUtils;

/**
 * BeanUtils
 * 
 * @author Fred Feng
 * @version 1.0
 */
@SuppressWarnings("unchecked")
public class BeanUtils {

	private BeanUtils() {
	}

	private static final TypeConverter typeConverter = new StandardTypeConverter();

	public static <T> void registerConverter(Class<T> requiredType, BaseConverter<T> converter) {
		typeConverter.register(requiredType, converter);
	}

	public static Object copy(Object original) {
		return copy(original, (PropertyFilter) null);
	}

	public static Object copy(Object original, PropertyFilter propertyFilter) {
		return copy(original, original.getClass(), propertyFilter);
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
		PropertyUtils.copyProperties(original, destination, propertyFilter, true, typeConverter);
	}

	public static void setProperty(Object bean, String propertyName, Object value) {
		PropertyUtils.setProperty(bean, propertyName, value, typeConverter);
	}

	public static <T> T instantiate(String className) {
		return instantiate(className, (Object[]) null);
	}

	public static <T> T instantiate(String className, Object... arguments) {
		return instantiate(className, Thread.currentThread().getContextClassLoader(), arguments);
	}

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
