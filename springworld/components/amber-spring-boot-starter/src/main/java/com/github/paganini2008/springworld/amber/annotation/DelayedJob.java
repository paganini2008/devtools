package com.github.paganini2008.springworld.amber.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

/**
 * 
 * DelayedJob
 * 
 * @author Fred Feng
 * @created 2018-03
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface DelayedJob {

	String name() default "";

	long delay();

	TimeUnit timeUnit() default TimeUnit.SECONDS;

	String description() default "";
}
