package com.github.paganini2008.springworld.fastjpa.support;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Tuple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.beans.PropertyMapper;
import com.github.paganini2008.devtools.beans.PropertyUtils;
import com.github.paganini2008.devtools.reflection.FieldFilters;
import com.github.paganini2008.devtools.reflection.FieldUtils;
import com.github.paganini2008.springworld.fastjpa.AbstractTransformer;
import com.github.paganini2008.springworld.fastjpa.JpaAttributeDetail;
import com.github.paganini2008.springworld.fastjpa.Model;

/**
 * 
 * BeanTransformer
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-03
 * @version 1.0
 */
public class BeanTransformer<E, T> extends AbstractTransformer<E, T> {

	private static final Logger logger = LoggerFactory.getLogger(BeanTransformer.class);
	private final BeanReflection<T> beanReflection;

	public BeanTransformer(Class<T> resultClass, String... includedProperties) {
		this.beanReflection = new BeanReflection<T>(resultClass, includedProperties);
		initialize(resultClass);
	}

	private final Map<String, String> attributeNames = new HashMap<String, String>();
	private final Map<String, Field> attributeFields = new HashMap<String, Field>();

	private void initialize(Class<T> resultClass) {
		List<Field> fields = FieldUtils.getFields(resultClass, FieldFilters.isAnnotationPresent(PropertyMapper.class));
		for (Field field : fields) {
			PropertyMapper propertyMapper = field.getAnnotation(PropertyMapper.class);
			String path = propertyMapper.value();
			if (StringUtils.isNotBlank(path)) {
				String key = path;
				String value = path;
				int index;
				if ((index = path.indexOf(".")) > 0) {
					key = path.substring(0, index);
				}
				attributeNames.put(key, value);
				attributeFields.put(key, field);
			}
		}
	}

	protected void setAttributeValue(Model<E> model, String attributeName, Class<?> javaType, Tuple tuple, T object) {
		final Object result = tuple.get(attributeName);
		if (model.isManaged(javaType)) {
			String realAttribute;
			Object attributeValue;
			for (JpaAttributeDetail attributeDetail : model.getAttributeDetails(attributeName)) {
				realAttribute = attributeDetail.getName();
				try {
					if (attributeNames.containsKey(realAttribute)) {
						attributeValue = PropertyUtils.getProperty(result, attributeNames.get(realAttribute));
						beanReflection.setProperty(object, attributeFields.get(realAttribute).getName(), attributeValue);
					} else {
						attributeValue = PropertyUtils.getProperty(result, realAttribute);
						beanReflection.setProperty(object, realAttribute, attributeValue);
					}
				} catch (Exception e) {
					if (logger.isTraceEnabled()) {
						logger.trace(e.getMessage(), e);
					}
				}
			}
		} else {
			Object attributeValue = result;
			try {
				if (attributeNames.containsKey(attributeName)) {
					attributeValue = PropertyUtils.getProperty(result, attributeNames.get(attributeName));
					beanReflection.setProperty(object, attributeFields.get(attributeName).getName(), attributeValue);
				} else {
					beanReflection.setProperty(object, attributeName, attributeValue);
				}
			} catch (Exception e) {
				if (logger.isTraceEnabled()) {
					logger.trace(e.getMessage(), e);
				}
			}
		}
	}

	protected T createObject(int columns) {
		return beanReflection.instantiateBean();
	}
}
