package com.github.paganini2008.springworld.jdbc.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * Arg
 *
 * @author Fred Feng
 * @created 2019-10
 * @revised 2020-01
 * @version 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Arg {

	String value();
	
}
