package com.github.paganini2008.devtools.db4j;

import java.util.HashMap;
import java.util.Map;

/**
 * AbstractSqlParameter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public abstract class AbstractSqlParameter implements SqlType {

	private final Map<String, JdbcType> jdbcTypeMap = new HashMap<String, JdbcType>();

	public void addJdbcType(String paramName, JdbcType jdbcType) {
		if (jdbcType == null) {
			jdbcType = JdbcType.OTHER;
		}
		jdbcTypeMap.put(paramName, jdbcType);
	}

	public JdbcType getJdbcType(String paramName) {
		return jdbcTypeMap.containsKey(paramName) ? jdbcTypeMap.get(paramName) : JdbcType.OTHER;
	}

}
