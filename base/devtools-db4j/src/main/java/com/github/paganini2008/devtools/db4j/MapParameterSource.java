package com.github.paganini2008.devtools.db4j;

import java.util.Map;

/**
 * MapParameterSource
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class MapParameterSource extends ParameterSourceSupport implements ParameterSource {

	public MapParameterSource(Map<String, ?> parameterMap) {
		this(parameterMap, null);
	}

	public MapParameterSource(Map<String, ?> parameterMap, Map<String, JdbcType> jdbcTypeMap) {
		this.parameterMap = parameterMap;
		if (jdbcTypeMap != null) {
			for (Map.Entry<String, JdbcType> e : jdbcTypeMap.entrySet()) {
				addJdbcType(e.getKey(), e.getValue());
			}
		}
	}

	private final Map<String, ?> parameterMap;

	public boolean hasValue(String paramName) {
		return parameterMap.containsKey(paramName);
	}

	public Object getValue(String paramName) {
		return parameterMap.get(paramName);
	}

}