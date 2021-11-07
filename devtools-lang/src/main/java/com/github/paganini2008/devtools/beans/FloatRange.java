package com.github.paganini2008.devtools.beans;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * FloatRange
 *
 * @author Fred Feng
 *
 * @since 2.0.4
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface FloatRange {

	float from() default Float.MIN_VALUE;

	float to() default Float.MAX_VALUE;

	int precision() default -1;

	int scale() default 6;
	
	String value() default "";

}
