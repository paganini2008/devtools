package com.github.paganini2008.springboot.fastjpa.support;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.paganini2008.devtools.beans.PropertyUtils;
import com.github.paganini2008.devtools.reflection.ConstructorUtils;
import com.github.paganini2008.devtools.reflection.FieldFilters;
import com.github.paganini2008.devtools.reflection.FieldUtils;

/**
 * 
 * BeanReflection
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-02
 * @version 1.0
 */
public class BeanReflection<T> {

	private static final Logger logger = LoggerFactory.getLogger(BeanReflection.class);

	public BeanReflection(Class<T> requiredType, String... includedProperties) {
		this.requiredType = requiredType;
		if (includedProperties != null) {
			includedPropertyNames.addAll(Arrays.asList(includedProperties));
		}
		initialize();
	}

	private final Class<T> requiredType;
	private final Set<String> includedPropertyNames = new HashSet<String>();
	private final Map<String, String> propertyNameMapper = new HashMap<String, String>();

	private void initialize() {
		List<Field> fields = FieldUtils.getFields(requiredType, FieldFilters.isAnnotationPresent(PropertyMapper.class));
		if (fields != null) {
			for (Field field : fields) {
				PropertyMapper mapper = field.getAnnotation(PropertyMapper.class);
				propertyNameMapper.put(mapper.value(), field.getName());
			}
		}
	}

	public void setProperty(T object, String attributeName, Object attributeValue) {
		final String propertyName = mapPropertyName(attributeName);
		if (!hasPropertyValue(propertyName)) {
			return;
		}
		try {
			PropertyUtils.setProperty(object, propertyName, attributeValue);
		} catch (Exception e) {
			try {
				FieldUtils.writeField(object, propertyName, attributeValue);
			} catch (Exception ignored) {
				if (logger.isTraceEnabled()) {
					logger.trace("Attribute '{}' cannot be set value.", requiredType.getName() + "#" + propertyName);
				}
			}
		}
	}

	public T instantiateBean() {
		try {
			return ConstructorUtils.invokeConstructor(requiredType, (Object[]) null);
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	private boolean hasPropertyValue(String propertyName) {
		return includedPropertyNames.isEmpty() || includedPropertyNames.contains(propertyName);
	}

	private String mapPropertyName(String propertyName) {
		return propertyNameMapper.containsKey(propertyName) ? propertyNameMapper.get(propertyName) : propertyName;
	}

}
