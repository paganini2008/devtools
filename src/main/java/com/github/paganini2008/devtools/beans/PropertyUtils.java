package com.github.paganini2008.devtools.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.collection.LruMap;
import com.github.paganini2008.devtools.converter.TypeConverter;
import com.github.paganini2008.devtools.reflection.MethodUtils;

/**
 * 
 * PropertyUtils
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
public class PropertyUtils {

	private PropertyUtils() {
	}

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
		if (original != null) {
			Map<String, PropertyDescriptor> destination = new LinkedHashMap<String, PropertyDescriptor>();
			for (Map.Entry<String, PropertyDescriptor> e : original.entrySet()) {
				if (filter == null || filter.accept(e.getKey(), e.getValue())) {
					destination.put(e.getKey(), e.getValue());
				}
			}
			return destination;
		}
		return null;
	}

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
			Map<String, PropertyDescriptor> data = new HashMap<String, PropertyDescriptor>(descriptors.length);
			for (PropertyDescriptor descriptor : descriptors) {
				data.put(descriptor.getName(), descriptor);
			}
			return data;
		}
		return null;
	}

	public static void populate(Object destination, Map<String, ?> map) {
		populate(destination, map, null);
	}

	public static void populate(Object destination, Map<String, ?> map, PropertyFilter filter) {
		populate(destination, map, filter, true);
	}

	public static void populate(Object destination, Map<String, ?> map, PropertyFilter filter, boolean overwrite) {
		populate(destination, map, filter, overwrite, null);
	}

	public static void populate(Object destination, Map<String, ?> map, PropertyFilter filter, boolean overwrite,
			TypeConverter typeConverter) {
		populate(destination, null, map, filter, overwrite, typeConverter);
	}

	public static void populate(Object destination, Class<?> stopClass, Map<String, ?> map, PropertyFilter filter, boolean overwrite,
			TypeConverter typeConverter) {
		Assert.isNull(destination, "Destination instance must not be null.");
		Map<String, PropertyDescriptor> destMap = getMappedPropertyDescriptors(destination.getClass(), stopClass, filter);
		PropertyDescriptor desc;
		String propertyName;
		Method method;
		for (Map.Entry<String, ?> e : map.entrySet()) {
			propertyName = e.getKey();
			if (null != (desc = destMap.get(propertyName))) {
				if (overwrite) {
					method = desc.getReadMethod();
					if (method != null) {
						Object oldValue = callGetter(destination, method);
						if (oldValue != null) {
							continue;
						}
					}
				}
				method = desc.getWriteMethod();
				if (method != null) {
					setProperty(destination, method, desc.getPropertyType(), e.getValue(), typeConverter);
				}
			}
		}
	}

	public static Map<String, PropertyDescriptor> getMappedPropertyDescriptors(Class<?> type, Class<?> stopClass, PropertyFilter filter) {
		Map<String, PropertyDescriptor> descriptors = getPropertyDescriptors(type, stopClass, filter);
		if (descriptors != null) {
			Map<String, PropertyDescriptor> results = new LinkedHashMap<String, PropertyDescriptor>();
			Class<?> propertyType;
			for (Map.Entry<String, PropertyDescriptor> e : descriptors.entrySet()) {
				propertyType = e.getValue().getPropertyType();
				if (propertyType.isAnnotationPresent(Excluded.class)) {
					continue;
				}
				Mapper m = type.getAnnotation(Mapper.class);
				if (m == null) {
					results.put(e.getKey(), e.getValue());
				} else {
					results.put(StringUtils.isBlank(m.value()) ? e.getKey() : m.value(), e.getValue());
				}
			}
			return results;
		}
		return null;
	}

	public static PropertyDescriptor getPropertyDescriptor(Class<?> beanClass, String propertyName) {
		final Map<String, PropertyDescriptor> descMap = getPropertyDescriptors(beanClass, null);
		return descMap.get(propertyName);
	}

	public static boolean hasProperty(Class<?> beanClass, String propertyName) {
		final Map<String, PropertyDescriptor> descMap = getPropertyDescriptors(beanClass, null);
		return descMap.containsKey(propertyName);
	}

	public static boolean setProperty(Object bean, PropertyDescriptor descriptor, Object value, TypeConverter typeConverter) {
		Assert.isNull(bean, "Source instance must not be null.");
		Assert.isNull(descriptor, "Property descriptor must not be null.");
		Method method = descriptor.getWriteMethod();
		Assert.isNull(method, "Cannot find the setter of '" + descriptor.getName() + "'.");
		return setProperty(bean, method, descriptor.getPropertyType(), value, typeConverter);
	}

	private static boolean setProperty(Object bean, Method method, Class<?> propertyType, Object value, TypeConverter typeConverter) {
		Object realValue = value;
		if (typeConverter != null) {
			realValue = typeConverter.convert(value, propertyType, null);
		}
		if (realValue == null && propertyType.isPrimitive()) {
			return false;
		}
		callSetter(bean, method, realValue);
		return true;
	}

	public static boolean setProperty(Object bean, String property, Object value, TypeConverter typeConverter) {
		final int index = property.indexOf(".");
		if (index != -1) {
			bean = getProperty(bean, property.substring(0, index));
			return setPropertyNested(bean, property.substring(index + 1), value, typeConverter);
		} else {
			return setPropertyNested(bean, property, value, typeConverter);
		}
	}

	private static boolean setPropertyNested(Object bean, String property, Object value, TypeConverter typeConverter) {
		Assert.isNull(bean, "Source instance must not be null.");
		PropertyDescriptor descriptor = getPropertyDescriptor(bean.getClass(), property);
		Assert.isNull(descriptor, "Property '" + property + "' is not found in class '" + bean.getClass().getName() + "'.");
		return setProperty(bean, descriptor, value, typeConverter);
	}

	public static Object getProperty(Object bean, PropertyDescriptor descriptor) {
		Assert.isNull(bean, "Source instance must not be null.");
		Assert.isNull(descriptor, "Property descriptor must not be null.");
		Method method = descriptor.getReadMethod();
		Assert.isNull(method, "Cannot find the getter of '" + descriptor.getName() + "'.");
		return callGetter(bean, method);
	}

	public static Object getProperty(Object bean, String propertyName) {
		Assert.hasNoText(propertyName, "PropertyName must not be null or empty.");
		final int index = propertyName.indexOf(".");
		if (index != -1) {
			bean = getPropertyNested(bean, propertyName.substring(0, index));
			return getProperty(bean, propertyName.substring(index + 1));
		} else {
			return getPropertyNested(bean, propertyName);
		}
	}

	private static Object getPropertyNested(Object bean, String property) {
		Assert.isNull(bean, "Source instance must not be null.");
		PropertyDescriptor descriptor = getPropertyDescriptor(bean.getClass(), property);
		Assert.isNull(descriptor, "Property '" + property + "' is not found in class '" + bean.getClass().getName() + "'.");
		return getProperty(bean, descriptor);
	}

	public static void copyProperties(Object original, Object destination) {
		copyProperties(original, destination, null);
	}

	public static void copyProperties(Object original, Object destination, PropertyFilter filter) {
		copyProperties(original, destination, filter, true);
	}

	public static void copyProperties(Object original, Object destination, PropertyFilter filter, boolean overwrite) {
		copyProperties(original, destination, filter, overwrite, null);
	}

	public static void copyProperties(Object original, Object destination, PropertyFilter filter, boolean overwrite,
			TypeConverter typeConverter) {
		copyProperties(original, null, destination, filter, overwrite, typeConverter);
	}

	public static void copyProperties(Object original, Class<?> stopClass, Object destination, PropertyFilter filter, boolean overwrite,
			TypeConverter typeConverter) {
		Assert.isNull(original, "Source instance must not be null.");
		Assert.isNull(destination, "Destination instance must not be null.");
		Map<String, PropertyDescriptor> srcMap = getPropertyDescriptors(original.getClass(), stopClass, filter);
		Map<String, PropertyDescriptor> destMap = getMappedPropertyDescriptors(destination.getClass(), null, null);
		doCopyProperties(original, srcMap, destination, destMap, overwrite, typeConverter);
	}

	private static void doCopyProperties(Object original, Map<String, PropertyDescriptor> srcMap, Object destination,
			Map<String, PropertyDescriptor> destMap, boolean overwrite, TypeConverter typeConverter) {
		PropertyDescriptor desc;
		String propertyName;
		Object value;
		Method method;
		for (Map.Entry<String, PropertyDescriptor> e : srcMap.entrySet()) {
			propertyName = e.getKey();
			if (null != (desc = destMap.get(propertyName))) {
				method = e.getValue().getReadMethod();
				if (method != null) {
					value = callGetter(original, method);
					if (overwrite) {
						method = desc.getReadMethod();
						if (method != null) {
							Object oldValue = callGetter(destination, method);
							if (oldValue != null) {
								continue;
							}
						}
					}
					method = desc.getWriteMethod();
					if (method != null) {
						setProperty(destination, method, desc.getPropertyType(), value, typeConverter);
					}
				}
			}
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
		Map<String, PropertyDescriptor> descMap = getPropertyDescriptors(bean.getClass(), stopClass, filter);
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		PropertyDescriptor descriptor = null;
		Method method = null;
		Object value = null;
		for (Map.Entry<String, PropertyDescriptor> entry : descMap.entrySet()) {
			descriptor = entry.getValue();
			method = descriptor.getReadMethod();
			if (method != null) {
				value = callGetter(bean, method);
			}
			map.put(entry.getKey(), value);
		}
		return map;
	}

	private static Object callSetter(Object bean, Method method, Object value) {
		try {
			return MethodUtils.invokeMethod(bean, method, value);
		} catch (Exception e) {
			throw new BeanPropertyAccessException(
					"Cannot access the setter '" + method.getName() + "' on bean '" + bean.getClass().getName() + "'.", e);
		}
	}

	private static Object callGetter(Object bean, Method method) {
		try {
			return MethodUtils.invokeMethod(bean, method);
		} catch (Exception e) {
			throw new BeanPropertyAccessException(
					"Cannot access the getter '" + method.getName() + "' on bean '" + bean.getClass().getName() + "'.", e);
		}
	}

}
