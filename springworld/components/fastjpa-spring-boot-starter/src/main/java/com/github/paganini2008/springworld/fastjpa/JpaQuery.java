package com.github.paganini2008.springworld.fastjpa;

/**
 * 
 * JpaQuery
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-02
 * @version 1.0
 */
public interface JpaQuery<E> extends JpaJoin {

	JpaQuery<E> filter(Filter filter);

	JpaQuery<E> sort(JpaSort... sorts);

	default JpaGroupBy<E> groupBy(String... attributeNames) {
		return groupBy(new FieldList().addFields(attributeNames));
	}

	default JpaGroupBy<E> groupBy(String alias, String[] attributeNames) {
		return groupBy(new FieldList().addFields(alias, attributeNames));
	}

	default JpaGroupBy<E> groupBy(Field<?>... fields) {
		return groupBy(new FieldList(fields));
	}

	JpaGroupBy<E> groupBy(FieldList fieldList);

	default JpaResultSet<E> selectThis() {
		return selectAlias(Model.ROOT);
	}

	JpaResultSet<E> selectAlias(String... tableAlias);

	default JpaResultSet<E> select(String... attributeNames) {
		return select(new ColumnList().addColumns(attributeNames));
	}

	default JpaResultSet<E> select(String alias, String... attributeNames) {
		return select(new ColumnList().addColumns(alias, attributeNames));
	}

	default JpaResultSet<E> select(Column... columns) {
		return select(new ColumnList(columns));
	}

	default JpaResultSet<E> select(Field<?>... fields) {
		return select(new ColumnList().addColumns(fields));
	}

	JpaResultSet<E> select(ColumnList columnList);

	JpaQuery<E> distinct(boolean distinct);

	<X> JpaSubQuery<X, X> subQuery(Class<X> entityClass, String alias);

	<X, T> JpaSubQuery<X, T> subQuery(Class<X> entityClass, String alias, Class<T> resultClass);

}
