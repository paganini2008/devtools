package com.github.paganini2008.devtools.jdbc;

import java.lang.reflect.Type;

import com.github.paganini2008.devtools.jdbc.type.TypeHandler;

/**
 * TypeHandlerRegistry
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface TypeHandlerRegistry {

	TypeHandler getTypeHandler(Type type);

	TypeHandler getTypeHandler(Type javaType, JdbcType jdbcType);

	void register(JdbcType jdbcType, TypeHandler typeHandler);

	void register(Type javaType, TypeHandler typeHandler);

	void register(Type javaType, JdbcType jdbcType, TypeHandler typeHandler);

}
