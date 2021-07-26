/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
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
 * @since 2.0.1
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
		Cursor<Tuple> cursor = sqlRunner.queryForCursor(connection, "select * from crawler_resources limit 1000");
		while (cursor.hasNext()) {
			Tuple tuple = cursor.next();
			System.out.println(cursor.getRownum() + ": " + tuple.get("title") + "\t" + tuple.get("url"));
		}
		cursor.close();
		JdbcUtils.closeQuietly(connection);
	}

	public static void test3() throws SQLException {
		ParsedSqlRunner sqlRunner = new ParsedSqlRunner();
		Connection connection = getConnection();
		Cursor<Tuple> cursor = sqlRunner.queryForCachedCursor(connection, "select * from crawler_resources limit 100");
		while (cursor.hasNext()) {
			Tuple tuple = cursor.next();
			System.out.println(tuple.get("title") + "\t" + tuple.get("url"));
		}
		cursor.close();
		JdbcUtils.closeQuietly(connection);
	}

	public static void main(String[] args) throws SQLException {
		test2();
	}

}
