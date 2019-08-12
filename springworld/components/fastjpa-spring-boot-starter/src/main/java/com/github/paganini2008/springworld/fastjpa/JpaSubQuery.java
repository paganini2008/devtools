package com.github.paganini2008.springworld.fastjpa;

/**
 * 
 * JpaSubQuery
 * 
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-02
 */
public interface JpaSubQuery<E, T> extends JpaSubJoin<T>, SubQueryBuilder<T> {

	JpaSubQuery<E, T> filter(Filter filter);

	default JpaSubGroupBy<E, T> groupBy(String alias, String... attributeNames) {
		return groupBy(new FieldList().addFields(alias, attributeNames));
	}

	default JpaSubGroupBy<E, T> groupBy(Field<?>... fields) {
		return groupBy(new FieldList(fields));
	}

	JpaSubGroupBy<E, T> groupBy(FieldList fieldList);

	default JpaSubQuery<E, T> select(String attributeName) {
		return select(null, attributeName);
	}

	JpaSubQuery<E, T> select(String alias, String attributeName);

	JpaSubQuery<E, T> select(Field<T> field);

}
