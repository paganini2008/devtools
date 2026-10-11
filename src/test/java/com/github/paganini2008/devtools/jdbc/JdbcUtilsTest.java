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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import com.github.paganini2008.devtools.CaseFormats;
import com.github.paganini2008.devtools.collection.Tuple;

/**
 * JdbcUtilsTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
@Timeout(20)
public class JdbcUtilsTest {

	private JdbcTracker tracker;

	@BeforeEach
	public void setUp() throws SQLException {
		tracker = new JdbcTracker("jdbc_utils_test");
		tracker.execute("create table t_user(id bigint auto_increment primary key, user_name varchar(50), age int, "
				+ "salary decimal(10,2), birthday date, created timestamp, note clob, data blob, active boolean)");
	}

	@AfterEach
	public void tearDown() throws SQLException {
		tracker.drop();
	}

	private void insertUsers(int n) throws SQLException {
		List<Object[]> argsList = new ArrayList<Object[]>();
		for (int i = 1; i <= n; i++) {
			argsList.add(new Object[] { "user" + i, i });
		}
		try (Connection c = tracker.rawConnection()) {
			JdbcUtils.batchUpdate(c, "insert into t_user(user_name, age) values (?, ?)", argsList);
		}
	}

	private void assertNoOpenResources() throws SQLException {
		assertEquals(0, tracker.openStatements(), "unclosed statements");
		assertEquals(0, tracker.openResultSets(), "unclosed result sets");
	}

	@Test
	public void insert_returnsGeneratedKeyAndClosesResources() throws SQLException {
		try (Connection c = tracker.connection()) {
			assertEquals(1, JdbcUtils.insert(c, "insert into t_user(user_name, age) values (?, ?)", new Object[] { "a", 1 }));
			assertEquals(2, JdbcUtils.insert(c, "insert into t_user(user_name, age) values (?, ?)", new Object[] { "b", 2 }));
			assertEquals(0, JdbcUtils.insert(c, "insert into t_user(user_name) select user_name from t_user where 1 = 0",
					(PreparedStatementCallback) null));
		}
		assertNoOpenResources();
	}

	@Test
	public void insert_withoutNumericKey_returnsZero() throws SQLException {
		tracker.execute("create table t_code(code varchar(10) primary key)");
		try (Connection c = tracker.connection()) {
			assertEquals(0, JdbcUtils.insert(c, "insert into t_code(code) values (?)", new Object[] { "x" }));
		}
		assertNoOpenResources();
	}

	@Test
	public void crud_updateAndDeleteAndFetch() throws SQLException {
		insertUsers(3);
		try (Connection c = tracker.connection()) {
			assertEquals(1, JdbcUtils.update(c, "update t_user set age = ? where user_name = ?", new Object[] { 30, "user1" }));
			assertEquals(30, JdbcUtils.fetchOne(c, "select age from t_user where user_name = ?", new Object[] { "user1" }, Integer.class));
			assertEquals(2, JdbcUtils.update(c, "delete from t_user where age < 10"));
			assertEquals(1L, JdbcUtils.rowCount(c, "select * from t_user"));
			assertEquals(0, JdbcUtils.update(c, "update t_user set age = 1 where 1 = 0", (PreparedStatementCallback) null));
		}
		assertNoOpenResources();
	}

	@Test
	public void batchUpdate_skipsNullArgsAndReturnsCounts() throws SQLException {
		try (Connection c = tracker.connection()) {
			List<Object[]> argsList = new ArrayList<Object[]>();
			argsList.add(new Object[] { "a", 1 });
			argsList.add(null);
			argsList.add(new Object[0]);
			argsList.add(new Object[] { "b", 2 });
			int[] result = JdbcUtils.batchUpdate(c, "insert into t_user(user_name, age) values (?, ?)", argsList);
			assertArrayEquals(new int[] { 1, 1 }, result);
			assertArrayEquals(new int[0], JdbcUtils.batchUpdate(c, "insert into t_user(user_name) values ('x')",
					(PreparedStatementCallback) null));
			assertEquals(2L, JdbcUtils.rowCount(c, "select * from t_user"));
		}
		assertNoOpenResources();
	}

	@Test
	public void fetchOne_variants() throws SQLException {
		insertUsers(2);
		try (Connection c = tracker.connection()) {
			Tuple tuple = JdbcUtils.fetchOne(c, "select * from t_user order by id");
			assertEquals("user1", tuple.get("userName"));
			assertEquals("user1", tuple.getProperty("userName"));
			assertNull(JdbcUtils.fetchOne(c, "select * from t_user where 1 = 0"));
			assertEquals("user2", JdbcUtils.fetchOne(c, "select user_name from t_user where age = 2", String.class));
			assertNull(JdbcUtils.fetchOne(c, "select user_name from t_user where 1 = 0", String.class));
			assertNull(JdbcUtils.fetchOne(c, "select user_name from t_user where age = ?", new Object[] { 99 }, String.class));
			Tuple byArgs = JdbcUtils.fetchOne(c, "select * from t_user where age = ?", new Object[] { 2 });
			assertEquals("user2", byArgs.get("userName"));
			assertNull(JdbcUtils.fetchOne(c, "select * from t_user where age = ?", new Object[] { 99 }));
			assertEquals(Long.valueOf(2), JdbcUtils.fetchOne(c, "select count(*) from t_user", (PreparedStatementCallback) null, Long.class));
		}
		assertNoOpenResources();
	}

	@Test
	public void fetchAll_variants() throws SQLException {
		insertUsers(3);
		try (Connection c = tracker.connection()) {
			List<Tuple> all = JdbcUtils.fetchAll(c, "select * from t_user order by id");
			assertEquals(3, all.size());
			assertEquals("user3", all.get(2).get("userName"));
			assertTrue(JdbcUtils.fetchAll(c, "select * from t_user where 1 = 0").isEmpty());
			List<Tuple> some = JdbcUtils.fetchAll(c, "select * from t_user where age > ? order by id", new Object[] { 1 });
			assertEquals(2, some.size());
			assertEquals(3, JdbcUtils.fetchAll(c, "select * from t_user", (Object[]) null).size());
			assertEquals(3, JdbcUtils.fetchAll(c, "select * from t_user", new Object[0]).size());
		}
		assertNoOpenResources();
	}

	@Test
	public void invalidSql_throwsAndClosesStatements() throws SQLException {
		try (Connection c = tracker.connection()) {
			assertThrows(SQLException.class, () -> JdbcUtils.update(c, "update no_table set x = 1"));
			assertThrows(SQLException.class, () -> JdbcUtils.update(c, "update no_table set x = ?", new Object[] { 1 }));
			assertThrows(SQLException.class, () -> JdbcUtils.fetchOne(c, "select x from t_user"));
			assertThrows(SQLException.class, () -> JdbcUtils.fetchOne(c, "select * from t_user where id = ?", new Object[] { "abc" }));
			assertThrows(SQLException.class, () -> JdbcUtils.fetchAll(c, "select bad syntax"));
			assertThrows(SQLException.class, () -> JdbcUtils.fetchAll(c, "select * from t_user where x = ?", new Object[] { 1 }));
			assertThrows(SQLException.class, () -> JdbcUtils.insert(c, "insert into t_user(x) values (?)", new Object[] { 1 }));
			assertThrows(SQLException.class, () -> JdbcUtils.batchUpdate(c, "insert into t_user(age) values (?)",
					Arrays.asList(new Object[] { "not number" }, new Object[] { 1 })));
			assertThrows(SQLException.class, () -> JdbcUtils.cursor(c, "select * from no_table"));
			assertThrows(SQLException.class, () -> JdbcUtils.cursor(c, "select * from no_table where id = ?", new Object[] { 1 }));
			assertFalse(c.isClosed(), "failed cursor must not close the caller's connection");
		}
		assertNoOpenResources();
	}

	@Test
	public void dataTypes_mapNullDateDecimalClobBlob() throws SQLException {
		Date birthday = Date.valueOf("2000-01-31");
		Timestamp created = Timestamp.valueOf("2026-10-11 08:30:15.5");
		try (Connection c = tracker.connection()) {
			JdbcUtils.update(c, "insert into t_user(user_name, age, salary, birthday, created, note, data, active) values (?,?,?,?,?,?,?,?)",
					ps -> {
						ps.setString(1, "typed");
						ps.setObject(2, null);
						ps.setBigDecimal(3, new BigDecimal("12345.67"));
						ps.setDate(4, birthday);
						ps.setTimestamp(5, created);
						ps.setString(6, "长文本 clob");
						ps.setBytes(7, "blob".getBytes(StandardCharsets.UTF_8));
						ps.setBoolean(8, true);
					});
			Tuple tuple = JdbcUtils.fetchOne(c, "select * from t_user where user_name = ?", new Object[] { "typed" });
			assertNull(tuple.get("AGE"));
			assertEquals(new BigDecimal("12345.67"), tuple.get("SALARY"));
			assertEquals(birthday, tuple.get("BIRTHDAY"));
			assertEquals(created, tuple.get("CREATED"));
			assertEquals(Boolean.TRUE, tuple.get("ACTIVE"));
			Object note = tuple.get("NOTE");
			if (note instanceof Clob) {
				Clob clob = (Clob) note;
				assertEquals("长文本 clob", clob.getSubString(1, (int) clob.length()));
			} else {
				assertEquals("长文本 clob", note);
			}
			Object data = tuple.get("DATA");
			byte[] bytes = data instanceof Blob ? ((Blob) data).getBytes(1, (int) ((Blob) data).length()) : (byte[]) data;
			assertEquals("blob", new String(bytes, StandardCharsets.UTF_8));
			assertEquals(new BigDecimal("12345.67"), JdbcUtils.fetchOne(c, "select salary from t_user", BigDecimal.class));
		}
		assertNoOpenResources();
	}

	@Test
	public void transaction_commitAndRollback() throws SQLException {
		try (Connection c = tracker.rawConnection()) {
			JdbcUtils.disableCommit(c);
			assertFalse(c.getAutoCommit());
			JdbcUtils.update(c, "insert into t_user(user_name) values ('a')");
			JdbcUtils.rollback(c);
			assertEquals(0L, JdbcUtils.rowCount(c, "select * from t_user"));
			JdbcUtils.update(c, "insert into t_user(user_name) values ('b')");
			JdbcUtils.commit(c);
			JdbcUtils.update(c, "insert into t_user(user_name) values ('c')");
			JdbcUtils.rollbackQuietly(c);
			JdbcUtils.update(c, "insert into t_user(user_name) values ('d')");
			JdbcUtils.commitQuietly(c);
		}
		try (Connection c = tracker.rawConnection()) {
			assertEquals(2L, JdbcUtils.rowCount(c, "select * from t_user"));
		}
		Connection c1 = tracker.rawConnection();
		JdbcUtils.disableCommitQuietly(c1);
		JdbcUtils.update(c1, "insert into t_user(user_name) values ('e')");
		JdbcUtils.commitAndClose(c1);
		assertTrue(c1.isClosed());
		Connection c2 = tracker.rawConnection();
		c2.setAutoCommit(false);
		JdbcUtils.update(c2, "insert into t_user(user_name) values ('f')");
		JdbcUtils.rollbackAndClose(c2);
		assertTrue(c2.isClosed());
		Connection c3 = tracker.rawConnection();
		c3.setAutoCommit(false);
		JdbcUtils.update(c3, "insert into t_user(user_name) values ('g')");
		JdbcUtils.commitAndCloseQuietly(c3);
		Connection c4 = tracker.rawConnection();
		c4.setAutoCommit(false);
		JdbcUtils.update(c4, "insert into t_user(user_name) values ('h')");
		JdbcUtils.rollbackAndCloseQuietly(c4);
		assertTrue(c3.isClosed() && c4.isClosed());
		try (Connection c = tracker.rawConnection()) {
			assertEquals(4L, JdbcUtils.rowCount(c, "select * from t_user"));
		}
	}

	@Test
	public void closeAndTransactionHelpers_areNullSafe() throws SQLException {
		JdbcUtils.close((Connection) null);
		JdbcUtils.close((java.sql.Statement) null);
		JdbcUtils.close((java.sql.ResultSet) null);
		JdbcUtils.closeQuietly((Connection) null);
		JdbcUtils.closeQuietly((java.sql.Statement) null);
		JdbcUtils.closeQuietly((java.sql.ResultSet) null);
		JdbcUtils.commit(null);
		JdbcUtils.commitQuietly(null);
		JdbcUtils.commitAndClose(null);
		JdbcUtils.commitAndCloseQuietly(null);
		JdbcUtils.rollback(null);
		JdbcUtils.rollbackQuietly(null);
		JdbcUtils.rollbackAndClose(null);
		JdbcUtils.rollbackAndCloseQuietly(null);
		JdbcUtils.printWarnings(null, new PrintWriter(new StringWriter()));
		JdbcUtils.disableCommitQuietly(null);
	}

	@Test
	public void quietHelpers_swallowSqlExceptions() throws SQLException {
		Connection closed = tracker.rawConnection();
		closed.close();
		JdbcUtils.commitQuietly(closed);
		JdbcUtils.rollbackQuietly(closed);
		JdbcUtils.commitAndCloseQuietly(closed);
		JdbcUtils.rollbackAndCloseQuietly(closed);
		JdbcUtils.disableCommitQuietly(closed);
		assertThrows(SQLException.class, () -> JdbcUtils.commit(closed));
		assertThrows(SQLException.class, () -> JdbcUtils.disableCommit(closed));
		Connection c = tracker.rawConnection();
		java.sql.Statement s = c.createStatement();
		java.sql.ResultSet rs = s.executeQuery("select 1");
		c.close();
		JdbcUtils.closeQuietly(rs);
		JdbcUtils.closeQuietly(s);
		JdbcUtils.closeQuietly(c);
	}

	@Test
	public void printStackTrace_followsNextExceptionsAndFlushes() throws SQLException {
		SQLException first = new SQLException("first");
		first.setNextException(new SQLException("second"));
		StringWriter out = new StringWriter();
		JdbcUtils.printStackTrace(first, new PrintWriter(out));
		String text = out.toString();
		assertTrue(text.contains("first"));
		assertTrue(text.contains("Next SQLException:"));
		assertTrue(text.contains("second"));

		Connection c = tracker.rawConnection();
		StringWriter warnings = new StringWriter();
		JdbcUtils.printWarnings(c, new PrintWriter(warnings));
		assertEquals("", warnings.toString());
		c.close();
		StringWriter closedOut = new StringWriter();
		JdbcUtils.printWarnings(c, new PrintWriter(closedOut));
		assertTrue(closedOut.toString().contains("SQLException") || closedOut.toString().length() > 0);

		Connection warned = (Connection) java.lang.reflect.Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { Connection.class },
				(p, m, a) -> m.getName().equals("getWarnings") ? new SQLWarning("careful") : null);
		StringWriter warnOut = new StringWriter();
		JdbcUtils.printWarnings(warned, new PrintWriter(warnOut));
		assertTrue(warnOut.toString().contains("careful"));
		JdbcUtils.printStackTrace(new SQLException("to stderr"));
		JdbcUtils.printWarnings(warned);
	}

	@Test
	public void setPath_changesCatalogAndSchemaOnlyWhenDifferent() throws SQLException {
		tracker.execute("create schema if not exists other");
		try (Connection c = tracker.rawConnection()) {
			String catalog = c.getCatalog();
			JdbcUtils.setPath(c, null, null);
			JdbcUtils.setPath(c, "", " ");
			assertEquals("PUBLIC", c.getSchema());
			JdbcUtils.setPath(c, catalog, "OTHER");
			assertEquals("OTHER", c.getSchema());
			JdbcUtils.setPath(c, catalog, "OTHER");
			assertEquals(catalog, c.getCatalog());
		}
	}

	@Test
	public void getConnection_opensRealConnection() throws SQLException {
		try (Connection c = JdbcUtils.getConnection(tracker.getUrl(), "sa", "")) {
			assertFalse(c.isClosed());
		}
	}

	@Test
	public void cursor_iteratesAndClosesConnectionWhenExhausted() throws SQLException {
		insertUsers(3);
		Connection c = tracker.connection();
		Cursor<Tuple> cursor = JdbcUtils.cursor(c, "select * from t_user order by id");
		assertTrue(cursor.isOpened());
		assertTrue(cursor.hasNext());
		assertTrue(cursor.hasNext(), "hasNext must not skip rows");
		assertEquals("user1", cursor.next().get("userName"));
		assertEquals(1, cursor.getRownum());
		assertEquals("user2", cursor.next().get("userName"));
		assertEquals("user3", cursor.next().get("userName"));
		assertFalse(cursor.hasNext());
		assertFalse(cursor.hasNext());
		assertFalse(cursor.isOpened());
		assertThrows(NoSuchElementException.class, () -> cursor.next());
		assertTrue(c.isClosed());
		assertNoOpenResources();
	}

	@Test
	public void cursor_withArgs_listAndMark() throws SQLException {
		insertUsers(5);
		Connection c = tracker.connection();
		Cursor<Tuple> cursor = JdbcUtils.cursor(c, "select * from t_user where age > ? order by id", new Object[] { 1 });
		cursor.mark(2);
		assertEquals("user4", cursor.next().get("userName"));
		List<Tuple> rest = cursor.list();
		assertEquals(1, rest.size());
		assertEquals("user5", rest.get(0).get("userName"));
		assertTrue(c.isClosed());
		assertThrows(DetachedSqlException.class, () -> cursor.getRownum());
		assertThrows(DetachedSqlException.class, () -> cursor.mark(1));
		assertNoOpenResources();
	}

	@Test
	public void cursor_closeEarly_releasesResourcesOnce() throws SQLException {
		insertUsers(3);
		Connection c = tracker.connection();
		Cursor<Tuple> cursor = JdbcUtils.cursor(c, "select * from t_user", (PreparedStatementCallback) null);
		assertTrue(cursor.hasNext());
		cursor.close();
		cursor.close();
		assertFalse(cursor.isOpened());
		assertFalse(cursor.hasNext());
		assertTrue(c.isClosed());
		assertNoOpenResources();
	}

	@Test
	public void cursor_errorWhileReading_throwsDetachedSqlException() throws SQLException {
		insertUsers(2);
		Connection c = tracker.connection();
		Cursor<Tuple> cursor = JdbcUtils.cursor(c, "select * from t_user");
		cursor.hasNext();
		tracker.closeAll();
		assertThrows(DetachedSqlException.class, () -> cursor.next());
		assertFalse(cursor.isOpened());

		Connection c2 = tracker.connection();
		Cursor<Tuple> cursor2 = JdbcUtils.cursor(c2, "select * from t_user");
		tracker.closeAll();
		assertThrows(DetachedSqlException.class, () -> cursor2.hasNext());
		assertFalse(cursor2.isOpened());
	}

	@Test
	public void scan_stopsAtMaxRecordsAndClosesCursor() throws SQLException {
		insertUsers(5);
		List<Object> names = new ArrayList<Object>();
		Connection c = tracker.connection();
		JdbcUtils.scan(c, "select * from t_user where age > ? order by id", new Object[] { 0 }, t -> names.add(t.get("userName")), 2);
		assertEquals(Arrays.asList("user1", "user2"), names);
		assertTrue(c.isClosed());
		names.clear();
		JdbcUtils.scan(tracker.connection(), "select * from t_user order by id", (Object[]) null, t -> names.add(t.get("AGE")), -1);
		assertEquals(5, names.size());
		assertNoOpenResources();
	}

	@Test
	public void scan_consumerFailure_stillClosesCursor() throws SQLException {
		insertUsers(3);
		Connection c = tracker.connection();
		assertThrows(IllegalStateException.class, () -> JdbcUtils.scan(c, "select * from t_user", (Object[]) null, t -> {
			throw new IllegalStateException("boom");
		}, 0));
		assertTrue(c.isClosed());
		assertNoOpenResources();
	}

	@Test
	public void batchScan_pagesThroughAllRecordsOrStopsAtMax() throws SQLException {
		insertUsers(25);
		List<Integer> sizes = new ArrayList<Integer>();
		JdbcUtils.batchScan(tracker.connectionFactory(), "select * from t_user order by id", (Object[]) null, 1, 10,
				list -> sizes.add(list.size()), -1);
		assertEquals(Arrays.asList(10, 10, 5), sizes);
		sizes.clear();
		JdbcUtils.batchScan(tracker.connectionFactory(), "select * from t_user order by id", new Object[0], 1, 10,
				list -> sizes.add(list.size()), 10);
		assertEquals(Arrays.asList(10), sizes);
		sizes.clear();
		JdbcUtils.batchScan(tracker.connectionFactory(), "select * from t_user where age > ? order by id", ps -> ps.setInt(1, 20), 1, 3,
				list -> sizes.add(list.size()), 0);
		assertEquals(Arrays.asList(3, 2), sizes);
		assertEquals(0, tracker.openConnections());
		assertNoOpenResources();
	}

	@Test
	public void batchScan_withDataSource() throws SQLException {
		insertUsers(4);
		UnpooledDataSource dataSource = new UnpooledDataSource("org.h2.Driver", tracker.getUrl(), "sa", "", 2);
		List<Integer> sizes = new ArrayList<Integer>();
		JdbcUtils.batchScan(dataSource, "select * from t_user", new Object[0], 1, 3, list -> sizes.add(list.size()), -1);
		JdbcUtils.batchScan(dataSource, "select * from t_user where age > ?", ps -> ps.setInt(1, 2), 1, 3, list -> sizes.add(list.size()),
				-1);
		assertEquals(Arrays.asList(3, 1, 2), sizes);
	}

	@Test
	public void pageableQuery_factories() throws SQLException {
		insertUsers(7);
		UnpooledDataSource dataSource = new UnpooledDataSource("org.h2.Driver", tracker.getUrl(), "sa", "", 2);
		assertEquals(7, JdbcUtils.pageableQuery(dataSource, "select * from t_user", (Object[]) null).rowCount());
		assertEquals(3, JdbcUtils.pageableQuery(dataSource, "select * from t_user where age > ?", new Object[] { 4 }).rowCount());
		assertEquals(3, JdbcUtils.pageableQuery(dataSource, new DefaultPageableSql("select * from t_user where age > ?"), new Object[] { 4 })
				.list().size());
		assertEquals(2, JdbcUtils.pageableQuery(dataSource, new DefaultPageableSql("select * from t_user"), (Object[]) null).list(2).size());
		assertEquals(1, JdbcUtils.pageableQuery(tracker.connectionFactory(), "select * from t_user where age = ?", new Object[] { 1 })
				.rowCount());
		PageableQuery<Tuple> query = JdbcUtils.pageableQuery(tracker.connectionFactory(), new DefaultPageableSql("select * from t_user order by id"),
				new Object[0]);
		assertEquals("user1", query.first().get("userName"));
		assertEquals(0, tracker.openConnections());
	}

	@Test
	public void rowCountAndTruncate() throws SQLException {
		insertUsers(4);
		try (Connection c = tracker.connection()) {
			assertEquals(4L, JdbcUtils.rowCount(c, "select * from t_user"));
			assertEquals(2L, JdbcUtils.rowCount(c, "select * from t_user where age > ?", new Object[] { 2 }));
			assertEquals(1L, JdbcUtils.rowCount(c, "select * from t_user where age = ?", ps -> ps.setInt(1, 1)));
			JdbcUtils.truncateTable(c, "t_user");
			assertEquals(0L, JdbcUtils.rowCount(c, "select * from t_user"));
		}
		assertNoOpenResources();
	}

	@Test
	public void existsTable_andMetadataCursors() throws SQLException {
		tracker.execute("create table t_role(id int primary key, user_id bigint, foreign key (user_id) references t_user(id))");
		try (Connection c = tracker.connection()) {
			assertTrue(JdbcUtils.existsTable(c, "PUBLIC", "T_USER"));
			assertTrue(JdbcUtils.existsTable(c, null, "PUBLIC", "T_ROLE"));
			assertFalse(JdbcUtils.existsTable(c, "PUBLIC", "T_NONE"));
			// '_' is a wildcard of the table name pattern: a table sorted before the exact match must not hide it
			tracker.execute("create table tauser(id int)");
			assertTrue(JdbcUtils.existsTable(c, "PUBLIC", "T_USER"));
			assertTrue(JdbcUtils.existsTable(c, "PUBLIC", "TAUSER"));
			assertFalse(JdbcUtils.existsTable(c, "PUBLIC", "T_USE_"));
			tracker.execute("drop table tauser");

			List<String> tables = new ArrayList<String>();
			for (Tuple t : JdbcUtils.describe(c, null, "PUBLIC").list()) {
				tables.add(t.getProperty("tableName"));
			}
			assertEquals(Arrays.asList("T_ROLE", "T_USER"), tables);
			assertTrue(JdbcUtils.describe(c).list().size() > 2);

			List<Tuple> columns = JdbcUtils.getTableMetadata(c, "T_USER").list();
			assertEquals(9, columns.size());
			assertEquals("ID", columns.get(0).getProperty("columnName"));
			assertEquals(9, JdbcUtils.getTableMetadata(c, null, "PUBLIC", "T_USER").list().size());

			List<Tuple> pks = JdbcUtils.getPkMetadata(c, "T_USER").list();
			assertEquals(1, pks.size());
			assertEquals("ID", pks.get(0).getProperty("columnName"));
			assertEquals(1, JdbcUtils.getPkMetadata(c, null, "PUBLIC", "T_ROLE").list().size());

			List<Tuple> fks = JdbcUtils.getFkMetadata(c, "T_ROLE").list();
			assertEquals(1, fks.size());
			assertEquals("T_USER", fks.get(0).getProperty("pktableName"));
			assertTrue(JdbcUtils.getFkMetadata(c, null, "PUBLIC", "T_USER").list().isEmpty());
			assertFalse(c.isClosed(), "metadata cursors must not close the connection");
		}
		assertNoOpenResources();
	}

	@Test
	public void toTuple_respectsCaseFormat() throws SQLException {
		insertUsers(1);
		try (Connection c = tracker.rawConnection();
				java.sql.Statement s = c.createStatement();
				java.sql.ResultSet rs = s.executeQuery("select user_name, age from t_user")) {
			assertTrue(rs.next());
			Tuple tuple = JdbcUtils.toTuple(rs, CaseFormats.UPPER_CAMEL);
			assertEquals("user1", tuple.get("UserName"));
			assertEquals(2, tuple.size());
			assertEquals(1, JdbcUtils.toTuple(rs).get("AGE"));
		}
	}

	@Test
	public void setValues_handlesNullAndEmptyArgs() throws SQLException {
		try (Connection c = tracker.rawConnection(); java.sql.PreparedStatement ps = c.prepareStatement("select ? from dual")) {
			JdbcUtils.setValues(ps, null);
			JdbcUtils.setValues(ps, new Object[0]);
			JdbcUtils.setValues(ps, new Object[] { "x" });
			try (java.sql.ResultSet rs = ps.executeQuery()) {
				assertTrue(rs.next());
				assertEquals("x", rs.getString(1));
			}
		}
		assertNotNull(JdbcUtils.SQL_ROW_COUNT);
	}
}
