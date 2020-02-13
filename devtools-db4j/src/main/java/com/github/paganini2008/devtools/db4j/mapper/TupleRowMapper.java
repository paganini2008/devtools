package com.github.paganini2008.devtools.db4j.mapper;

import com.github.paganini2008.devtools.CaseFormats;
import com.github.paganini2008.devtools.collection.Tuple;
import com.github.paganini2008.devtools.db4j.JdbcType;
import com.github.paganini2008.devtools.db4j.TypeHandlerRegistry;

/**
 * 
 * TupleRowMapper
 *
 * @author Fred Feng
 * @created 2016-02
 * @revised 2020-01
 * @version 1.0
 */
public class TupleRowMapper extends AbstractRowMapper<Tuple> {

	public TupleRowMapper(TypeHandlerRegistry typeHandlerRegistry) {
		super(typeHandlerRegistry);
	}

	protected Tuple createObject(int columnCount) {
		return Tuple.newTuple(CaseFormats.LOWER_CAMEL);
	}

	protected void setValue(Tuple tuple, int columnIndex, String columnName, String columnDisplayName,
			JdbcType jdbcType, Object columnValue) {
		tuple.set(columnName, columnValue);
	}

}
