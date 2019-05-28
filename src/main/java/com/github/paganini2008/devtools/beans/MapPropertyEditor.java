package com.github.paganini2008.devtools.beans;

import java.util.Map;

import com.github.paganini2008.devtools.collection.MapUtils;

/**
 * MapPropertyEditor
 * 
 * @author Fred Feng
 * @version 1.0
 */

@SuppressWarnings("unchecked")
public class MapPropertyEditor implements PropertyEditor<Map<String, Object>> {

	public <T> T getValue(Map<String, Object> object, String propertyName) {
		return getValue(object, propertyName, null);
	}

	public <T> T getValue(Map<String, Object> object, String propertyName, T defaultValue) {
		return (T) MapUtils.get(object, propertyName, defaultValue);
	}
}
