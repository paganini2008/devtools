package com.github.paganini2008.devtools.db4j.mapper;

import java.util.Map;

import com.github.paganini2008.devtools.collection.CamelCaseInsensitiveMap;
import com.github.paganini2008.devtools.db4j.JdbcType;

/**
 * 
 * MapRowMapper
 *
 * @author Fred Feng
 * @version 1.0
 */
public class MapRowMapper extends AbstractRowMapper<Map<String, Object>> {

	@Override
	protected Map<String, Object> createObject(int columnCount) {
		return new CamelCaseInsensitiveMap<Object>();
	}

	@Override
	protected void setValue(Map<String, Object> map, int columnIndex, String columnName, String columnDisplayName, JdbcType jdbcType,
			Object columnValue) {
		map.put(columnDisplayName, columnValue);
	}

}
