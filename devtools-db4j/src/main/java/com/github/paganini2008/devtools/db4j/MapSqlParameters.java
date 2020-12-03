package com.github.paganini2008.devtools.db4j;

import java.util.List;
import java.util.Map;

/**
 * MapSqlParameters
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class MapSqlParameters extends AbstractSqlParameter implements SqlParameters {

	private final List<Map<String, Object>> parameterList;

	public MapSqlParameters(List<Map<String, Object>> parameterList) {
		this(parameterList, null);
	}

	public MapSqlParameters(List<Map<String, Object>> parameterList, Map<String, JdbcType> jdbcTypeMap) {
		this.parameterList = parameterList;
		if (jdbcTypeMap != null) {
			for (Map.Entry<String, JdbcType> e : jdbcTypeMap.entrySet()) {
				addJdbcType(e.getKey(), e.getValue());
			}
		}
	}

	public boolean hasValue(int index, String paramName) {
		return parameterList.get(index).containsKey(paramName);
	}

	public Object getValue(int index, String paramName) {
		return parameterList.get(index).get(paramName);
	}

	public int getSize() {
		return parameterList.size();
	}

}
