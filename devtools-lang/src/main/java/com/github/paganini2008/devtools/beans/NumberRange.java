package com.github.paganini2008.devtools.beans;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * NumberRange
 *
 * @author Fred Feng
 *
 * @since 2.0.4
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface NumberRange {

	double from() default Double.MIN_VALUE;

	double to() default Double.MAX_VALUE;

	int precision() default -1;

	int scale() default 0;
	
	String value() default "";

}
