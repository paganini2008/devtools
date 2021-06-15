/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

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
package com.github.paganini2008.devtools.objectpool.examples;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.github.paganini2008.devtools.RandomUtils;
import com.github.paganini2008.devtools.Sequence;
import com.github.paganini2008.devtools.collection.Tuple;
import com.github.paganini2008.devtools.jdbc.JdbcUtils;
import com.github.paganini2008.devtools.multithreads.ExecutorUtils;
import com.github.paganini2008.devtools.objectpool.dbpool.GenericDataSource;
import com.github.paganini2008.devtools.objectpool.dbpool.QuerySpan;

/**
 * 
 * TestDataSource
 *
 * @author Fred Feng
 * @version 1.0
 */
public class TestDataSource {

	static {
		System.setProperty("devtools.logging.com.github.paganini2008.devtools.objectpool", "debug");
	}

	public static void main(String[] args) throws Exception {
		GenericDataSource dataSource = new GenericDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setJdbcUrl(
				"jdbc:mysql://localhost:3306/db_mec_hlsh_v2?userUnicode=true&characterEncoding=UTF8&useSSL=false&serverTimezone=UTC&autoReconnect=true&zeroDateTimeBehavior=convertToNull");
		dataSource.setUser("fengy");
		dataSource.setPassword("123456");
		Executor executor = Executors.newFixedThreadPool(10);
		for (final int i : Sequence.forEach(0, 10000)) {
			executor.execute(() -> {
				Connection connection = null;
				Tuple tuple = null;
				try {
					connection = dataSource.getConnection();
					tuple = JdbcUtils.fetchOne(connection, "select * from mec_area where level=? limit 1",
							new Object[] { RandomUtils.randomInt(1, 4) });
					System.out.println(tuple);
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					JdbcUtils.closeQuietly(connection);
				}
			});
		}
		System.in.read();
		// Sql Query Summary
		Map<String, QuerySpan> results = dataSource.getStatisticsResult("dd/MM/yyyy");
		System.out.println(results);
		dataSource.close();
		ExecutorUtils.gracefulShutdown(executor, 60000);
		System.out.println("TestDataSource.main()");
	}

}
