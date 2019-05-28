package com.github.paganini2008.devtools.jdbc;

import java.sql.SQLException;

import javax.sql.DataSource;

public class JdbcSqlEngine implements SqlEngine {

	private final TransactionFactory transactionFactory;
	private final ThreadLocal<Transaction> transactionPool;
	private SqlRunner sqlRunner = new SqlRunner();

	public JdbcSqlEngine(DataSource dataSource) {
		transactionFactory = new JdbcTransactionFactory(dataSource);
		transactionPool = new ThreadLocal<Transaction>() {
			protected Transaction initialValue() {
				return transactionFactory.createTransaction();
			}
		};
	}

	public void setSqlRunner(SqlRunner sqlRunner) {
		this.sqlRunner = sqlRunner;
	}

	public SqlSession beginTransaction() {
		return new JdbcSqlSession(transactionPool.get(), sqlRunner);
	}

	public void discard() throws SQLException {
		transactionPool.get().rollback();
	}

	public void execute() throws SQLException {
		transactionPool.get().commit();
	}

	public void close() throws SQLException {
		transactionPool.get().close();
	}

}
