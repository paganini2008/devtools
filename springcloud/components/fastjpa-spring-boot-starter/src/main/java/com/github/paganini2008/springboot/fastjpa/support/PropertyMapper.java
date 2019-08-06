package com.github.paganini2008.springboot.fastjpa.support;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 
 * PropertyMapper
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-02
 * @version 1.0
 */
@Target({ FIELD })
@Retention(RUNTIME)
public @interface PropertyMapper {

	String value();

}
