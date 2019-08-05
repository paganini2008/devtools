package com.allyes.components.config;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import com.allyes.developer.utils.EventListener;
import com.allyes.developer.utils.ObjectUtils;
import com.allyes.developer.utils.StringUtils;
import com.allyes.developer.utils.SystemPropertyUtils;
import com.allyes.developer.utils.collection.CollectionUtils;
import com.allyes.developer.utils.collection.MapUtils;
import com.allyes.developer.utils.io.PropertyChangeEvent;
import com.allyes.developer.utils.regex.RegexUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * ApplicationPropertiesKeeper
 * 
 * @author Fred Feng
 * @created 2019-03
 */
@Slf4j
public class ApplicationPropertiesKeeper implements BeanPostProcessor, EnvironmentAware, ApplicationContextAware {

	private static final String loggingMarker = "slf4j.application.properties.keeper.mark";
	private static final Marker markerFactory = MarkerFactory.getMarker(SystemPropertyUtils.getString(loggingMarker, "default"));
	private static final String PATTERN_PLACEHOLDER = "\\$\\{(.*)\\}";
	private ApplicationProperties applicationProperties;
	private Environment environment;
	private ApplicationContext applicationContext;

	public void setApplicationProperties(ApplicationProperties applicationProperties) {
		this.applicationProperties = applicationProperties;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
		final Class<?> beanClass = bean.getClass();
		if (beanClass.isAnnotationPresent(ConfigurationProperties.class)) {
			ConfigurationProperties configurationProperties = beanClass.getAnnotation(ConfigurationProperties.class);
			final String prefix = configurationProperties.prefix();
			List<Field> fields = FieldUtils.getFieldsListWithAnnotation(bean.getClass(), Keeping.class);
			List<String> keys = new ArrayList<String>();
			for (Field field : fields) {
				String key = prefix + "." + field.getName();
				keys.add(key);
				watchingFieldChange(key, field, bean, beanName);
			}
			if (CollectionUtils.isNotEmpty(fields)) {
				watchingDeclaredObjectChange(keys.toArray(new String[0]), beanClass, beanName);
			}
		} else {
			List<Field> fields = FieldUtils.getFieldsListWithAnnotation(beanClass, Keeping.class);
			List<String> keys = new ArrayList<String>();
			for (Field field : fields) {
				if (field.isAnnotationPresent(Value.class)) {
					Value value = field.getAnnotation(Value.class);
					String represent = value.value();
					String key = resolvePlaceholder(represent);
					keys.add(key);
					watchingFieldChange(key, field, bean, beanName);
				} else {
					Keeping keeping = field.getAnnotation(Keeping.class);
					if (StringUtils.isNotBlank(keeping.value())) {
						String key = keeping.value();
						keys.add(key);
						watchingFieldChange(key, field, bean, beanName);
					}
				}
			}
			if (CollectionUtils.isNotEmpty(fields)) {
				watchingDeclaredObjectChange(keys.toArray(new String[0]), beanClass, beanName);
			}
		}
		return bean;
	}

	private String resolvePlaceholder(String represent) {
		String key = RegexUtils.match(represent, PATTERN_PLACEHOLDER, 0, 1, 0);
		int index = key.indexOf(':');
		if (index > 0) {
			key = key.substring(0, index);
		}
		return key;
	}

	private void watchingDeclaredObjectChange(String[] keys, Object bean, String beanName) {
		applicationProperties.addEventListener(event -> {
			Properties latest = event.getLatest();
			Properties current = event.getCurrent();
			Map<Object, Object> difference = MapUtils.compareEachOther(latest, current);
			if (MapUtils.isNotEmpty(difference)) {
				for (String key : keys) {
					if (difference.containsKey(key)) {
						applicationContext.publishEvent(new BeanChangeEvent(bean, beanName));
						log.info(markerFactory, "[BeanPropertyChange] Publish BeanChangeEvent OK.");
						break;
					}
				}
			}
		});
	}

	private void watchingFieldChange(String key, Field field, Object bean, String beanName) {
		final String propertyName = field.getName();
		applicationProperties.addEventListener(new EventListener<PropertyChangeEvent<Properties>>() {

			public void onEventFired(PropertyChangeEvent<Properties> event) {
				Properties latest = event.getLatest();
				Properties current = event.getCurrent();
				Map<Object, Object> difference = MapUtils.compareEachOther(latest, current);
				if (difference != null && difference.containsKey(key)) {
					Object previousValue = null;
					Object currentValue = environment.getProperty(key, field.getType());
					try {
						previousValue = FieldUtils.readField(field, bean, true);
						FieldUtils.writeField(bean, propertyName, currentValue, true);
						currentValue = FieldUtils.readField(field, bean, true);
						if (ObjectUtils.equals(previousValue, currentValue)) {
							log.warn(markerFactory, "[BeanPropertyChange] It seems true that property name '" + propertyName
									+ "' overridden by other PropertySource. So there is nothing to happen on the property.");
						} else {
							log.info(markerFactory, "[BeanPropertyChange] propertyName: " + propertyName + ", previousValue: "
									+ previousValue + ", currentValue: " + currentValue);
						}
						applicationContext
								.publishEvent(new BeanPropertyChangeEvent(bean, beanName, propertyName, previousValue, currentValue));
						log.info(markerFactory, "[BeanPropertyChange] Publish BeanPropertyChangeEvent OK.");
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}

				}
			}
		});
		log.info("Find Keeping Key {} for {}.{}", key, bean.getClass().getName(), field.getName());
	}

}
