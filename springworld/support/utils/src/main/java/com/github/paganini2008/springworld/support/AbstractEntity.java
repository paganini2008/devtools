package com.github.paganini2008.springworld.support;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.paganini2008.devtools.ClassUtils;
import com.github.paganini2008.devtools.beans.PropertyMapper;
import com.github.paganini2008.devtools.beans.PropertyUtils;
import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.reflection.ConstructorUtils;
import com.github.paganini2008.devtools.reflection.FieldFilters;
import com.github.paganini2008.devtools.reflection.FieldUtils;
import com.github.paganini2008.springworld.fastjpa.support.InjectionHandler;
import com.github.paganini2008.springworld.fastjpa.support.ReferenceId;

/**
 * 
 * AbstractEntity
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-03
 * @version 1.0
 */
@SuppressWarnings("all")
public abstract class AbstractEntity implements Serializable {

	private static final Logger logger = LoggerFactory.getLogger(AbstractEntity.class);
	private static final List<Class<?>> acceptedExtraPropertyTypes = Collections
			.unmodifiableList(Arrays.asList(String.class, BigDecimal.class, BigInteger.class, Date.class, Enum.class));
	private static final ConcurrentMap<Class<?>, InjectionHandler> injectionHandlers = new ConcurrentHashMap<Class<?>, InjectionHandler>();

	public Map<String, Object> asMap() {
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		for (PropertyDescriptor pd : getAccessablePropertyDescriptors(getClass())) {
			try {
				data.put(pd.getName(), PropertyUtils.getProperty(this, pd.getName()));
			} catch (Exception ignored) {
				if (logger.isWarnEnabled()) {
					logger.warn(ignored.getMessage());
				}
			}
		}
		return data;
	}

	public <T> T asBean(Class<T> resultClass) {
		T other;
		try {
			other = (T) ConstructorUtils.invokeConstructor(resultClass, (Object[]) null);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		Object value;
		String path;
		for (PropertyDescriptor pd : getAccessablePropertyDescriptors(resultClass)) {
			path = pd.getName();
			try {
				value = PropertyUtils.getProperty(this, path);
				PropertyUtils.setProperty(other, pd.getName(), value);
			} catch (Exception ignored) {
				if (logger.isWarnEnabled()) {
					logger.warn(ignored.getMessage());
				}
			}
		}
		List<Field> fields = FieldUtils.getFields(resultClass, FieldFilters.isAnnotationPresent(PropertyMapper.class));
		for (Field field : fields) {
			PropertyMapper propertyMapper = field.getAnnotation(PropertyMapper.class);
			path = propertyMapper.value();
			try {
				value = PropertyUtils.getProperty(this, path);
				PropertyUtils.setProperty(other, field.getName(), value);
			} catch (Exception ignored) {
				if (logger.isWarnEnabled()) {
					logger.warn(ignored.getMessage());
				}
			}
		}
		return other;
	}

	public void fill(Object anotherObject) {
		Object value = null;
		for (PropertyDescriptor pd : getAccessablePropertyDescriptors(getClass())) {
			try {
				value = PropertyUtils.getProperty(anotherObject, pd.getName());
				PropertyUtils.setProperty(this, pd.getName(), value);
			} catch (Exception ignored) {
				if (logger.isWarnEnabled()) {
					logger.warn(ignored.getMessage());
				}
			}
		}
		final Class<?> targetClass = anotherObject.getClass();
		List<Field> fields = FieldUtils.getFields(targetClass, FieldFilters.isAnnotationPresent(ReferenceId.class));
		for (Field field : fields) {
			ReferenceId propertyInjection = field.getAnnotation(ReferenceId.class);
			try {
				PropertyDescriptor targetPd = PropertyUtils.getPropertyDescriptor(this.getClass(), propertyInjection.targetProperty());
				Object original = PropertyUtils.getProperty(anotherObject, field.getName());
				InjectionHandler injectionHandler = instantiateInjectionHandler(propertyInjection.using());
				Object convertion = injectionHandler.inject(original, targetPd.getName(), targetPd.getPropertyType());
				PropertyUtils.setProperty(this, targetPd.getName(), convertion);
			} catch (Exception ignored) {
				if (logger.isWarnEnabled()) {
					logger.warn(ignored.getMessage());
				}
			}
		}
	}

	private static InjectionHandler instantiateInjectionHandler(Class<?> handlerClass) {
		InjectionHandler injectionHandler = injectionHandlers.get(handlerClass);
		if (injectionHandler == null) {
			injectionHandlers.putIfAbsent(handlerClass, (InjectionHandler) ConstructorUtils.invokeConstructor(handlerClass));
			injectionHandler = injectionHandlers.get(handlerClass);
		}
		return injectionHandler;
	}

	private PropertyDescriptor[] getAccessablePropertyDescriptors(Class<?> beanClass) {
		List<PropertyDescriptor> results = new ArrayList<PropertyDescriptor>();
		Map<String, PropertyDescriptor> descriptors = PropertyUtils.getPropertyDescriptors(beanClass);
		if (MapUtils.isNotEmpty(descriptors)) {
			for (PropertyDescriptor pd : descriptors.values()) {
				Class<?> propertyType = pd.getPropertyType();
				if (acceptedPropertyType(propertyType)) {
					results.add(pd);
				}
			}
		}
		return results.toArray(new PropertyDescriptor[0]);
	}

	protected boolean acceptedPropertyType(Class<?> propertyType) {
		if (ClassUtils.isPrimitiveOrWrapper(propertyType)) {
			return true;
		}
		for (Class<?> cl : acceptedExtraPropertyTypes) {
			if (cl == propertyType || propertyType.isAssignableFrom(cl)) {
				return true;
			}
		}
		return false;
	}

}
