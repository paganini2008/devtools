package com.github.paganini2008.springboot.fastjpa;

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
