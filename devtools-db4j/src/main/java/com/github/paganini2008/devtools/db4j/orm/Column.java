package com.github.paganini2008.devtools.db4j.orm;

import com.github.paganini2008.devtools.db4j.JdbcType;

/**
 * 
 * Column
 *
 * @author Fred Feng
 * @since 2.0.5
 */
public @interface Column {

	String name() default "";

	JdbcType jdbcType() default JdbcType.AUTO;

	int length() default -1;

	boolean nullable() default true;

	boolean primaryKey() default false;

	boolean autoIncrement() default false;

	boolean unique() default false;
	
	String comment() default "";

	String value() default "";
}
