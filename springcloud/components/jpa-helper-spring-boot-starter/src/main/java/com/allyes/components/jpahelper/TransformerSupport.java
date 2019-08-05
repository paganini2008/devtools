package com.allyes.components.jpahelper;

import javax.persistence.Tuple;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * TransformerSupport
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-02
 * @version 1.0
 */
public abstract class TransformerSupport<E, T> extends AbstractTransformer<E, T> {

	private static final Logger logger = LoggerFactory.getLogger(TransformerSupport.class);

	protected void setAttributeValue(Model<E> model, String attributeName, Class<?> javaType, Tuple tuple, T object) {
		final Object result = tuple.get(attributeName);
		if (model.isManaged(javaType)) {
			String realAttribute;
			Object attributeValue;
			for (JpaAttributeDetail attributeDetail : model.getAttributeDetails(attributeName)) {
				realAttribute = attributeDetail.getName();
				try {
					attributeValue = PropertyUtils.getProperty(result, realAttribute);
					setAsValue(realAttribute, attributeValue, object);
				} catch (Exception ignored) {
					if (logger.isTraceEnabled()) {
						logger.trace("Attribute '{}' cannot be set value.", javaType + "#" + realAttribute);
					}
				}
			}
		} else {
			setAsValue(attributeName, result, object);
		}
	}

	protected abstract void setAsValue(String attributeName, Object attributeValue, T object);
}
