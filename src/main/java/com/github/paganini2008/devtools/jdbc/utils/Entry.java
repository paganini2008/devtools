package com.github.paganini2008.devtools.jdbc.utils;

import com.github.paganini2008.devtools.jdbc.JdbcType;

/**
 * Entry
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class Entry {

	private String columnName;
	private String displayName;
	private Object value;
	private JdbcType jdbcType;

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public JdbcType getJdbcType() {
		return jdbcType;
	}

	public void setJdbcType(JdbcType jdbcType) {
		this.jdbcType = jdbcType;
	}

}
