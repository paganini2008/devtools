package com.github.paganini2008.devtools.beans;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.github.paganini2008.devtools.collection.MultiMapMap;
import com.github.paganini2008.devtools.converter.ConvertUtils;

/**
 * 
 * DynamicBean
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
public class DynamicBean implements Serializable {

	private static final long serialVersionUID = 2414487869249998453L;
	private final Map<String, Object> dynamicProperties = new LinkedHashMap<String, Object>();
	private final MultiMapMap<String, Integer, Object> indexedDynamicProperties = new MultiMapMap<String, Integer, Object>();
	private final MultiMapMap<String, String, Object> mappedDynamicProperties = new MultiMapMap<String, String, Object>();

	public DynamicBean setProperty(String attributeName, Object attributeValue) {
		dynamicProperties.put(attributeName, attributeValue);
		indexedDynamicProperties.remove(attributeName);
		mappedDynamicProperties.remove(attributeName);
		return this;
	}

	public DynamicBean setProperty(String attributeName, int index, Object attributeValue) {
		indexedDynamicProperties.put(attributeName, index, attributeValue);
		dynamicProperties.remove(attributeName);
		mappedDynamicProperties.remove(attributeName);
		return this;
	}

	public DynamicBean setProperty(String attributeName, String key, Object attributeValue) {
		mappedDynamicProperties.put(attributeName, key, attributeValue);
		dynamicProperties.remove(attributeName);
		indexedDynamicProperties.remove(attributeName);
		return this;
	}

	public Object getProperty(String attributeName) {
		return getProperty(attributeName, (Object) null);
	}

	public Object getProperty(String attributeName, Object defaultValue) {
		return dynamicProperties.getOrDefault(attributeName, defaultValue);
	}

	public <T> T getProperty(String attributeName, Class<T> requiredType) {
		Object result = getProperty(attributeName);
		if (result != null) {
			try {
				return requiredType.cast(result);
			} catch (RuntimeException e) {
				return ConvertUtils.convertValue(result, requiredType);
			}
		}
		return null;
	}

	public Object getProperty(String attributeName, int index) {
		return getProperty(attributeName, index, (Object) null);
	}

	public Object getProperty(String attributeName, int index, Object defaultValue) {
		return indexedDynamicProperties.get(attributeName, index, defaultValue);
	}

	public <T> T getProperty(String attributeName, int index, Class<T> requiredType) {
		Object result = getProperty(attributeName, index);
		if (result != null) {
			try {
				return requiredType.cast(result);
			} catch (RuntimeException e) {
				return ConvertUtils.convertValue(result, requiredType);
			}
		}
		return null;
	}

	public Object getProperty(String attributeName, String key) {
		return getProperty(attributeName, key, null);
	}

	public Object getProperty(String attributeName, String key, Object defaultValue) {
		return mappedDynamicProperties.get(attributeName, key, defaultValue);
	}

	public <T> T getProperty(String attributeName, String key, Class<T> requiredType) {
		Object result = getProperty(attributeName, key);
		if (result != null) {
			try {
				return requiredType.cast(result);
			} catch (RuntimeException e) {
				return ConvertUtils.convertValue(result, requiredType);
			}
		}
		return null;
	}

	public static DynamicBean wrap(Map<String, Object> kwargs) {
		DynamicBean bean = new DynamicBean();
		for (Map.Entry<String, Object> entry : kwargs.entrySet()) {
			setProperty(bean, entry.getKey(), entry.getValue());
		}
		return bean;
	}

	public static DynamicBean wrap(Object object) {
		Map<String, Object> kwargs = PropertyUtils.convertToMap(object);
		return wrap(kwargs);
	}

	private static void setProperty(DynamicBean bean, String attributeName, Object value) {
		if (value instanceof Collection) {
			int index = 0;
			for (Object element : (Collection<?>) value) {
				bean.setProperty(attributeName, index++, element);
			}
		} else if (value instanceof Map) {
			for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
				bean.setProperty(attributeName, (String) entry.getKey(), entry.getValue());
			}
		} else {
			bean.setProperty(attributeName, value);
		}
	}

}
