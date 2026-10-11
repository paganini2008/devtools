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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import com.github.paganini2008.devtools.collection.Tuple;

/**
 * PageableQueryImplTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
@Timeout(20)
public class PageableQueryImplTest {

	private JdbcTracker tracker;

	@BeforeEach
	public void setUp() throws SQLException {
		tracker = new JdbcTracker("jdbc_pageable_query_test");
		tracker.execute("create table t_num(id int primary key, val varchar(10))");
		List<Object[]> argsList = new ArrayList<Object[]>();
		for (int i = 1; i <= 23; i++) {
			argsList.add(new Object[] { i, "v" + i });
		}
		try (Connection c = tracker.rawConnection()) {
			JdbcUtils.batchUpdate(c, "insert into t_num(id, val) values (?, ?)", argsList);
		}
	}

	@AfterEach
	public void tearDown() throws SQLException {
		tracker.drop();
	}

	@Test
	public void rowCountListAndPaging() throws SQLException {
		PageableQuery<Tuple> query = new PageableQueryImpl(tracker.connectionFactory(), new DefaultPageableSql("select * from t_num order by id"),
				null);
		assertEquals(23, query.rowCount());
		assertEquals(23, query.list().size());
		List<Tuple> slice = query.list(5, 10);
		assertEquals(5, slice.size());
		assertEquals(11, slice.get(0).get("ID"));
		List<Integer> sizes = new ArrayList<Integer>();
		for (PageResponse<Tuple> page : query.forEach(1, 10)) {
			sizes.add(page.getContent().size());
		}
		assertEquals(Arrays.asList(10, 10, 3), sizes);
		PageResponse<Tuple> page = query.list(PageRequest.of(3, 10));
		assertEquals(3, ((PageableQueryImpl) query).getPageNumber());
		assertEquals(3, page.getContent().size());
		assertEquals(0, tracker.openConnections());
		assertEquals(0, tracker.openResources());
	}

	@Test
	public void rowCount_withParameters() throws SQLException {
		PageableQuery<Tuple> query = new PageableQueryImpl(tracker.connectionFactory(),
				new DefaultPageableSql("select * from t_num where id > ?"), ps -> ps.setInt(1, 20));
		assertEquals(3, query.rowCount());
		assertEquals(3, query.list().size());
	}

	@Test
	public void rowCount_returnsZeroWhenCountSqlReturnsNoRow() {
		PageableSql sql = new PageableSql() {

			@Override
			public String countableSql() {
				return "select id from t_num where 1 = 0";
			}

			@Override
			public String pageableSql(int maxResults, int firstResult) {
				return "select * from t_num";
			}
		};
		assertEquals(0, new PageableQueryImpl(tracker.connectionFactory(), sql, null).rowCount());
	}

	@Test
	public void errors_areWrappedAndConnectionsClosed() throws SQLException {
		PageableQuery<Tuple> query = new PageableQueryImpl(tracker.connectionFactory(), new DefaultPageableSql("select * from no_table"), null);
		PageableException e1 = assertThrows(PageableException.class, () -> query.rowCount());
		assertTrue(e1.getCause() instanceof SQLException);
		assertThrows(PageableException.class, () -> query.list(10, 0));
		PageableQuery<Tuple> failing = new PageableQueryImpl(tracker.connectionFactory(), new DefaultPageableSql("select * from t_num"), ps -> {
			throw new IllegalStateException("callback failed");
		});
		assertThrows(IllegalStateException.class, () -> failing.list(10, 0));
		assertEquals(0, tracker.openConnections());
		assertEquals(0, tracker.openResources());
		PageableQuery<Tuple> noConnection = new PageableQueryImpl(() -> {
			throw new SQLException("no connection");
		}, new DefaultPageableSql("select 1"), null);
		assertThrows(PageableException.class, () -> noConnection.list(1, 0));
		assertThrows(PageableException.class, () -> noConnection.rowCount());
	}

	@Test
	public void exceptionsAndSmallTypes() {
		assertEquals("m", new PageableException("m").getMessage());
		Throwable cause = new RuntimeException("c");
		assertSame(cause, new PageableException("m", cause).getCause());
		assertEquals("d", new DetachedSqlException("d").getMessage());
		assertSame(cause, new DetachedSqlException("d", cause).getCause());
		assertSame(cause, new DetachedSqlException(cause).getCause());
		assertEquals("j", new JdbcDumpException("j").getMessage());
		assertSame(cause, new JdbcDumpException("j", cause).getCause());
		assertSame(cause, new JdbcDumpException(cause).getCause());
		assertEquals("?", new DefaultTokenHandler().handleToken("name"));
		TokenHandler upper = token -> token.toUpperCase();
		assertEquals("A", upper.handleToken("a"));

		SQLException root = new SQLException("root");
		assertSame(root, ExceptionUtils.unwrapThrowable(new InvocationTargetException(new UndeclaredThrowableException(root))));
		assertSame(root, ExceptionUtils.unwrapThrowable(root));
		assertEquals(null, ExceptionUtils.unwrapThrowable(null));

		for (TransactionIsolationLevel level : TransactionIsolationLevel.values()) {
			assertSame(level, TransactionIsolationLevel.get(level.getLevel()));
		}
		assertEquals(Connection.TRANSACTION_SERIALIZABLE, TransactionIsolationLevel.SERIALIZABLE.getLevel());
		assertThrows(IllegalArgumentException.class, () -> TransactionIsolationLevel.get(99));

		DumpProgress.DEFAULT.onStart(null, null, null, null, null);
		DumpProgress.DEFAULT.progress(null, null, null, null, 1, 2, null);
		DumpProgress.DEFAULT.onEnd(null, null, null, null, null);
	}
}
