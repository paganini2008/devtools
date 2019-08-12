package com.github.paganini2008.springworld.fastjpa;

/**
 * 
 * JpaSubGroupBy
 * 
 * @author Fred Feng
 * @created 2019-07
 * @created 2019-02
 */
public interface JpaSubGroupBy<E, T> {

	JpaSubGroupBy<E, T> having(Filter filter);

	default JpaSubGroupBy<E, T> select(String attributeName){
		return select(null, attributeName);
	}

	JpaSubGroupBy<E, T> select(String alias, String attributeName);

	JpaSubGroupBy<E, T> select(Field<T> field);

}
