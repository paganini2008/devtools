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
package com.github.paganini2008.devtools.objectpool.dbpool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import com.github.paganini2008.devtools.jdbc.JdbcUtils;

/**
 * ConnectionPoolTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
@Timeout(30)
public class ConnectionPoolTest {

	private static final String URL = "jdbc:h2:mem:dbpool_connection_pool_test;DB_CLOSE_DELAY=-1";

	private ConnectionPool pool;
	private Connection keeper;

	@BeforeEach
	public void setUp() throws SQLException {
		keeper = DriverManager.getConnection(URL, "sa", "");
		try (Statement s = keeper.createStatement()) {
			s.execute("create table if not exists t_pool(id int primary key, name varchar(20))");
			s.execute("delete from t_pool");
		}
		pool = newPool();
	}

	private ConnectionPool newPool() {
		ConnectionPool pool = new ConnectionPool();
		pool.setDriverClassName("org.h2.Driver");
		pool.setJdbcUrl(URL);
		pool.setUser("sa");
		pool.setPassword("");
		pool.setMaxSize(2);
		pool.setMaxWaitTime(200);
		pool.setCloseTimeout(200);
		return pool;
	}

	@AfterEach
	public void tearDown() throws SQLException {
		pool.close();
		try (Statement s = keeper.createStatement()) {
			s.execute("drop all objects");
		}
		keeper.close();
	}

	private static Connection real(Connection proxy) throws SQLException {
		return proxy.unwrap(Connection.class);
	}

	@Test
	public void take_returnsProxyWhichGoesBackToPoolWhenClosed() throws SQLException {
		Connection c1 = pool.take();
		Connection real1 = real(c1);
		assertNotSame(real1, c1);
		assertTrue(c1.isValid(1));
		assertFalse(c1.isClosed());
		assertEquals(1, pool.getObjectPool().getBusySize());
		c1.close();
		assertTrue(c1.isClosed());
		assertFalse(c1.isValid(1));
		assertFalse(real1.isClosed(), "the physical connection stays open in the pool");
		assertEquals(1, pool.getObjectPool().getIdleSize());
		Connection c2 = pool.take();
		assertSame(real1, real(c2));
		assertNotSame(c1, c2, "each borrowing gets a new proxy");
		c2.close();
		c2.close();
		assertEquals(1, pool.getObjectPool().getIdleSize());
		assertEquals(60000L, pool.getConnectionTimeout());
		assertEquals(200L, pool.getMaxWaitTime());
	}

	@Test
	public void staleProxy_cannotTouchConnectionLentToOthers() throws SQLException {
		Connection c1 = pool.take();
		c1.close();
		Connection c2 = pool.take();
		SQLException e = assertThrows(SQLException.class, () -> c1.createStatement());
		assertTrue(e.getMessage().contains("closed"));
		assertThrows(SQLException.class, () -> c1.prepareStatement("select 1"));
		assertThrows(SQLException.class, () -> c1.setAutoCommit(false));
		c1.close();
		assertFalse(c2.isClosed(), "closing a stale proxy must not give back the current borrowing");
		assertTrue(c2.equals(c2));
		assertFalse(c2.equals(c1));
		assertEquals(System.identityHashCode(c2), c2.hashCode());
		assertNotNull(c2.toString());
		c2.close();
	}

	@Test
	public void take_timesOutWhenExhausted() throws SQLException {
		Connection c1 = pool.take();
		Connection c2 = pool.take();
		long start = System.nanoTime();
		SQLException e = assertThrows(SQLException.class, () -> pool.take());
		assertTrue(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start) >= 150);
		assertTrue(e.getCause() instanceof com.github.paganini2008.devtools.objectpool.PooledObjectException);
		c1.close();
		pool.take().close();
		c2.close();
	}

	@Test
	public void giveback_rollsBackAndRestoresConnectionState() throws SQLException {
		Connection c1 = pool.take();
		Connection real1 = real(c1);
		int isolation = c1.getTransactionIsolation();
		c1.setAutoCommit(false);
		c1.setReadOnly(true);
		c1.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		c1.setReadOnly(false);
		JdbcUtils.update(c1, "insert into t_pool values (1, 'uncommitted')");
		c1.setReadOnly(true);
		c1.close();
		Connection c2 = pool.take();
		assertSame(real1, real(c2));
		assertTrue(c2.getAutoCommit());
		assertFalse(c2.isReadOnly());
		assertEquals(isolation, c2.getTransactionIsolation());
		assertEquals(0L, JdbcUtils.rowCount(c2, "select * from t_pool"));
		c2.setAutoCommit(false);
		JdbcUtils.update(c2, "insert into t_pool values (2, 'committed')");
		c2.commit();
		c2.close();
		assertEquals(1L, JdbcUtils.rowCount(keeper, "select * from t_pool"));
	}

	@Test
	public void configuredAutoCommitAndIsolation_areApplied() throws SQLException {
		pool.close();
		pool = newPool();
		pool.setAutoCommit(false);
		pool.setDefaultTransactionIsolationLevel(Connection.TRANSACTION_SERIALIZABLE);
		Connection c = pool.take();
		assertFalse(c.getAutoCommit());
		assertEquals(Connection.TRANSACTION_SERIALIZABLE, c.getTransactionIsolation());
		c.setAutoCommit(true);
		c.close();
		Connection again = pool.take();
		assertFalse(again.getAutoCommit());
		again.close();
	}

	@Test
	public void brokenConnection_isDiscardedOnGiveback() throws SQLException {
		Connection c1 = pool.take();
		real(c1).close();
		c1.close();
		assertEquals(0, pool.getObjectPool().getPoolSize());
		Connection c2 = pool.take();
		assertFalse(c2.isClosed());
		c2.close();
	}

	@Test
	public void preparedStatements_areCachedPerConnection() throws SQLException {
		Connection c = pool.take();
		PreparedStatement ps1 = c.prepareStatement("select * from t_pool where id = ?");
		PreparedStatement real1 = ps1.unwrap(PreparedStatement.class);
		ps1.setInt(1, 1);
		ResultSet rs = ps1.executeQuery();
		ps1.close();
		assertTrue(rs.isClosed(), "result sets are closed when the statement goes back to the cache");
		assertTrue(ps1.isClosed());
		ps1.close();
		assertThrows(SQLException.class, () -> ps1.executeQuery(), "a stale statement proxy must be unusable");
		PreparedStatement ps2 = c.prepareStatement("select * from t_pool where id = ?");
		assertSame(real1, ps2.unwrap(PreparedStatement.class));
		assertNotSame(ps1, ps2);
		assertFalse(ps1.equals(ps2));
		assertTrue(ps2.equals(ps2));
		assertEquals(System.identityHashCode(ps2), ps2.hashCode());
		assertNotNull(ps2.toString());
		assertSame(c, ps2.getConnection(), "statement must expose the proxy connection");
		PreparedStatement concurrent = c.prepareStatement("select * from t_pool where id = ?");
		assertNotSame(real1, concurrent.unwrap(PreparedStatement.class), "a statement in use must not be shared");
		PreparedStatement withKeys = c.prepareStatement("insert into t_pool values (?, ?)", Statement.RETURN_GENERATED_KEYS);
		withKeys.setInt(1, 9);
		withKeys.setString(2, "nine");
		assertEquals(1, withKeys.executeUpdate());
		withKeys.close();
		ps2.close();
		concurrent.close();
		c.close();
		assertEquals(1L, JdbcUtils.rowCount(keeper, "select * from t_pool"));
	}

	@Test
	public void unclosedStatements_areReclaimedWhenConnectionCloses() throws SQLException {
		Connection c = pool.take();
		PreparedStatement ps = c.prepareStatement("select * from t_pool");
		PreparedStatement realPs = ps.unwrap(PreparedStatement.class);
		ResultSet rs = ps.executeQuery();
		ps.executeQuery();
		Statement plain = c.createStatement();
		c.close();
		assertTrue(rs.isClosed());
		assertTrue(ps.isClosed());
		assertThrows(SQLException.class, () -> ps.executeQuery());
		plain.close();
		Connection again = pool.take();
		PreparedStatement reused = again.prepareStatement("select * from t_pool");
		assertSame(realPs, reused.unwrap(PreparedStatement.class));
		reused.close();
		again.close();
	}

	@Test
	public void statementCacheSize_limitsCachedStatements() throws SQLException {
		pool.close();
		pool = newPool();
		pool.setStatementCacheSize(1);
		Connection c = pool.take();
		PreparedStatement a = c.prepareStatement("select 1");
		PreparedStatement realA = a.unwrap(PreparedStatement.class);
		a.close();
		PreparedStatement b = c.prepareStatement("select 2");
		PreparedStatement realB = b.unwrap(PreparedStatement.class);
		b.close();
		assertTrue(realA.isClosed(), "the evicted statement is closed");
		assertFalse(realB.isClosed());
		c.close();

		pool.close();
		pool = newPool();
		pool.setStatementCacheSize(0);
		Connection c2 = pool.take();
		PreparedStatement d = c2.prepareStatement("select 1");
		PreparedStatement realD = d.unwrap(PreparedStatement.class);
		d.close();
		assertTrue(realD.isClosed(), "statements are not cached when cache size is 0");
		c2.close();
	}

	@Test
	public void invalidSqlOnPrepare_throwsOriginalSqlException() throws SQLException {
		Connection c = pool.take();
		assertThrows(java.sql.SQLSyntaxErrorException.class, () -> c.prepareStatement("select * from no_table"));
		PreparedStatement ps = c.prepareStatement("select * from t_pool where id = ?");
		assertThrows(SQLException.class, () -> ps.setString(5, "x"));
		assertThrows(SQLException.class, () -> c.setTransactionIsolation(12345));
		ps.close();
		c.close();
	}

	@Test
	public void queryStatistics_recordExecuteQuery() throws SQLException {
		pool.getQueryStatistics().setAcceptableExecutionTime(-1);
		pool.getQueryStatistics().setStatisticalSampleCount(2);
		Connection c = pool.take();
		for (int i = 0; i < 3; i++) {
			PreparedStatement ps = c.prepareStatement("select * from t_pool where id = ?");
			ps.setInt(1, i);
			ps.executeQuery().close();
			ps.close();
		}
		PreparedStatement update = c.prepareStatement("delete from t_pool where id = ?");
		update.setInt(1, 0);
		update.executeUpdate();
		update.close();
		c.close();
		String today = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
		Map<String, QuerySpan> result = pool.getStatisticsResult(today);
		assertEquals(1, result.size());
		QuerySpan span = result.get("select * from t_pool where id = ?");
		assertEquals(3, span.getExecutionCount());
		assertEquals(2, span.getQueryTraces().size());
		assertNull(pool.getStatisticsResult("01/01/1970"));
	}

	@Test
	public void testSql_validatesNewConnections() throws SQLException {
		pool.close();
		pool = newPool();
		pool.setTestSql("select 1");
		pool.take().close();
		pool.close();
		pool = newPool();
		pool.setTestSql("select * from no_table");
		SQLException e = assertThrows(SQLException.class, () -> pool.take());
		assertTrue(e.getMessage().contains("Invalid object"));
		assertEquals(0, pool.getObjectPool().getPoolSize());
	}

	@Test
	public void connectionOperation_timesOutAndDiscardsConnection() throws Exception {
		pool.close();
		pool = newPool();
		ExecutorService blocked = Executors.newSingleThreadExecutor();
		CountDownLatch release = new CountDownLatch(1);
		pool.setExecutor(blocked);
		pool.setConnectionTimeout(100);
		Connection c = pool.take();
		Connection real = real(c);
		blocked.submit(() -> {
			release.await();
			return null;
		});
		SQLException e = assertThrows(SQLException.class, () -> c.getAutoCommit());
		assertTrue(e.getMessage().contains("timeout"));
		release.countDown();
		assertTrue(c.isClosed());
		assertTrue(real.isClosed(), "a timed out connection is discarded");
		assertEquals(0, pool.getObjectPool().getPoolSize());
		c.close();
		Connection next = pool.take();
		assertTrue(next.getAutoCommit());
		assertThrows(SQLException.class, () -> next.setTransactionIsolation(12345), "async errors are unwrapped");
		next.close();
		pool.close();
		assertTrue(blocked.isShutdown());
		pool = newPool();
		ExecutorService rejected = Executors.newSingleThreadExecutor();
		rejected.shutdown();
		pool.setExecutor(rejected);
		Connection r = pool.take();
		SQLException e2 = assertThrows(SQLException.class, () -> r.getAutoCommit());
		assertTrue(e2.getMessage().contains("rejected"));
		pool.setConnectionTimeout(0);
		pool.setExecutor(null);
		r.close();
	}

	@Test
	public void withoutExecutor_operationsRunInCallerThread() throws SQLException {
		pool.close();
		pool = newPool();
		pool.setExecutor(null);
		assertNull(pool.getConnectionFactory().getExecutor());
		Connection c = pool.take();
		c.setAutoCommit(false);
		assertFalse(c.getAutoCommit());
		assertThrows(SQLException.class, () -> c.setTransactionIsolation(12345), "the original SQLException is thrown");
		PreparedStatement ps = c.prepareStatement("select 1");
		PooledConnection pooled = (PooledConnection) java.lang.reflect.Proxy.getInvocationHandler(c);
		PreparedStatementCache cache = pooled.getStatementCache();
		assertEquals(1, cache.leasedSize());
		ps.close();
		assertEquals(0, cache.leasedSize());
		assertEquals(1, cache.size());
		c.close();
	}

	@Test
	public void connectionOperation_interruptedWhileWaiting() throws Exception {
		pool.close();
		pool = newPool();
		ExecutorService blocked = Executors.newSingleThreadExecutor();
		CountDownLatch release = new CountDownLatch(1);
		pool.setExecutor(blocked);
		pool.setConnectionTimeout(0);
		Connection c = pool.take();
		blocked.submit(() -> {
			release.await();
			return null;
		});
		Thread.currentThread().interrupt();
		SQLException e = assertThrows(SQLException.class, () -> c.getAutoCommit());
		assertTrue(Thread.interrupted());
		assertTrue(e.getCause() instanceof InterruptedException);
		release.countDown();
		c.close();
	}

	@Test
	public void expiredConnection_isClosedWhenFinallyReturned() throws Exception {
		pool.setMaxWaitTimeForExpiration(20);
		pool.setCheckObjectExpiredInterval(10);
		pool.setCheckObjectExpired(true);
		Connection c = pool.take();
		Connection real = real(c);
		long deadline = System.currentTimeMillis() + 5000;
		while (pool.getObjectPool().getPoolSize() > 0 && System.currentTimeMillis() < deadline) {
			Thread.sleep(5);
		}
		assertEquals(0, pool.getObjectPool().getPoolSize());
		c.close();
		assertTrue(real.isClosed());
	}

	@Test
	public void close_closesAllConnectionsAndRejectsTake() throws SQLException {
		Connection idle = pool.take();
		Connection realIdle = real(idle);
		idle.close();
		pool.close();
		assertTrue(realIdle.isClosed());
		assertThrows(SQLException.class, () -> pool.take());
	}

	@Test
	public void otherSettings_arePassedToTheObjectPool() throws SQLException {
		pool.setMaxIdleSize(1);
		pool.setMaxUsage(1);
		pool.setTestWhileIdleInterval(1000);
		pool.setTestWhileIdle(true);
		assertEquals(1, pool.getObjectPool().getMaxIdleSize());
		assertEquals(1, pool.getObjectPool().getMaxUsage());
		assertTrue(pool.getObjectPool().isTestWhileIdle());
		Connection c = pool.take();
		Connection real = real(c);
		c.close();
		assertTrue(real.isClosed(), "maxUsage 1 destroys the connection after one use");
	}

	@Test
	public void concurrentBorrowers_shareTheBoundedPool() throws Exception {
		pool.setMaxSize(3);
		pool.setMaxWaitTime(10000);
		final int threads = 8;
		final AtomicInteger active = new AtomicInteger();
		final AtomicInteger maxActive = new AtomicInteger();
		final CountDownLatch start = new CountDownLatch(1);
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		List<Future<?>> futures = new ArrayList<Future<?>>();
		for (int i = 0; i < threads; i++) {
			final int id = i;
			futures.add(executor.submit(() -> {
				start.await();
				for (int j = 0; j < 25; j++) {
					try (Connection c = pool.take()) {
						maxActive.accumulateAndGet(active.incrementAndGet(), Math::max);
						try (PreparedStatement ps = c.prepareStatement("insert into t_pool values (?, ?)")) {
							ps.setInt(1, id * 1000 + j);
							ps.setString(2, "t" + id);
							ps.executeUpdate();
						}
						active.decrementAndGet();
					}
				}
				return null;
			}));
		}
		start.countDown();
		for (Future<?> f : futures) {
			f.get(20, TimeUnit.SECONDS);
		}
		executor.shutdown();
		assertTrue(maxActive.get() <= 3);
		assertTrue(pool.getObjectPool().getPoolSize() <= 3);
		assertEquals(0, pool.getObjectPool().getBusySize());
		assertEquals(threads * 25L, JdbcUtils.rowCount(keeper, "select * from t_pool"));
	}

	@Test
	public void connectionObjectFactory_accessorsAndValidation() throws SQLException {
		ConnectionObjectFactory factory = pool.getConnectionFactory();
		factory.setTestSql(" ");
		factory.setStatementCacheSize(null);
		assertNull(factory.getStatementCacheSize());
		assertEquals(URL, factory.getJdbcUrl());
		assertEquals("sa", factory.getUser());
		assertEquals("", factory.getPassword());
		assertEquals("org.h2.Driver", factory.getDriverClassName());
		assertEquals(" ", factory.getTestSql());
		assertNull(factory.getAutoCommit());
		assertNull(factory.getDefaultTransactionIsolationLevel());
		assertNotNull(factory.getExecutor());
		assertFalse(factory.validateObject(null));
		assertFalse(factory.testObject("not a connection"));
		factory.destroyObject(null);
		PooledConnection pc = factory.createObject();
		assertTrue(factory.testObject(pc));
		assertTrue(pc.isOpened());
		assertFalse(pc.isValid());
		assertNotNull(pc.getRealConnection());
		assertNotNull(pc.getProxyConnection());
		assertEquals(0, pc.getStatementCache().size());
		factory.destroyObject(pc);
		assertFalse(pc.isOpened());
		assertFalse(factory.testObject(pc));
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> factory.setDriverClassName("no.such.Driver"));
		assertTrue(e.getMessage().contains("no.such.Driver"));
		factory.setJdbcUrl("jdbc:h2:mem:dbpool_connection_pool_test;IFEXISTS=TRUE;NO_SUCH_SETTING=1");
		assertThrows(SQLException.class, () -> factory.createObject());
		factory.setJdbcUrl(URL);
		factory.setDefaultTransactionIsolationLevel(12345);
		assertThrows(SQLException.class, () -> factory.createObject());
		factory.setDefaultTransactionIsolationLevel(null);
		ExecutorService custom = Executors.newSingleThreadExecutor();
		factory.setExecutor(custom);
		assertSame(custom, factory.getExecutor());
		factory.setExecutor(custom);
		custom.shutdown();
		assertNotEquals(0, factory.hashCode());
	}

	@Test
	public void genericDataSource_delegatesToPool() throws SQLException {
		GenericDataSource dataSource = new GenericDataSource("org.h2.Driver", URL, "sa", "");
		dataSource.setTestSql("select 1");
		dataSource.setAutoCommit(null);
		dataSource.setAutoCommit(true);
		dataSource.setDefaultTransactionIsolationLevel(null);
		dataSource.setDefaultTransactionIsolationLevel(Connection.TRANSACTION_READ_COMMITTED);
		dataSource.setMaxIdleSize(2);
		dataSource.setMaxSize(2);
		dataSource.setMaxUsage(10);
		dataSource.setTestWhileIdleInterval(5000);
		dataSource.setTestWhileIdle(true);
		dataSource.setCheckObjectExpiredInterval(5000);
		dataSource.setCheckObjectExpired(true);
		dataSource.setMaxWaitTimeForExpiration(60000);
		dataSource.setAcceptableExecutionTime(-1);
		dataSource.setStatisticalSampleCount(5);
		try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement("select count(*) from t_pool")) {
			try (ResultSet rs = ps.executeQuery()) {
				assertTrue(rs.next());
			}
			assertEquals(Connection.TRANSACTION_READ_COMMITTED, c.getTransactionIsolation());
		}
		String today = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
		assertEquals(1, dataSource.getStatisticsResult(today).size());
		dataSource.close();
		assertThrows(SQLException.class, () -> dataSource.getConnection());
		GenericDataSource empty = new GenericDataSource();
		empty.setDriverClassName("org.h2.Driver");
		empty.setJdbcUrl(URL);
		empty.setUser("sa");
		empty.setPassword("");
		empty.getConnection().close();
		empty.close();
	}

	@Test
	public void genericDataSource_forwardsPoolTimeoutsAndExecutor() throws Exception {
		GenericDataSource dataSource = new GenericDataSource("org.h2.Driver", URL, "sa", "");
		ExecutorService executor = Executors.newSingleThreadExecutor();
		try {
			dataSource.setMaxSize(1);
			dataSource.setMaxWaitTime(100);
			dataSource.setStatementCacheSize(0);
			dataSource.setExecutor(executor);
			dataSource.setConnectionTimeout(5000);
			dataSource.setCloseTimeout(100);

			Connection held = dataSource.getConnection();
			try (PreparedStatement ps = held.prepareStatement("select count(*) from t_pool"); ResultSet rs = ps.executeQuery()) {
				assertTrue(rs.next());
			}
			long begin = System.nanoTime();
			assertThrows(SQLException.class, () -> dataSource.getConnection());
			assertTrue(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - begin) < 5000L);

			begin = System.nanoTime();
			dataSource.close();
			assertTrue(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - begin) < 5000L);
		} finally {
			executor.shutdownNow();
		}
	}

	@Test
	public void plainStatements_areClosedWhenConnectionIsGivenBack() throws SQLException {
		for (boolean withExecutor : new boolean[] { true, false }) {
			pool.close();
			pool = newPool();
			if (!withExecutor) {
				pool.setExecutor(null);
			}
			Connection c = pool.take();
			Connection real = real(c);
			Statement plain = c.createStatement();
			ResultSet rs = plain.executeQuery("select 1");
			Statement scrollable = c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			Statement call = c.prepareCall("call 1");
			Statement closedByUser = c.createStatement();
			closedByUser.close();
			c.close();
			assertTrue(plain.isClosed(), "a statement left open by the borrower must be closed on giveback");
			assertTrue(rs.isClosed());
			assertTrue(scrollable.isClosed());
			assertTrue(call.isClosed());
			assertFalse(real.isClosed(), "the physical connection stays in the pool");
			Connection again = pool.take();
			assertSame(real, real(again));
			try (Statement s = again.createStatement(); ResultSet r = s.executeQuery("select 2")) {
				assertTrue(r.next());
				assertEquals(2, r.getInt(1));
			}
			again.close();
		}
	}

	@Test
	public void cachedStatement_stateSetByPreviousBorrowerIsReset() throws SQLException {
		JdbcUtils.update(keeper, "insert into t_pool values (1, 'a'), (2, 'b'), (3, 'c')");
		Connection c = pool.take();
		PreparedStatement ps = c.prepareStatement("select * from t_pool order by id");
		PreparedStatement realPs = ps.unwrap(PreparedStatement.class);
		int defaultFetchSize = ps.getFetchSize();
		ps.setMaxRows(1);
		ps.setQueryTimeout(5);
		ps.setFetchSize(1);
		ps.setMaxFieldSize(1);
		try (ResultSet rs = ps.executeQuery()) {
			assertTrue(rs.next());
			assertFalse(rs.next());
		}
		ps.close();
		c.close();

		Connection next = pool.take();
		PreparedStatement reused = next.prepareStatement("select * from t_pool order by id");
		assertSame(realPs, reused.unwrap(PreparedStatement.class), "the statement comes from the cache");
		assertEquals(0, reused.getMaxRows(), "maxRows of the previous borrower must not leak");
		assertEquals(0, reused.getQueryTimeout());
		assertEquals(0, reused.getMaxFieldSize());
		assertEquals(defaultFetchSize, reused.getFetchSize());
		int rows = 0;
		try (ResultSet rs = reused.executeQuery()) {
			while (rs.next()) {
				assertEquals(1, rs.getString(2).length());
				rows++;
			}
		}
		assertEquals(3, rows);
		reused.close();
		next.close();
	}

	@Test
	public void interruptedQueuedOperation_isCancelledAndNeverRunsLater() throws Exception {
		pool.close();
		pool = newPool();
		ExecutorService blocked = Executors.newSingleThreadExecutor();
		CountDownLatch release = new CountDownLatch(1);
		pool.setExecutor(blocked);
		pool.setConnectionTimeout(0);
		Connection c = pool.take();
		Connection real = real(c);
		blocked.submit(() -> {
			release.await();
			return null;
		});
		Thread.currentThread().interrupt();
		SQLException e = assertThrows(SQLException.class, () -> c.setAutoCommit(false));
		assertTrue(Thread.interrupted());
		assertTrue(e.getCause() instanceof InterruptedException);
		release.countDown();
		// drain the executor: anything queued before this task has run when it completes
		blocked.submit(() -> null).get(5, TimeUnit.SECONDS);
		assertTrue(real.getAutoCommit(), "the interrupted operation must not be executed afterwards");
		assertFalse(c.isClosed(), "a cancelled operation leaves the connection usable");
		c.close();
	}

	@Test
	public void close_waitsForBorrowedConnectionWhichIsRolledBackAndClosedOnGiveback() throws Exception {
		pool.setCloseTimeout(5000);
		Connection c = pool.take();
		Connection real = real(c);
		c.setAutoCommit(false);
		JdbcUtils.update(c, "insert into t_pool values (1, 'uncommitted')");
		ExecutorService executor = Executors.newSingleThreadExecutor();
		try {
			Future<?> closing = executor.submit(() -> {
				pool.close();
				return null;
			});
			long deadline = System.currentTimeMillis() + 5000;
			while (pool.getObjectPool().isRunning() && System.currentTimeMillis() < deadline) {
				Thread.sleep(5);
			}
			assertFalse(pool.getObjectPool().isRunning());
			assertThrows(SQLException.class, () -> pool.take(), "no connection can be taken while closing");
			assertFalse(closing.isDone(), "close waits for the borrowed connection");
			assertFalse(real.isClosed());
			c.close();
			closing.get(5, TimeUnit.SECONDS);
		} finally {
			executor.shutdownNow();
		}
		assertTrue(real.isClosed());
		assertEquals(0, pool.getObjectPool().getPoolSize());
		assertEquals(0L, JdbcUtils.rowCount(keeper, "select * from t_pool"), "the uncommitted work is rolled back");
	}

	@Test
	public void close_forciblyClosesUnreturnedConnectionAfterTimeout() throws SQLException {
		pool.setCloseTimeout(50);
		Connection c = pool.take();
		Connection real = real(c);
		PreparedStatement ps = c.prepareStatement("select 1");
		PreparedStatement realPs = ps.unwrap(PreparedStatement.class);
		pool.close();
		assertTrue(real.isClosed());
		assertTrue(realPs.isClosed(), "statements of a forcibly closed connection are closed");
		assertTrue(c.isClosed());
		assertThrows(SQLException.class, () -> c.createStatement());
		c.close();
		assertEquals(0, pool.getObjectPool().getPoolSize());
	}

	@Test
	public void concurrentCloseOfOneProxy_givesBackOnlyOnce() throws Exception {
		pool.setMaxSize(1);
		pool.setMaxWaitTime(5000);
		ExecutorService executor = Executors.newFixedThreadPool(3);
		try {
			for (int i = 0; i < 200; i++) {
				final Connection c = pool.take();
				final CountDownLatch start = new CountDownLatch(1);
				Future<?> closer1 = executor.submit(() -> {
					start.await();
					c.close();
					return null;
				});
				Future<?> closer2 = executor.submit(() -> {
					start.await();
					c.close();
					return null;
				});
				Future<Connection> taker = executor.submit(() -> {
					start.await();
					Connection next = pool.take();
					next.setAutoCommit(false);
					return next;
				});
				start.countDown();
				closer1.get(5, TimeUnit.SECONDS);
				closer2.get(5, TimeUnit.SECONDS);
				Connection next = taker.get(5, TimeUnit.SECONDS);
				assertFalse(next.isClosed(), "the new borrowing must not be invalidated by a stale close");
				assertFalse(next.getAutoCommit(), "the new borrowing must not be reset by a stale close");
				assertEquals(1, pool.getObjectPool().getBusySize());
				assertEquals(0, pool.getObjectPool().getIdleSize());
				next.close();
			}
		} finally {
			executor.shutdownNow();
		}
		assertEquals(1, pool.getObjectPool().getIdleSize());
	}

	@Test
	public void reset_restoresEveryChangedConnectionSetting() throws Exception {
		final List<String> calls = new ArrayList<String>();
		final boolean[] state = { true, false };
		final int[] isolation = { Connection.TRANSACTION_READ_COMMITTED };
		Connection recording = (Connection) java.lang.reflect.Proxy.newProxyInstance(getClass().getClassLoader(),
				new Class<?>[] { Connection.class }, (proxy, method, args) -> {
					String name = method.getName();
					switch (name) {
					case "getAutoCommit":
						return state[0];
					case "isReadOnly":
						return state[1];
					case "getTransactionIsolation":
						return isolation[0];
					case "setAutoCommit":
						state[0] = (Boolean) args[0];
						break;
					case "setReadOnly":
						state[1] = (Boolean) args[0];
						break;
					case "setTransactionIsolation":
						isolation[0] = (Integer) args[0];
						break;
					case "isClosed":
						return false;
					default:
						break;
					}
					calls.add(name + (args != null ? java.util.Arrays.toString(args) : ""));
					return null;
				});
		PooledConnection pc = new PooledConnection(recording, 0, null, pool);
		state[0] = false;
		state[1] = true;
		isolation[0] = Connection.TRANSACTION_SERIALIZABLE;
		calls.clear();
		pc.reset();
		assertEquals(java.util.Arrays.asList("rollback", "setAutoCommit[true]", "setReadOnly[false]",
				"setTransactionIsolation[" + Connection.TRANSACTION_READ_COMMITTED + "]", "clearWarnings"), calls);
		calls.clear();
		pc.reset();
		assertEquals(java.util.Arrays.asList("clearWarnings"), calls, "nothing is touched when the state is unchanged");
	}

	@Test
	public void closedPreparedStatement_isNotPutBackIntoTheCache() throws SQLException {
		Connection c = pool.take();
		PreparedStatement ps = c.prepareStatement("select 1");
		PreparedStatementCache cache = ((PooledConnection) java.lang.reflect.Proxy.getInvocationHandler(c)).getStatementCache();
		ps.unwrap(PreparedStatement.class).close();
		ps.close();
		assertEquals(0, cache.size(), "a physically closed statement must not be cached");
		assertEquals(0, cache.leasedSize());
		PreparedStatement fresh = c.prepareStatement("select 1");
		assertFalse(fresh.unwrap(PreparedStatement.class).isClosed());
		fresh.close();
		assertEquals(1, cache.size());
		c.close();
	}

	@Test
	public void interruptedRunningOperation_discardsTheConnection() throws Exception {
		final CountDownLatch entered = new CountDownLatch(1);
		final CountDownLatch release = new CountDownLatch(1);
		final AtomicInteger closed = new AtomicInteger();
		Connection blocking = (Connection) java.lang.reflect.Proxy.newProxyInstance(getClass().getClassLoader(),
				new Class<?>[] { Connection.class }, (proxy, method, args) -> {
					switch (method.getName()) {
					case "getAutoCommit":
						return true;
					case "isReadOnly":
					case "isClosed":
						return false;
					case "getTransactionIsolation":
						return Connection.TRANSACTION_READ_COMMITTED;
					case "getCatalog":
						entered.countDown();
						release.await(5, TimeUnit.SECONDS);
						return "C";
					case "close":
						closed.incrementAndGet();
						return null;
					default:
						return null;
					}
				});
		ExecutorService executor = Executors.newSingleThreadExecutor();
		try {
			PooledConnection pc = new PooledConnection(blocking, 0, executor, pool);
			Connection c = pc.lease();
			final Thread caller = Thread.currentThread();
			Thread interrupter = new Thread(() -> {
				try {
					if (entered.await(5, TimeUnit.SECONDS)) {
						caller.interrupt();
					}
				} catch (InterruptedException ignored) {
				}
			});
			interrupter.start();
			SQLException e = assertThrows(SQLException.class, () -> c.getCatalog());
			assertTrue(Thread.interrupted());
			assertTrue(e.getCause() instanceof InterruptedException);
			interrupter.join(5000);
			assertFalse(pc.isValid(), "a connection whose operation was interrupted while running is discarded");
			assertTrue(c.isClosed());
			assertEquals(1, closed.get(), "the physical connection is closed");
		} finally {
			release.countDown();
			executor.shutdownNow();
		}
	}

	@Test
	public void manyPlainStatementsInOneBorrowing_closedOnesAreForgotten() throws SQLException {
		pool.setExecutor(null);
		pool.close();
		pool = newPool();
		pool.setExecutor(null);
		Connection c = pool.take();
		List<Statement> open = new ArrayList<Statement>();
		for (int i = 0; i < 40; i++) {
			Statement s = c.createStatement();
			if (i % 4 == 0) {
				open.add(s);
			} else {
				s.close();
			}
		}
		c.close();
		for (Statement s : open) {
			assertTrue(s.isClosed());
		}
	}

	@Test
	public void statementCache_replacesDuplicateAndRecreatesStaleStatements() throws SQLException {
		Connection c = pool.take();
		PreparedStatementCache cache = ((PooledConnection) java.lang.reflect.Proxy.getInvocationHandler(c)).getStatementCache();
		PreparedStatement first = c.prepareStatement("select 1");
		PreparedStatement second = c.prepareStatement("select 1");
		PreparedStatement realFirst = first.unwrap(PreparedStatement.class);
		PreparedStatement realSecond = second.unwrap(PreparedStatement.class);
		first.close();
		second.close();
		assertEquals(1, cache.size(), "only one statement per sql is cached");
		assertTrue(realFirst.isClosed(), "the replaced statement is closed");
		assertFalse(realSecond.isClosed());
		// a cached statement closed behind the pool's back is recreated on the next take
		realSecond.close();
		PreparedStatement third = c.prepareStatement("select 1");
		PreparedStatement realThird = third.unwrap(PreparedStatement.class);
		assertNotSame(realSecond, realThird);
		assertFalse(realThird.isClosed());
		third.setFetchDirection(ResultSet.FETCH_FORWARD);
		third.setLargeMaxRows(1L);
		third.close();
		PreparedStatement fourth = c.prepareStatement("select 1");
		assertSame(realThird, fourth.unwrap(PreparedStatement.class));
		assertEquals(0L, fourth.getLargeMaxRows());
		fourth.close();
		c.close();
	}

	@Test
	public void setExecutor_shutsDownTheDefaultExecutorItReplaces() {
		ConnectionObjectFactory factory = pool.getConnectionFactory();
		ExecutorService defaultExecutor = factory.getExecutor();
		assertNotNull(defaultExecutor);
		ExecutorService custom = Executors.newSingleThreadExecutor();
		try {
			factory.setExecutor(custom);
			assertTrue(defaultExecutor.isShutdown(), "the replaced default executor must not leak its threads");
			assertSame(custom, factory.getExecutor());
			factory.setExecutor(null);
			assertFalse(custom.isShutdown(), "a user supplied executor is not shut down when replaced");
			assertNull(factory.getExecutor());
		} finally {
			custom.shutdownNow();
		}
	}
}
