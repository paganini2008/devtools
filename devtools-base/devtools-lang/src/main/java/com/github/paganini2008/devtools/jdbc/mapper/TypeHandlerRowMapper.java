package com.github.paganini2008.devtools.jdbc.mapper;

import com.github.paganini2008.devtools.jdbc.TypeHandlerRegistry;

public interface TypeHandlerRowMapper<T> extends RowMapper<T> {

	void setTypeHandlerRegistry();
	
}
