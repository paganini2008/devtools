package com.github.paganini2008.devtools.db4j.examples;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import com.github.paganini2008.devtools.RandomUtils;
import com.github.paganini2008.devtools.collection.Tuple;
import com.github.paganini2008.devtools.db4j.ParsedSqlRunner;
import com.github.paganini2008.devtools.jdbc.Cursor;
import com.github.paganini2008.devtools.jdbc.JdbcUtils;

/**
 * 
 * SqlRunnerTester
 *
 * @author Fred Feng
 * @version 1.0
 */
public class SqlRunnerTester {

	private static Connection getConnection() throws SQLException {
		String jdbcUrl = "jdbc:mysql://localhost:3306/test?userUnicode=true&characterEncoding=UTF8&useSSL=false&serverTimezone=UTC&autoReconnect=true&zeroDateTimeBehavior=convertToNull";
		String user = "fengy";
		String password = "123456";
		return DriverManager.getConnection(jdbcUrl, user, password);
	}

	public static void test1() throws SQLException {
		ParsedSqlRunner sqlRunner = new ParsedSqlRunner();
		Connection connection = getConnection();
		List<Tuple> list = sqlRunner.queryForList(connection, "select * from ccms_param limit 100",
				new Object[] { RandomUtils.randomInt(1, 4) });
		for (Tuple tuple : list) {
			System.out.println(tuple);
		}
		System.out.println("Total rows: " + list.size());
		JdbcUtils.closeQuietly(connection);
	}
	
	public static void test2() throws SQLException {
		ParsedSqlRunner sqlRunner = new ParsedSqlRunner();
		Connection connection = getConnection();
		Cursor<Tuple> cursor = sqlRunner.queryForCursor(connection, "select * from ccms_param limit 100",(Object[])null);
		while(cursor.hasNext()) {
			System.out.println(cursor.next());
		}
		cursor.close();
		JdbcUtils.closeQuietly(connection);
	}

	public static void main(String[] args) throws SQLException {
		test2();
	}

}
