package com.github.paganini2008.springworld.fastjpa;

/**
 * 
 * JpaCustomUpdate
 * 
 * @author Fred Feng
 * @created 2019-02
 */
public interface JpaCustomUpdate<X> {

	JpaUpdate<X> update(Class<X> entityClass);

	JpaDelete<X> delete(Class<X> entityClass);

	int executeUpdate(JpaDeleteCallback<X> callback);

	int executeUpdate(JpaUpdateCallback<X> callback);
}
