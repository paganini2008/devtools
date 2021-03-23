package com.github.paganini2008.devtools.beans.streaming.examples;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.collection.Tuple;
import com.github.paganini2008.devtools.jdbc.JdbcUtils;

public class TestMain {

	static {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static Connection getMySqlConnection() throws SQLException {
		String url = "jdbc:mysql://10.25.19.220:3306/nexus_dev?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false";
		String user = "root";
		String password = "Apex@2020";
		return DriverManager.getConnection(url, user, password);
	}

	private static Connection getPGConnection() throws SQLException {
		String url = "jdbc:postgresql://10.25.19.161:5432/nexus_dev?characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true&useUnicode=true&useSSL=false&serverTimezone=UTC";
		String user = "postgres";
		String password = "apex2020";
		return DriverManager.getConnection(url, user, password);
	}

	public static void main(String[] args) throws Exception {
		String selectSql = "select * from sys_message";
		Connection pgConn = getPGConnection();
		List<Tuple> list = JdbcUtils.fetchAll(pgConn, selectSql);
		for (Tuple tuple : list) {
			saveToMySql(tuple);
		}
		pgConn.close();
	}

	private static void saveToMySql(Tuple tuple) throws SQLException {
		String insertSql = "insert into sys_message (project,lang,code,message,module,message_type,updater,last_modified) values (?,?,?,?,?,?,?,?)";
		Connection connection = getMySqlConnection();
		Object[] args = new Object[] { tuple.getProperty("project", String.class), tuple.getProperty("lang", String.class),
				tuple.getProperty("code", String.class), tuple.getProperty("message", String.class),
				tuple.getProperty("module", String.class), tuple.getProperty("message_type", String.class),
				tuple.getProperty("updater", String.class), tuple.getProperty("last_modified", Date.class) };
		JdbcUtils.insert(connection, insertSql, args);
		System.out.println("Save record: " + ArrayUtils.join(args));
		connection.close();
	}

}
