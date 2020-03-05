package com.github.paganini2008.devtools.db4j;

import java.lang.reflect.Type;

import com.github.paganini2008.devtools.db4j.type.TypeHandler;

/**
 * TypeHandlerRegistry
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface TypeHandlerRegistry {

	TypeHandler getTypeHandler(Type javaType);

	TypeHandler getTypeHandler(Type javaType, JdbcType jdbcType);

	void register(JdbcType jdbcType, TypeHandler typeHandler);

	void register(Type javaType, TypeHandler typeHandler);

	void register(Type javaType, JdbcType jdbcType, TypeHandler typeHandler);

}
