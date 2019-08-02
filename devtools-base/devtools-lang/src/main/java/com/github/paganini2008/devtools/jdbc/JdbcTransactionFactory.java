package com.github.paganini2008.devtools.jdbc;

import javax.sql.DataSource;

public class JdbcTransactionFactory implements TransactionFactory {

	private final DataSource dataSource;

	public JdbcTransactionFactory(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Transaction createTransaction() {
		return new JdbcTransaction(dataSource);
	}

}
