package com.github.paganini2008.devtools.beans;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * Recur
 *
 * @author Fred Feng
 *
 * @since 2.0.4
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface Recur {

}
