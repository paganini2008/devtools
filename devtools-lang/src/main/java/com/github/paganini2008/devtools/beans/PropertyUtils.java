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

/**
 * 
 * PropertyUtils
 * 
 * @author Fred Feng
 * @version 1.0
 */
public abstract class PropertyUtils {

	private static final Map<Type, Map<Type, Map<String, PropertyDescriptor>>> cache = new LruMap<Type, Map<Type, Map<String, PropertyDescriptor>>>(
			256);

	public static Map<String, PropertyDescriptor> getPropertyDescriptors(Class<?> beanClass) {
		return getPropertyDescriptors(beanClass, (Class<?>) null);
	}

	public static Map<String, PropertyDescriptor> getPropertyDescriptors(Class<?> beanClass, Class<?> stopClass) {
		Assert.isNull(beanClass, "Bean class must not be null.");
		if (stopClass == null) {
			stopClass = Object.class;
		}
		Map<Type, Map<String, PropertyDescriptor>> map = cache.get(beanClass);
		if (map == null) {
			cache.put(beanClass, new LruMap<Type, Map<String, PropertyDescriptor>>(16));
			map = cache.get(beanClass);
		}
		Map<String, PropertyDescriptor> data = map.get(stopClass);
		if (data == null) {
			map.put(stopClass, fetchPropertyDescriptors(beanClass, stopClass));
			data = map.get(stopClass);
		}
		return data;
	}

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
		BeanInfo info = null;
		try {
			info = Introspector.getBeanInfo(beanClass, stopClass);
		} catch (IntrospectionException e) {
			throw new IllegalArgumentException(
					"Invalid bean class '" + beanClass.getName() + "' or stop class '" + stopClass.getName() + "'.", e);
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

	public static void populate(Object destination, Map<String, ?> map) {
		populate(destination, map, null);
	}

	public static void populate(Object destination, Map<String, ?> map, PropertyFilter filter) {
		populate(destination, map, filter, true);
	}

	public static void populate(Object destination, Map<String, ?> map, PropertyFilter filter, boolean overwrited) {
		populate(destination, null, map, filter, overwrited);
	}

	public static void populate(Object destination, Class<?> stopClass, Map<String, ?> map, PropertyFilter filter, boolean overwrited) {
		Assert.isNull(destination, "Destination instance must not be null.");
		Map<String, PropertyDescriptor> dest = getPropertyDescriptors(destination.getClass(), stopClass, filter);
		PropertyDescriptor descriptor;
		String propertyName;
		for (Map.Entry<String, ?> entry : map.entrySet()) {
			propertyName = entry.getKey();
			descriptor = dest.get(propertyName);
			if (descriptor != null) {
				if (!overwrited) {
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
			field = FieldUtils.getFieldIfAbsent(type, propertyName);
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

	public static PropertyDescriptor getPropertyDescriptor(Class<?> beanClass, String propertyName) {
		final Map<String, PropertyDescriptor> dest = getPropertyDescriptors(beanClass, null);
		return dest.get(propertyName);
	}

	public static boolean hasProperty(Class<?> beanClass, String propertyName) {
		final Map<String, PropertyDescriptor> dest = getPropertyDescriptors(beanClass, null);
		return dest.containsKey(propertyName);
	}

	public static boolean setProperty(Object bean, PropertyDescriptor descriptor, Object value) {
		Assert.isNull(bean, "Source instance must not be null.");
		Assert.isNull(descriptor, "Property descriptor must not be null.");
		Method method = descriptor.getWriteMethod();
		Assert.isNull(method, "Cannot find the setter of '" + descriptor.getName() + "'.");
		return setProperty(bean, method, descriptor.getPropertyType(), value);
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

	public static Object getProperty(Object bean, PropertyDescriptor descriptor) {
		Assert.isNull(bean, "Source instance must not be null.");
		Assert.isNull(descriptor, "Property descriptor must not be null.");
		Method method = descriptor.getReadMethod();
		Assert.isNull(method, "Cannot find the getter of '" + descriptor.getName() + "'.");
		return invokeGetter(bean, method);
	}

	public static Object getProperty(Object bean, String propertyName) {
		Assert.isNull(bean, "Source instance must not be null.");
		Assert.hasNoText(propertyName, "PropertyName must not be null or empty.");
		final int index = propertyName.indexOf('.');
		if (index > 0) {
			bean = getProperty(bean, propertyName.substring(0, index));
			return getProperty(bean, propertyName.substring(index + 1));
		} else {
			PropertyDescriptor descriptor = getPropertyDescriptor(bean.getClass(), propertyName);
			Assert.isNull(descriptor, "Property '" + propertyName + "' is not found in class '" + bean.getClass().getName() + "'.");
			return getProperty(bean, descriptor);
		}
	}

	public static void copyProperties(Object original, Object destination) {
		copyProperties(original, destination, null);
	}

	public static void copyProperties(Object original, Object destination, PropertyFilter filter) {
		copyProperties(original, destination, filter, true);
	}

	public static void copyProperties(Object original, Object destination, PropertyFilter filter, boolean overwrited) {
		copyProperties(original, null, destination, filter, overwrited);
	}

	public static void copyProperties(Object original, Class<?> stopClass, Object destination, PropertyFilter filter, boolean overwrited) {
		copyProperties(original, stopClass, destination, filter, overwrited, false);
	}

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
		PropertyDescriptor descriptor;
		String path;
		Object value;
		for (Map.Entry<String, PropertyDescriptor> entry : dest.entrySet()) {
			path = entry.getKey();
			descriptor = entry.getValue();
			if (!overwrited) {
				Object current = getProperty(destination, descriptor);
				if (current != null) {
					continue;
				}
			}
			try {
				value = getProperty(original, path);
			} catch (RuntimeException ignored) {
				value = null;
			}
			setProperty(destination, descriptor, value);
		}
	}

	public static Map<String, Object> convertToMap(Object bean) {
		return convertToMap(bean, null);
	}

	public static Map<String, Object> convertToMap(Object bean, Class<?> stopClass) {
		return convertToMap(bean, stopClass, null);
	}

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
			return MethodUtils.invokeMethod(bean, method);
		} catch (Exception e) {
			throw new BeanPropertyAccessException(
					"Cannot access the getter '" + method.getName() + "' on bean '" + bean.getClass().getName() + "'.", e);
		}
	}

}
