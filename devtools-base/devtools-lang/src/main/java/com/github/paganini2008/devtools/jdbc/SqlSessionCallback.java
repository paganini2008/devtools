package com.github.paganini2008.devtools.jdbc;

public interface SqlSessionCallback<T> {

	T execute(Transaction transaction, SqlRunner sqlRunner);

}
