package com.github.paganini2008.springworld.scheduler;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * 
 * Job
 * 
 * @author Fred Feng
 * @created 2019-11
 * @version 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Job {

	String name() default "";
	
	String cron();

	String description() default "";

}
