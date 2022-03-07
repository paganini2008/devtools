package com.github.paganini2008.devtools.db4j.orm;

import java.lang.reflect.Type;

/**
 * 
 * Feature
 *
 * @author Fred Feng
 * @since 2.0.5
 */
public abstract class Feature {

	public abstract String getColumnType(Type javaType);

}
