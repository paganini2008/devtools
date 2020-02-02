package com.github.paganini2008.springworld.jdbc;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import com.github.paganini2008.springworld.jdbc.annotations.DaoScan;

/**
 * 
 * DaoScannerRegistrar
 *
 * @author Fred Feng
 * @created 2019-10
 * @revised 2020-01
 * @version 1.0
 */
public class DaoScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

	private ResourceLoader resourceLoader;

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		ClassPathDaoScanner scanner = new ClassPathDaoScanner(registry);
		if (resourceLoader != null) {
			scanner.setResourceLoader(resourceLoader);
		}
		AnnotationAttributes annotationAttributes = AnnotationAttributes
				.fromMap(importingClassMetadata.getAnnotationAttributes(DaoScan.class.getName()));
		List<String> basePackages = new ArrayList<String>();
		if (annotationAttributes.containsKey("basePackages")) {
			for (String pkg : annotationAttributes.getStringArray("basePackages")) {
				if (StringUtils.hasText(pkg)) {
					basePackages.add(pkg);
				}
			}
		}
		scanner.scan(StringUtils.toStringArray(basePackages));
	}

}
