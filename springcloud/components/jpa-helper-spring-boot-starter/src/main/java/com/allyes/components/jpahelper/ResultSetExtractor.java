package com.allyes.components.jpahelper;

import javax.persistence.Query;

/**
 * 
 * ResultSetExtractor
 * 
 * @author Fred Feng
 * @created 2019-02
 */
public interface ResultSetExtractor<T> {

	T extractData(Query query);

}
