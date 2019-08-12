package com.github.paganini2008.springworld.fastjpa;

/**
 * 
 * JpaGroupBy
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-02
 */
public interface JpaGroupBy<E> {

	JpaGroupBy<E> having(Filter expression);

	default JpaResultSet<E> select(String... attributeNames) {
		return select(new ColumnList().addColumns(attributeNames));
	}

	default JpaResultSet<E> select(String alias, String[] attributeNames) {
		return select(new ColumnList().addColumns(alias, attributeNames));
	}

	default JpaResultSet<E> select(Column... columns) {
		return select(new ColumnList(columns));
	}

	default JpaResultSet<E> select(Field<?>... fields) {
		return select(new ColumnList().addColumns(fields));
	}

	JpaResultSet<E> select(ColumnList columnList);

	JpaGroupBy<E> sort(JpaSort... sorts);

}
