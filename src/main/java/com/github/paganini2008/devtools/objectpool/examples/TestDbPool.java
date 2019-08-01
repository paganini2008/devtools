package com.github.paganini2008.devtools.objectpool.examples;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.github.paganini2008.devtools.RandomUtils;
import com.github.paganini2008.devtools.Sequence;
import com.github.paganini2008.devtools.beans.Tuple;
import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.jdbc.DBUtils;
import com.github.paganini2008.devtools.multithreads.ExecutorUtils;
import com.github.paganini2008.devtools.objectpool.dbpool.GenericDataSource;
import com.github.paganini2008.devtools.objectpool.dbpool.QuerySpan;

public class TestDbPool {

	static {
		System.setProperty("devtools.logging.com.github.paganini2008.devtools.objectpool", "debug");
	}

	public static void main(String[] args) throws Exception {
		GenericDataSource ds = new GenericDataSource();
		ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
		ds.setUrl(
				"jdbc:mysql://10.200.28.42:9998/db_mec_hlsh_v2?userUnicode=true&characterEncoding=UTF8&useSSL=false&serverTimezone=UTC&autoReconnect=true&zeroDateTimeBehavior=convertToNull");
		ds.setUser("fengyan");
		ds.setPassword("Allyes@911");
		Executor executor = Executors.newFixedThreadPool(10);
		for (final int i : Sequence.forEach(0, 10000)) {
			executor.execute(() -> {
				Connection connection = null;
				try {
					connection = ds.getConnection();
					Iterator<Tuple> iterator = DBUtils.executeQuery(connection, "select * from mec_area where level=?",
							new Object[] { RandomUtils.randomInt(1, 4) });
					System.out.println(CollectionUtils.getFirst(iterator));
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					DBUtils.closeQuietly(connection);
				}
			});
		}
		System.in.read();
		Map<String, QuerySpan> results = ds.getStatisticsResult("30/07/2019");
		System.out.println(results);
		ds.close();
		ExecutorUtils.gracefulShutdown(executor, 60000);
		System.out.println("TestDbPool.main()");
	}

}
