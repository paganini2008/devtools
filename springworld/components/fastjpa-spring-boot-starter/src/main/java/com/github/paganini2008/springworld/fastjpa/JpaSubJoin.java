package com.github.paganini2008.springworld.fastjpa;

/**
 * 
 * JpaSubJoin
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-04
 */
public interface JpaSubJoin<T> {

	default <X> JpaSubQuery<X, T> join(String attributeName, String alias) {
		return join(attributeName, alias, null);
	}

	default <X> JpaSubQuery<X, T> leftJoin(String attributeName, String alias) {
		return leftJoin(attributeName, alias, null);
	}

	default <X> JpaSubQuery<X, T> rightJoin(String attributeName, String alias) {
		return rightJoin(attributeName, alias, null);
	}

	<X> JpaSubQuery<X, T> join(String attributeName, String alias, Filter on);

	<X> JpaSubQuery<X, T> leftJoin(String attributeName, String alias, Filter on);

	<X> JpaSubQuery<X, T> rightJoin(String attributeName, String alias, Filter on);
}
