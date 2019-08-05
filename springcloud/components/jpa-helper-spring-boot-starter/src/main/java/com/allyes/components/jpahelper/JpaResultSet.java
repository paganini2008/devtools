package com.allyes.components.jpahelper;

import com.allyes.components.jpahelper.support.ResultSetSlice;

/**
 * 
 * JpaResultSet
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-02
 */
public interface JpaResultSet<E> extends ResultSetSlice<E> {

	<T> T getResult(Class<T> requiredType);

	<T> ResultSetSlice<T> setTransformer(Transformer<E, T> transformer);

}
