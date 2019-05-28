package com.github.paganini2008.devtools.jdbc.utils;

import com.github.paganini2008.devtools.jdbc.JdbcType;
import com.github.paganini2008.devtools.jdbc.TypeHandlerRegistry;
import com.github.paganini2008.devtools.jdbc.mapper.BasicRowMapper;

public class EntryRowMapper extends BasicRowMapper<Entry> {

	public EntryRowMapper(TypeHandlerRegistry typeHandlerRegistry) {
		super(typeHandlerRegistry);
	}

	protected Entry createObject(int columnCount) {
		return new Entry();
	}

	protected void setValue(Entry e, int columnIndex, String columnName, String columnDisplayName, JdbcType jdbcType,
			Object columnValue) {
		e.setColumnName(columnName);
		e.setDisplayName(columnDisplayName);
		e.setJdbcType(jdbcType);
		e.setValue(columnValue);
	}
	
	

}
