package com.github.paganini2008.devtools.jdbc.mapper;

import java.util.LinkedHashMap;
import java.util.Map;

import com.github.paganini2008.devtools.jdbc.JdbcType;
import com.github.paganini2008.devtools.jdbc.TypeHandlerRegistry;

/**
 * MapRowMapper
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class MapRowMapper extends BasicRowMapper<Map<String, Object>> {

	public MapRowMapper(TypeHandlerRegistry typeHandlerRegistry) {
		super(typeHandlerRegistry);
	}

	protected Map<String, Object> createObject(int columnCount) {
		return new LinkedHashMap<String, Object>(columnCount);
	}

	protected void setValue(Map<String, Object> object, int columnIndex, String columnName, String columnDisplayName,
			JdbcType jdbcType, Object columnValue) {
		object.put(columnName, columnValue);
	}

}
