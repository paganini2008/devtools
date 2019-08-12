package com.github.paganini2008.springworld.fastjpa;

/**
 * 
 * JpaJoin
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-02
 */
public interface JpaJoin {

	default <X> JpaQuery<X> join(String attributeName, String alias) {
		return join(attributeName, alias, null);
	}

	default <X> JpaQuery<X> leftJoin(String attributeName, String alias) {
		return leftJoin(attributeName, alias, null);
	}

	default <X> JpaQuery<X> rightJoin(String attributeName, String alias) {
		return rightJoin(attributeName, alias, null);
	}

	<X> JpaQuery<X> join(String attributeName, String alias, Filter on);

	<X> JpaQuery<X> leftJoin(String attributeName, String alias, Filter on);

	<X> JpaQuery<X> rightJoin(String attributeName, String alias, Filter on);

}
