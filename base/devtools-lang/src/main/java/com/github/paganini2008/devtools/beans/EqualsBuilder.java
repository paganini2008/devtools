package com.github.paganini2008.devtools.beans;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import com.github.paganini2008.devtools.ObjectUtils;
import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.reflection.MethodUtils;

/**
 * 
 * EqualsBuilder
 * 
 * @author Fred Feng
 * @created 2012-05
 * @revised 2019-07
 * @version 1.0
 */
public abstract class EqualsBuilder {

	public static boolean reflectionEquals(Object source, Object target) {
		return reflectionEquals(source, target, (PropertyFilter) null);
	}

	public static boolean reflectionEquals(Object source, Object target, PropertyFilter filter) {
		return reflectionEquals(source, target, null, filter);
	}

	public static boolean reflectionEquals(Object source, Object target, Class<?> stopClass, PropertyFilter filter) {
		if (source == target) {
			return true;
		}
		if (source == null || target == null) {
			return false;
		}
		if (source.getClass() != target.getClass()) {
			return false;
		}
		Map<String, PropertyDescriptor> descriptors = PropertyUtils.getPropertyDescriptors(source.getClass(), stopClass, filter);
		if (MapUtils.isEmpty(descriptors)) {
			return source.equals(target);
		}
		return reflectionEquals(source, target, descriptors.values());
	}

	private static boolean reflectionEquals(Object source, Object target, Collection<PropertyDescriptor> descriptors) {
		Object left, right;
		Method method;
		for (PropertyDescriptor descriptor : descriptors) {
			method = descriptor.getReadMethod();
			if (method != null) {
				left = invokeMethod(source, method);
				right = invokeMethod(target, method);
				if (ObjectUtils.notEquals(left, right)) {
					return false;
				}
			}
		}
		return true;
	}

	private static Object invokeMethod(Object object, Method method) {
		try {
			return MethodUtils.invokeMethod(object, method);
		} catch (Exception e) {
			throw new BeanPropertyAccessException(e.getMessage(), e);
		}
	}

}
