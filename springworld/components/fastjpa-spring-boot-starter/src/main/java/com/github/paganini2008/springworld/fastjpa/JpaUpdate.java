package com.github.paganini2008.springworld.fastjpa;

/**
 * 
 * JpaUpdate
 * 
 * @author Fred Feng
 * @created 2019-02
 */
public interface JpaUpdate<E> extends Executable {

	JpaUpdate<E> filter(Filter filter);

	<T> JpaUpdate<E> set(String attributeName, T value);

	<T> JpaUpdate<E> set(String attributeName, String anotherAttributeName);

	<T> JpaUpdate<E> set(String attributeName, Field<T> value);

	<X> JpaSubQuery<X, X> subQuery(Class<X> entityClass);
}
