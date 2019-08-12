package com.github.paganini2008.springworld.fastjpa.support;

import com.github.paganini2008.devtools.collection.Tuple;

/**
 * 
 * RowMapper
 * 
 * @author Fred Feng
 * @created 2019-04
 */
public interface RowMapper<T> {

	T mapRow(Tuple tuple);

}
