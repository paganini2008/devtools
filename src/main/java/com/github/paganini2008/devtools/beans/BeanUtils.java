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
package com.github.paganini2008.devtools.beans;

import java.beans.PropertyDescriptor;
import java.util.Map;

import com.github.paganini2008.devtools.converter.ConvertUtils;
import com.github.paganini2008.devtools.reflection.ConstructorUtils;

/**
 * Convenience methods for copying, instantiating and accessing JavaBeans, with automatic type conversion
 * via {@link com.github.paganini2008.devtools.converter.ConvertUtils}.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 *
 * @since 2.0.1
 */
@SuppressWarnings("all")
public abstract class BeanUtils {

	/**
	 * Creates a new instance of the original's class (default constructor required) and copies all properties into it.
	 */
	public static <T> T copy(Object original) {
		return copy(original, (PropertyFilter) null);
	}

	/**
	 * Creates a new instance of the original's class and copies the properties accepted by the filter.
	 */
	public static <T> T copy(Object original, PropertyFilter propertyFilter) {
		return (T) copy(original, original.getClass(), propertyFilter);
	}

	/**
	 * Creates a new instance of the required type and copies the matching properties accepted by the filter, converting
	 * values where necessary.
	 * 
	 * @throws BeanInstantiationException if the required type cannot be instantiated
	 */
	public static <T> T copy(Object original, Class<T> requiredType, PropertyFilter propertyFilter) {
		final T destination = instantiate(requiredType);
		copyProperties(original, destination, propertyFilter);
		return destination;
	}

	/**
	 * Copies all matching properties from the original to the destination. See
	 * {@link PropertyUtils#copyProperties(Object, Object)}.
	 */
	public static void copyProperties(Object original, Object destination) {
		copyProperties(original, destination, null);
	}

	/**
	 * Copies the matching properties accepted by the filter from the original to the destination.
	 */
	public static void copyProperties(Object original, Object destination, PropertyFilter propertyFilter) {
		PropertyUtils.copyProperties(original, destination, propertyFilter);
	}

	/**
	 * Sets a (possibly nested) property, converting the value if necessary. Unlike
	 * {@link PropertyUtils#setProperty(Object, String, Object)}, any failure is silently ignored.
	 */
	public static void setProperty(Object bean, String propertyName, Object value) {
		try {
			PropertyUtils.setProperty(bean, propertyName, value);
		} catch (RuntimeException ignored) {
		}
	}

	/**
	 * Gets a (possibly nested) property converted to the required type; returns null if the value is null or cannot be
	 * converted.
	 */
	public static <T> T getProperty(Object bean, String propertyName, Class<T> requiredType) {
		return getProperty(bean, propertyName, requiredType, null);
	}

	/**
	 * Gets a (possibly nested) property converted to the required type.
	 * 
	 * @param requiredType the target type, or null to return the raw value
	 * @param defaultValue returned when the value is null or cannot be converted
	 * @throws IllegalArgumentException if the bean is null or the property does not exist
	 */
	public static <T> T getProperty(Object bean, String propertyName, Class<T> requiredType, T defaultValue) {
		Object rawValue = PropertyUtils.getProperty(bean, propertyName);
		if (rawValue == null) {
			return defaultValue;
		}
		if (requiredType != null) {
			// checked instead of catching the ClassCastException of Class#cast, which is expensive on this hot path
			if (requiredType.isInstance(rawValue)) {
				return requiredType.cast(rawValue);
			}
			return ConvertUtils.convertValue(rawValue, requiredType, defaultValue);
		}
		return (T) rawValue;
	}

	/**
	 * Creates an instance of the required type and sets each writable property from the map entry with the same name.
	 * Null values and unknown keys are ignored; values are converted to the property type. A null map yields an instance
	 * with default values.
	 */
	public static <T> T convertAsBean(Map<String, ?> map, Class<T> requiredType) {
		T object = instantiate(requiredType);
		if (map == null) {
			return object;
		}
		Map<String, PropertyDescriptor> desc = PropertyUtils.getPropertyDescriptors(object.getClass());
		String key;
		Object value;
		for (Map.Entry<String, PropertyDescriptor> entry : desc.entrySet()) {
			key = entry.getKey();
			value = map.get(key);
			if (value != null && entry.getValue().getWriteMethod() != null) {
				PropertyUtils.setProperty(object, entry.getValue(), value);
			}
		}
		return object;
	}

	/**
	 * Loads the class with the context class loader and invokes its default constructor.
	 * 
	 * @throws BeanInstantiationException if the class cannot be found or instantiated
	 */
	public static <T> T instantiate(String className) {
		return instantiate(className, (Object[]) null);
	}

	/**
	 * Loads the class with the context class loader and invokes the constructor best matching the arguments.
	 */
	public static <T> T instantiate(String className, Object... arguments) {
		return instantiate(className, Thread.currentThread().getContextClassLoader(), arguments);
	}

	/**
	 * Loads the class with the given class loader and invokes the constructor best matching the arguments.
	 */
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

	/**
	 * Invokes the constructor best matching the arguments (null arguments match any reference type).
	 * 
	 * @throws BeanInstantiationException if no constructor matches or the constructor fails
	 */
	public static <T> T instantiate(Class<T> requiredType, Object... arguments) {
		try {
			return ConstructorUtils.invokeConstructor(requiredType, arguments);
		} catch (Exception e) {
			throw new BeanInstantiationException(e.getMessage(), e);
		}
	}

}
