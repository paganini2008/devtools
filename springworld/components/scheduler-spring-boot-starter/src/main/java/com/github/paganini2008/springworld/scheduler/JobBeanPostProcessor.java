package com.github.paganini2008.springworld.scheduler;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import com.github.paganini2008.devtools.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * JobBeanPostProcessor
 * 
 * @author Fred Feng
 * @created 2019-11
 * @revised 2019-11
 * @version 1.0
 */
@Slf4j
@Component
public class JobBeanPostProcessor implements BeanPostProcessor {

	@Autowired
	private JobManager jobManager;

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean.getClass().isAnnotationPresent(Job.class)) {
			Job info = bean.getClass().getAnnotation(Job.class);
			try {
				jobManager.scheduleJob(bean, StringUtils.isNotBlank(info.name()) ? info.name() : beanName, info.description(),
						info.value());
			} catch (Exception e) {
				log.warn("Unable to schedule job: {}/{}, because of the reason '{}'", info.name(), bean.getClass().getName(),
						e.getMessage());
			}
		}
		return bean;
	}

}
