package com.allyes.components.jpahelper.support;

import com.allyes.developer.utils.collection.Tuple;

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
