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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.paganini2008.devtools.converter.ConvertUtils;
import com.github.paganini2008.devtools.reflection.ConstructorUtils;

/**
 * 
 * BeanUtils
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
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
		T object = instantiate(requiredType);
		Map<String, PropertyDescriptor> desc = PropertyUtils.getPropertyDescriptors(object.getClass());
		String key;
		Object value;
		for (Map.Entry<String, PropertyDescriptor> entry : desc.entrySet()) {
			key = entry.getKey();
			value = map.get(key);
			if (value != null) {
				PropertyUtils.setProperty(object, entry.getValue(), value);
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

	public static <T> List<T> mockBeans(int count, Class<T> beanClass, MockContext context) {
		return mockBeans(count, beanClass, context, new RandomTemplate());
	}

	public static <T> List<T> mockBeans(int count, Class<T> beanClass, MockContext context, RandomOperations operations) {
		List<T> list = new ArrayList<T>(count);
		for (int i = 0; i < count; i++) {
			list.add(mockBean(beanClass, context, operations));
		}
		return list;
	}

	public static <T> T mockBean(Class<T> beanClass, MockContext context, RandomOperations operations) {
		T object = instantiate(beanClass);
		Map<String, PropertyDescriptor> desc = PropertyUtils.getPropertyDescriptors(object.getClass());
		String propertyName;
		Class<?> propertyType;
		Object propertyValue;
		for (PropertyDescriptor pd : desc.values()) {
			context.reset();
			propertyName = pd.getName();
			propertyType = pd.getPropertyType();
			if (propertyType.isAnnotationPresent(Recur.class)) {
				propertyValue = mockBean(propertyType, context, operations);
			} else {
				propertyValue = context.mock(propertyType, operations);
			}
			if (propertyValue != null) {
				PropertyUtils.setProperty(object, pd.getName(), propertyValue);
			}
		}
		return object;
	}

}
