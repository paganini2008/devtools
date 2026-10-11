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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.collection.LruMap;
import com.github.paganini2008.devtools.converter.ConvertUtils;
import com.github.paganini2008.devtools.reflection.FieldUtils;
import com.github.paganini2008.devtools.reflection.MethodUtils;
import com.github.paganini2008.devtools.reflection.ReflectionException;

/**
 * Low level JavaBean property access based on {@link java.beans.Introspector}: property descriptors (cached), getting and
 * setting simple or nested ({@code "a.b.c"}) properties, populating from maps, copying and converting to maps.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class PropertyUtils {

	/**
	 * Descriptors cached per bean class and stop class. The cache is attached to the bean class itself through
	 * {@link ClassValue}, so it never keeps a class (and its class loader) alive after the class has been unloaded, for
	 * example when a web application is redeployed while this library lives in a shared class loader.
	 */
	private static final ClassValue<Map<Type, Map<String, PropertyDescriptor>>> cache = new ClassValue<Map<Type, Map<String, PropertyDescriptor>>>() {

		@Override
		protected Map<Type, Map<String, PropertyDescriptor>> computeValue(Class<?> type) {
			return new LruMap<Type, Map<String, PropertyDescriptor>>(16);
		}
	};

	/**
	 * Returns the (cached, unmodifiable) property descriptors of the class keyed by name, excluding the properties of
	 * {@link Object} such as {@code class}.
	 * 
	 * @throws IllegalArgumentException if the bean class is null
	 */
	public static Map<String, PropertyDescriptor> getPropertyDescriptors(Class<?> beanClass) {
		return getPropertyDescriptors(beanClass, (Class<?>) null);
	}

	/**
	 * Returns the (cached, unmodifiable) property descriptors declared below the stop class ({@link Object} if null).
	 * 
	 * @throws IllegalArgumentException if the bean class is null or the stop class is not a super class of it
	 */
	public static Map<String, PropertyDescriptor> getPropertyDescriptors(Class<?> beanClass, Class<?> stopClass) {
		// explicit checks instead of Assert on this hot path: Assert creates the exception even when the check passes
		if (beanClass == null) {
			throw new IllegalArgumentException("Bean class must not be null.");
		}
		if (stopClass == null) {
			stopClass = Object.class;
		}
		Map<Type, Map<String, PropertyDescriptor>> map = cache.get(beanClass);
		Map<String, PropertyDescriptor> data = map.get(stopClass);
		if (data == null) {
			data = fetchPropertyDescriptors(beanClass, stopClass);
			Map<String, PropertyDescriptor> existing = map.putIfAbsent(stopClass, data);
			if (existing != null) {
				data = existing;
			}
		}
		return data;
	}

	/**
	 * Returns a new map of the property descriptors declared below the stop class and accepted by the filter.
	 */
	public static Map<String, PropertyDescriptor> getPropertyDescriptors(Class<?> beanClass, Class<?> stopClass, PropertyFilter filter) {
		Map<String, PropertyDescriptor> original = getPropertyDescriptors(beanClass, stopClass);
		Map<String, PropertyDescriptor> destination = new LinkedHashMap<String, PropertyDescriptor>();
		if (original != null) {
			for (Map.Entry<String, PropertyDescriptor> e : original.entrySet()) {
				if (filter == null || filter.accept(beanClass, e.getKey(), e.getValue())) {
					destination.put(e.getKey(), e.getValue());
				}
			}
		}
		return destination;
	}

	@SuppressWarnings("unchecked")
	private static Map<String, PropertyDescriptor> fetchPropertyDescriptors(Class<?> beanClass, Class<?> stopClass) {
		if (beanClass == stopClass) {
			return Collections.EMPTY_MAP;
		}
		BeanInfo info = null;
		try {
			// interfaces have no super class, so the default stop class (Object) cannot be applied to them
			info = beanClass.isInterface() && stopClass == Object.class ? Introspector.getBeanInfo(beanClass)
					: Introspector.getBeanInfo(beanClass, stopClass);
		} catch (IntrospectionException e) {
			throw new IllegalArgumentException(
					"Invalid bean class '" + beanClass.getName() + "' or stop class '" + stopClass.getName() + "'.", e);
		} finally {
			// the result is cached here; drop the JDK's own BeanInfo cache so it cannot pin the class either
			Introspector.flushFromCaches(beanClass);
		}
		PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
		if (ArrayUtils.isNotEmpty(descriptors)) {
			Map<String, PropertyDescriptor> data = new LinkedHashMap<String, PropertyDescriptor>(descriptors.length);
			for (PropertyDescriptor descriptor : descriptors) {
				data.put(descriptor.getName(), descriptor);
			}
			return Collections.unmodifiableMap(data);
		}
		return Collections.EMPTY_MAP;
	}

	/**
	 * Sets each property of the destination from the map entry with the same name, converting values where necessary.
	 * Unknown keys are ignored; a null map is a no-op.
	 */
	public static void populate(Object destination, Map<String, ?> map) {
		populate(destination, map, null);
	}

	/**
	 * Populates the properties accepted by the filter.
	 */
	public static void populate(Object destination, Map<String, ?> map, PropertyFilter filter) {
		populate(destination, map, filter, true);
	}

	/**
	 * Populates the properties accepted by the filter.
	 * 
	 * @param overwrited if false, properties whose current value is not null are left unchanged
	 */
	public static void populate(Object destination, Map<String, ?> map, PropertyFilter filter, boolean overwrited) {
		populate(destination, null, map, filter, overwrited);
	}

	/**
	 * Populates the properties declared below the stop class and accepted by the filter.
	 */
	public static void populate(Object destination, Class<?> stopClass, Map<String, ?> map, PropertyFilter filter, boolean overwrited) {
		Assert.isNull(destination, "Destination instance must not be null.");
		if (map == null) {
			return;
		}
		Map<String, PropertyDescriptor> dest = getPropertyDescriptors(destination.getClass(), stopClass, filter);
		PropertyDescriptor descriptor;
		String propertyName;
		for (Map.Entry<String, ?> entry : map.entrySet()) {
			propertyName = entry.getKey();
			descriptor = dest.get(propertyName);
			if (descriptor != null) {
				if (!overwrited && descriptor.getReadMethod() != null) {
					Object current = getProperty(destination, descriptor);
					if (current != null) {
						continue;
					}
				}
				setProperty(destination, descriptor, entry.getValue());
			}
		}
	}

	private static Map<String, PropertyDescriptor> getMappingPropertyDescriptors(Class<?> type) {
		Map<String, PropertyDescriptor> descriptors = getPropertyDescriptors(type);
		Map<String, PropertyDescriptor> results = new LinkedHashMap<String, PropertyDescriptor>(descriptors);
		String propertyName;
		PropertyDescriptor descriptor;
		Method method;
		Field field;
		PropertyMapper mapping;
		for (Map.Entry<String, PropertyDescriptor> e : descriptors.entrySet()) {
			propertyName = e.getKey();
			descriptor = e.getValue();
			field = findField(type, propertyName);
			if (field != null) {
				if (field.isAnnotationPresent(ExcludedProperty.class)) {
					results.remove(propertyName);
				} else if (field.isAnnotationPresent(PropertyMapper.class)) {
					mapping = field.getAnnotation(PropertyMapper.class);
					results.put(StringUtils.isBlank(mapping.value()) ? e.getKey() : mapping.value(), descriptor);
				}
			}
			method = descriptor.getWriteMethod();
			if (method != null && method.isAnnotationPresent(PropertyMapper.class)) {
				mapping = method.getAnnotation(PropertyMapper.class);
				results.put(StringUtils.isBlank(mapping.value()) ? e.getKey() : mapping.value(), descriptor);
			}
		}
		return results;
	}

	/**
	 * Finds the field backing a property in the class or any of its super classes (whatever its visibility), so that
	 * mapping annotations declared on inherited private fields are honored.
	 */
	private static Field findField(Class<?> type, String fieldName) {
		for (Class<?> cls = type; cls != null; cls = cls.getSuperclass()) {
			try {
				return cls.getDeclaredField(fieldName);
			} catch (NoSuchFieldException | SecurityException ignored) {
			}
		}
		return FieldUtils.getFieldIfAbsent(type, fieldName);
	}

	/**
	 * Returns the descriptor of the named property, or null if the class has no such property.
	 */
	public static PropertyDescriptor getPropertyDescriptor(Class<?> beanClass, String propertyName) {
		final Map<String, PropertyDescriptor> dest = getPropertyDescriptors(beanClass, null);
		return dest.get(propertyName);
	}

	/**
	 * Checks whether the class has the named property.
	 */
	public static boolean hasProperty(Class<?> beanClass, String propertyName) {
		final Map<String, PropertyDescriptor> dest = getPropertyDescriptors(beanClass, null);
		return dest.containsKey(propertyName);
	}

	/**
	 * Sets the property, converting the value to the property type if necessary. If the property has no standard
	 * setter, a {@code setXxx} method accepting the value's class is used.
	 * 
	 * @return false if the converted value is null but the property is primitive, true otherwise
	 * @throws IllegalArgumentException if the bean or descriptor is null, or no setter exists
	 * @throws BeanPropertyAccessException if the setter fails
	 */
	public static boolean setProperty(Object bean, PropertyDescriptor descriptor, Object value) {
		Assert.isNull(bean, "Source instance must not be null.");
		Assert.isNull(descriptor, "Property descriptor must not be null.");
		Method method = descriptor.getWriteMethod();
		Class<?> propertyType = descriptor.getPropertyType();
		if (method == null) {
			if (value != null) {
				String methodName = "set" + StringUtils.capitalize(descriptor.getName());
				method = MethodUtils.getMethod(bean.getClass(), methodName, value.getClass());
				if (method != null) {
					propertyType = method.getParameterTypes()[0];
				}
			}
		}
		Assert.isNull(method, "Cannot find write method of property '" + descriptor.getName() + "'.");
		return setProperty(bean, method, propertyType, value);
	}

	private static boolean setProperty(Object bean, Method method, Class<?> propertyType, Object value) {
		Object realValue;
		try {
			realValue = propertyType.cast(value);
		} catch (RuntimeException e) {
			realValue = ConvertUtils.convertValue(value, propertyType);
		}
		if (realValue == null && propertyType.isPrimitive()) {
			return false;
		}
		invokeSetter(bean, method, realValue);
		return true;
	}

	/**
	 * Sets a simple or nested ({@code "address.city"}) property, converting the value if necessary.
	 * 
	 * @return false if the converted value is null but the property is primitive, true otherwise
	 * @throws IllegalArgumentException if the bean (or an intermediate bean) is null or the property does not exist
	 */
	public static boolean setProperty(Object bean, String propertyName, Object value) {
		final int index = propertyName.indexOf('.');
		if (index > 0) {
			bean = getProperty(bean, propertyName.substring(0, index));
			return setProperty(bean, propertyName.substring(index + 1), value);
		} else {
			Assert.isNull(bean, "Source instance must not be null.");
			PropertyDescriptor descriptor = getPropertyDescriptor(bean.getClass(), propertyName);
			Assert.isNull(descriptor, "Property '" + propertyName + "' is not found in class '" + bean.getClass().getName() + "'.");
			return setProperty(bean, descriptor, value);
		}
	}

	/**
	 * Reads the property through its getter.
	 * 
	 * @throws IllegalArgumentException if the bean or descriptor is null, or the property has no getter
	 * @throws BeanPropertyAccessException if the getter fails
	 */
	public static Object getProperty(Object bean, PropertyDescriptor descriptor) {
		if (bean == null) {
			throw new IllegalArgumentException("Source instance must not be null.");
		}
		if (descriptor == null) {
			throw new IllegalArgumentException("Property descriptor must not be null.");
		}
		Method method = descriptor.getReadMethod();
		if (method == null) {
			throw new IllegalArgumentException("Cannot find the getter of '" + descriptor.getName() + "'.");
		}
		return invokeGetter(bean, method);
	}

	/**
	 * Reads a simple or nested ({@code "address.city"}) property.
	 * 
	 * @throws IllegalArgumentException if the bean (or an intermediate bean) is null, the name is blank or the property
	 *             does not exist
	 */
	public static Object getProperty(Object bean, String propertyName) {
		if (bean == null) {
			throw new IllegalArgumentException("Source instance must not be null.");
		}
		if (StringUtils.isBlank(propertyName)) {
			throw new IllegalArgumentException("PropertyName must not be null or empty.");
		}
		final int index = propertyName.indexOf('.');
		if (index > 0) {
			bean = getProperty(bean, propertyName.substring(0, index));
			return getProperty(bean, propertyName.substring(index + 1));
		} else {
			PropertyDescriptor descriptor = getPropertyDescriptor(bean.getClass(), propertyName);
			if (descriptor == null) {
				throw new IllegalArgumentException(
						"Property '" + propertyName + "' is not found in class '" + bean.getClass().getName() + "'.");
			}
			return getProperty(bean, descriptor);
		}
	}

	/**
	 * Copies every property that exists in both beans and is readable on the original and writable on the destination,
	 * converting values where necessary. Properties missing on the original are left unchanged on the destination.
	 */
	public static void copyProperties(Object original, Object destination) {
		copyProperties(original, destination, null);
	}

	/**
	 * Copies the properties of the original accepted by the filter.
	 */
	public static void copyProperties(Object original, Object destination, PropertyFilter filter) {
		copyProperties(original, destination, filter, true);
	}

	/**
	 * Copies the properties of the original accepted by the filter.
	 * 
	 * @param overwrited if false, destination properties whose current value is not null are left unchanged
	 */
	public static void copyProperties(Object original, Object destination, PropertyFilter filter, boolean overwrited) {
		copyProperties(original, null, destination, filter, overwrited);
	}

	/**
	 * Copies the properties of the original declared below the stop class and accepted by the filter.
	 */
	public static void copyProperties(Object original, Class<?> stopClass, Object destination, PropertyFilter filter, boolean overwrited) {
		copyProperties(original, stopClass, destination, filter, overwrited, false);
	}

	/**
	 * Copies properties from the original to the destination.
	 * 
	 * @param mappingProperty if true, {@link PropertyMapper} and {@link ExcludedProperty} annotations on the destination
	 *            are honored
	 * @throws IllegalArgumentException if the original or destination is null
	 */
	public static void copyProperties(Object original, Class<?> stopClass, Object destination, PropertyFilter filter, boolean overwrited,
			boolean mappingProperty) {
		Assert.isNull(original, "Source instance must not be null.");
		Assert.isNull(destination, "Destination instance must not be null.");
		Map<String, PropertyDescriptor> orig = getPropertyDescriptors(original.getClass(), stopClass, filter);
		Map<String, PropertyDescriptor> dest = mappingProperty ? getMappingPropertyDescriptors(destination.getClass())
				: getPropertyDescriptors(destination.getClass());
		proceedCopyProperties(original, orig, destination, dest, overwrited);
	}

	private static void proceedCopyProperties(Object original, Map<String, PropertyDescriptor> orig, Object destination,
			Map<String, PropertyDescriptor> dest, boolean overwrited) {
		PropertyDescriptor descriptor, source;
		String path;
		Object value;
		int index;
		for (Map.Entry<String, PropertyDescriptor> entry : dest.entrySet()) {
			path = entry.getKey();
			descriptor = entry.getValue();
			if (descriptor.getWriteMethod() == null) {
				continue;
			}
			if (!overwrited && descriptor.getReadMethod() != null) {
				Object current = getProperty(destination, descriptor);
				if (current != null) {
					continue;
				}
			}
			source = orig.get(path);
			try {
				if (source != null) {
					if (source.getReadMethod() == null) {
						continue;
					}
					value = getProperty(original, source);
				} else if ((index = path.indexOf('.')) > 0 && orig.containsKey(path.substring(0, index))) {
					value = getProperty(original, path);
				} else {
					continue;
				}
			} catch (RuntimeException ignored) {
				continue;
			}
			setProperty(destination, descriptor, value);
		}
	}

	/**
	 * Reads all properties into a new ordered map keyed by property name.
	 * 
	 * @throws IllegalArgumentException if the bean is null
	 */
	public static Map<String, Object> convertToMap(Object bean) {
		return convertToMap(bean, null);
	}

	/**
	 * Reads the properties declared below the stop class into a new ordered map.
	 */
	public static Map<String, Object> convertToMap(Object bean, Class<?> stopClass) {
		return convertToMap(bean, stopClass, null);
	}

	/**
	 * Reads the properties declared below the stop class and accepted by the filter into a new ordered map.
	 */
	public static Map<String, Object> convertToMap(Object bean, Class<?> stopClass, PropertyFilter filter) {
		Assert.isNull(bean, "Source instance must not be null.");
		Map<String, PropertyDescriptor> dest = getPropertyDescriptors(bean.getClass(), stopClass, filter);
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		PropertyDescriptor descriptor;
		for (Map.Entry<String, PropertyDescriptor> entry : dest.entrySet()) {
			descriptor = entry.getValue();
			map.put(entry.getKey(), getProperty(bean, descriptor));
		}
		return map;
	}

	private static Object invokeSetter(Object bean, Method method, Object value) {
		try {
			return MethodUtils.invokeMethod(bean, method, value);
		} catch (Exception e) {
			throw new BeanPropertyAccessException(
					"Cannot access the setter '" + method.getName() + "' on bean '" + bean.getClass().getName() + "'.", e);
		}
	}

	private static Object invokeGetter(Object bean, Method method) {
		try {
			// same as MethodUtils.invokeMethod (including the wrapped exception), without its per call argument checks
			try {
				if (!method.isAccessible()) {
					method.setAccessible(true);
				}
				return method.invoke(bean);
			} catch (Exception e) {
				throw new ReflectionException("Failed to invoke method: " + method.getName(), e);
			}
		} catch (Exception e) {
			throw new BeanPropertyAccessException(
					"Cannot access the getter '" + method.getName() + "' on bean '" + bean.getClass().getName() + "'.", e);
		}
	}

}
