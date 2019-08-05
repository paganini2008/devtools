package com.allyes.components.jpahelper.support;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		Field[] fields = FieldUtils.getFieldsWithAnnotation(requiredType, PropertyMapper.class);
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
