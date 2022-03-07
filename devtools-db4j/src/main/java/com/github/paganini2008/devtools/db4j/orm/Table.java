package com.github.paganini2008.devtools.db4j.orm;

/**
 * 
 * Table
 *
 * @author Fred Feng
 * @since 2.0.5
 */
public @interface Table {

	String value() default "";

	String catalog() default "";

	String schema() default "";

	String comment() default "";

}
