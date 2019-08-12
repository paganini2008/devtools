package com.github.paganini2008.springworld.fastjpa;

/**
 * 
 * JpaDelete
 * 
 * @author Fred Feng
 * @created 2019-02
 */
public interface JpaDelete<E> extends Executable{

	JpaDelete<E> filter(Filter filter);

	<X> JpaSubQuery<X, X> subQuery(Class<X> entityClass);

}
