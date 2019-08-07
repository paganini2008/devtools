package com.github.paganini2008.devtools.beans;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * ExcludedProperty
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @created 2014-05
 * @version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface ExcludedProperty {
}