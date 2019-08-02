package com.github.paganini2008.devtools.jdbc;

import java.sql.SQLException;

public interface SqlEngine {

	SqlSession beginTransaction();

	void discard() throws SQLException;

	void execute() throws SQLException;

	void close() throws SQLException;

}
