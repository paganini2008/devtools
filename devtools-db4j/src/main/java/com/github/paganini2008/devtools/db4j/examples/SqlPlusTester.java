package com.github.paganini2008.devtools.db4j.examples;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.github.paganini2008.devtools.db4j.GeneratedKey;
import com.github.paganini2008.devtools.db4j.SqlPlus;
import com.github.paganini2008.devtools.db4j.Transaction;

/**
 * 
 * SqlPlusTester
 *
 * @author Fred Feng
 * @version 1.0
 */
public class SqlPlusTester {

	public static void main(String[] args) throws SQLException {
		String driverClassName = "com.mysql.cj.jdbc.Driver";
		String jdbcUrl = "jdbc:mysql://localhost:3306/test?userUnicode=true&characterEncoding=UTF8&useSSL=false&serverTimezone=UTC&autoReconnect=true&zeroDateTimeBehavior=convertToNull";
		String user = "fengy";
		String password = "123456";
		SqlPlus sqlPlus = new SqlPlus(driverClassName, jdbcUrl, user, password);
		Transaction transaction = sqlPlus.beginTransaction();
		GeneratedKey generatedKey = GeneratedKey.forNames("id");
		int effectedRows = transaction
				.update("insert into tb_point (username,points,tag,last_modified) values ('tester-1',100,5,'2020-02-26')", args);
		System.out.println("effectedRows: " + effectedRows);
		System.out.println("Added id: " + generatedKey.getKey());
		transaction.commit();
	}
}
