package com.github.paganini2008.devtools.beans;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * Example
 *
 * @author Fred Feng
 *
 * @since 2.0.4
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface Example {

	int length() default 32;

	String prefix() default "";

	String suffix() default "";

	boolean digit() default true;

	boolean lowerCaseLetter() default true;

	boolean upperCaseLetter() default true;

	String style() default "";

	String value() default "";
}
