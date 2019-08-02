package com.github.paganini2008.devtools.beans;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * Mapping
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-05
 * @version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD })
public @interface Mapping {

	String value();

}
