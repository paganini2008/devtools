package com.github.paganini2008.springworld.amber.config;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class TestImportSelector implements ImportSelector {

	private static final String[] classNames = new String[] {};

	public String[] selectImports(AnnotationMetadata importingClassMetadata) {
		return classNames;
	}

}
