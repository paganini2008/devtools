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
package com.github.paganini2008.devtools.beans;

import java.beans.PropertyDescriptor;
import java.util.Map;

import com.github.paganini2008.devtools.converter.ConvertUtils;
import com.github.paganini2008.devtools.reflection.ConstructorUtils;

/**
 * 
 * BeanUtils
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
@SuppressWarnings("all")
public abstract class BeanUtils {

	public static <T> T copy(Object original) {
		return copy(original, (PropertyFilter) null);
	}

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
		try {
			PropertyUtils.setProperty(bean, propertyName, value);
		} catch (RuntimeException ignored) {
		}
	}

	public static <T> T getProperty(Object bean, String propertyName, Class<T> requiredType) {
		return getProperty(bean, propertyName, requiredType, null);
	}

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

	public static <T> T convertAsBean(Map<String, ?> map, Class<T> requiredType) {
		T object = BeanUtils.instantiate(requiredType);
		Map<String, PropertyDescriptor> desc = PropertyUtils.getPropertyDescriptors(object.getClass());
		Object value;
		for (String key : desc.keySet()) {
			value = map.get(key);
			if (value != null) {
				PropertyUtils.setProperty(object, key, value);
			}
		}
		return object;
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
