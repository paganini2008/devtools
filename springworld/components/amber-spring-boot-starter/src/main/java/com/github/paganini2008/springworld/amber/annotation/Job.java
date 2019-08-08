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
 * Job
 * 
 * @author Fred Feng
 * @created 2019-03
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Job {

	String name() default "";

	String description() default "";

	int repeatCount() default 0;

	long interval();

	TimeUnit intervalTimeUnit() default TimeUnit.SECONDS;

	long delay();

	TimeUnit delayTimeUnit() default TimeUnit.SECONDS;
}
