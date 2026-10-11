/**
* Copyright 2017-2026 Fred Feng (paganini.fy@gmail.com)

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
package com.github.paganini2008.devtools.jdbc;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import com.github.paganini2008.devtools.collection.Tuple;

/**
 * JdbcDumpTemplateTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
@Timeout(30)
public class JdbcDumpTemplateTest {

	private static final String DDL_USER = "create table t_user(id int primary key, user_name varchar(20), age int)";
	private static final String DDL_LOG = "create table t_log(id int primary key, msg varchar(50))";
	private static final String DDL_EMPTY = "create table t_empty(id int primary key)";

	private JdbcTracker source;
	private JdbcTracker destination;
	private ExecutorService executor;

	@BeforeEach
	public void setUp() throws SQLException {
		source = new JdbcTracker("jdbc_dump_source");
		destination = new JdbcTracker("jdbc_dump_destination");
		source.execute(DDL_USER, DDL_LOG, DDL_EMPTY);
		destination.execute(DDL_USER, DDL_LOG, DDL_EMPTY);
		List<Object[]> users = new ArrayList<Object[]>();
		for (int i = 1; i <= 12; i++) {
			users.add(new Object[] { i, "user" + i, i * 10 });
		}
		List<Object[]> logs = new ArrayList<Object[]>();
		for (int i = 1; i <= 5; i++) {
			logs.add(new Object[] { i, "log" + i });
		}
		try (Connection c = source.rawConnection()) {
			JdbcUtils.batchUpdate(c, "insert into t_user values (?, ?, ?)", users);
			JdbcUtils.batchUpdate(c, "insert into t_log values (?, ?)", logs);
		}
		executor = Executors.newFixedThreadPool(4);
	}

	@AfterEach
	public void tearDown() throws SQLException {
		executor.shutdownNow();
		source.drop();
		destination.drop();
	}

	private long count(String table) throws SQLException {
		try (Connection c = destination.rawConnection()) {
			return JdbcUtils.rowCount(c, "select * from " + table);
		}
	}

	private JdbcDumpTemplate template() {
		return new JdbcDumpTemplate(source.connectionFactory(), destination.connectionFactory());
	}

	static class Options implements JdbcDumpOptions {

		String insertionSql;
		Executor executor;
		long maxRecords = -1;
		Predicate<Tuple> predicate = t -> true;

		@Override
		public String getInsertionSql(Tuple t) {
			return insertionSql;
		}

		@Override
		public String getSchema() {
			return "PUBLIC";
		}

		@Override
		public Executor getExecutor() {
			return executor;
		}

		@Override
		public long getMaxRecords() {
			return maxRecords;
		}

		@Override
		public Predicate<Tuple> getPredicate() {
			return predicate;
		}
	}

	static class RecordingProgress implements DumpProgress {

		final AtomicLong started = new AtomicLong();
		final AtomicLong ended = new AtomicLong();
		final AtomicLong lastProgress = new AtomicLong();
		volatile long total;

		@Override
		public void onStart(String catalog, String schema, String sql, Object[] args, JdbcDumpOptions dumpOptions) {
			started.incrementAndGet();
		}

		@Override
		public void progress(String catalog, String schema, String sql, Object[] args, long progressRecords, long totalRecords,
				JdbcDumpOptions dumpOptions) {
			lastProgress.accumulateAndGet(progressRecords, Math::max);
			total = totalRecords;
		}

		@Override
		public void onEnd(String catalog, String schema, String sql, Object[] args, JdbcDumpOptions dumpOptions) {
			ended.incrementAndGet();
		}
	}

	@Test
	public void dumpSql_rowByRow_withProgressAndPredicate() throws SQLException {
		Options options = new Options();
		options.insertionSql = "insert into t_user(id, user_name, age) values (?, ?, ?)";
		options.predicate = t -> ((Number) t.get("ID")).intValue() % 2 == 0;
		RecordingProgress progress = new RecordingProgress();
		long rows = template().dump(null, "PUBLIC", "select * from t_user where id > ?", new Object[] { 0 }, progress, options);
		assertEquals(6L, rows);
		assertEquals(6L, count("t_user"));
		assertEquals(1, progress.started.get());
		assertEquals(1, progress.ended.get());
		assertEquals(6L, progress.lastProgress.get());
		assertEquals(12L, progress.total);
		assertEquals(0, source.openConnections());
		assertEquals(0, destination.openConnections());
	}

	@Test
	public void dumpSql_withExecutor_waitsForAllWrites() throws SQLException {
		Options options = new Options();
		options.insertionSql = "insert into t_user(id, user_name, age) values (?, ?, ?)";
		options.executor = executor;
		long rows = template().dump(null, null, "select * from t_user", null, null, options);
		assertEquals(12L, rows, "row count must be complete when dump returns");
		assertEquals(12L, count("t_user"));
		assertEquals(0, destination.openConnections());
	}

	@Test
	public void dumpSql_maxRecords() throws SQLException {
		Options options = new Options();
		options.insertionSql = "insert into t_user(id, user_name, age) values (?, ?, ?)";
		options.maxRecords = 4;
		assertEquals(4L, template().dump(null, null, "select * from t_user order by id", null, null, options));
		assertEquals(4L, count("t_user"));
	}

	@Test
	public void dumpSql_writeFailureIsPropagated() throws SQLException {
		Options options = new Options();
		options.insertionSql = "insert into t_none(id) values (?)";
		assertThrows(JdbcDumpException.class, () -> template().dump(null, null, "select id from t_user", null, null, options));
		options.executor = executor;
		assertThrows(JdbcDumpException.class, () -> template().dump(null, null, "select id from t_user", null, null, options));
		assertEquals(0, destination.openConnections());
	}

	@Test
	public void dumpTable_buildsInsertionSqlFromColumns() throws SQLException {
		Options options = new Options();
		assertEquals(5L, template().dump(null, null, "t_log", null, options, null));
		assertEquals(5L, count("t_log"));
		try (Connection c = destination.rawConnection()) {
			assertEquals("log3", JdbcUtils.fetchOne(c, "select msg from t_log where id = 3", String.class));
		}
	}

	@Test
	public void dumpTable_errorHandlerReceivesFailures() throws SQLException {
		destination.execute("drop table t_log");
		List<String> failedSql = Collections.synchronizedList(new ArrayList<String>());
		long rows = template().dump(null, null, "t_log", null, new Options(), (catalog, schema, sql, args, e) -> failedSql.add(sql));
		assertEquals(0L, rows);
		assertEquals(Collections.singletonList("select * from t_log"), failedSql);
		assertThrows(JdbcDumpException.class, () -> template().dump(null, null, "t_log", null, new Options(), null));
		assertThrows(SQLException.class, () -> template().dump(null, null, "t_missing", null, new Options(), null));
	}

	@Test
	public void dumpAllTables_rowByRow_excludingTables() throws SQLException {
		long[] rows = template().dump(null, "PUBLIC", new String[] { "T_LOG" }, null, new Options(), null);
		assertArrayEquals(new long[] { 0L, 12L }, rows);
		assertEquals(12L, count("t_user"));
		assertEquals(0L, count("t_log"));
		assertEquals(0, source.openConnections());
		assertEquals(0, source.openResources());
	}

	@Test
	public void dumpAllTables_inBatches() throws SQLException {
		Options options = new Options();
		options.executor = executor;
		RecordingProgress progress = new RecordingProgress();
		long[] rows = new JdbcDumpTemplate(new UnpooledDataSource("org.h2.Driver", source.getUrl(), "sa", "", 4),
				new UnpooledDataSource("org.h2.Driver", destination.getUrl(), "sa", "", 4)).dump(null, "PUBLIC", (String[]) null, 5, progress, options,
						null);
		assertArrayEquals(new long[] { 0L, 5L, 12L }, rows);
		assertEquals(12L, count("t_user"));
		assertEquals(5L, count("t_log"));
		assertEquals(3, progress.started.get());
		assertEquals(3, progress.ended.get());
	}

	@Test
	public void dumpTable_inBatches_withPredicateAndMaxRecords() throws SQLException {
		Options options = new Options();
		options.predicate = t -> ((Number) t.get("AGE")).intValue() > 30;
		options.maxRecords = 10;
		long rows = template().dump(null, null, "t_user", 5, null, options, null);
		assertEquals(7L, rows);
		assertEquals(7L, count("t_user"));
	}

	@Test
	public void dumpSql_inBatches_errorHandling() throws SQLException {
		destination.execute("drop table t_user");
		List<String> failedSql = new ArrayList<String>();
		assertEquals(0L, template().dump(null, null, "t_user", 5, null, new Options(), (catalog, schema, sql, args, e) -> failedSql.add(sql)));
		assertEquals(1, failedSql.size());
		Options options = new Options();
		options.executor = executor;
		assertThrows(JdbcDumpException.class, () -> template().dump(null, null, "t_user", 5, null, options, null));
		Options badArgs = new Options() {
			@Override
			public Object[] getArgs(Tuple t) {
				return new Object[] { new Object() };
			}
		};
		destination.execute(DDL_USER);
		assertThrows(JdbcDumpException.class, () -> template().dump(null, null, "t_user", 5, null, badArgs, null));
		assertEquals(0, destination.openConnections());
	}

	@Test
	public void dumpOptions_defaults() {
		JdbcDumpOptions options = t -> "sql";
		assertEquals(null, options.getCatalog());
		assertEquals(null, options.getSchema());
		assertEquals(null, options.getExecutor());
		assertEquals(-1L, options.getMaxRecords());
		assertEquals(0, options.getArgs(null).length);
		Tuple tuple = Tuple.newTuple();
		tuple.set("a", 1);
		assertArrayEquals(new Object[] { 1 }, options.getArgs(tuple));
		assertTrue(options.getPredicate().test(tuple));
	}
}
