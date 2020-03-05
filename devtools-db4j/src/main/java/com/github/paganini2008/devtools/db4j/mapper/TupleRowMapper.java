package com.github.paganini2008.devtools.db4j.mapper;

import com.github.paganini2008.devtools.CaseFormats;
import com.github.paganini2008.devtools.collection.Tuple;
import com.github.paganini2008.devtools.db4j.JdbcType;

/**
 * 
 * TupleRowMapper
 *
 * @author Fred Feng
 * @version 1.0
 */
public class TupleRowMapper extends AbstractRowMapper<Tuple> {

	protected Tuple createObject(int columnCount) {
		return Tuple.newTuple(CaseFormats.LOWER_CAMEL);
	}

	protected void setValue(Tuple tuple, int columnIndex, String columnName, String columnDisplayName,
			JdbcType jdbcType, Object columnValue) {
		tuple.set(columnName, columnValue);
	}

}
