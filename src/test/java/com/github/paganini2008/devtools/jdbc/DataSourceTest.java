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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * DataSourceTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
@Timeout(20)
public class DataSourceTest {

	private static final String DRIVER = "org.h2.Driver";
	private JdbcTracker tracker;

	@BeforeEach
	public void setUp() throws SQLException {
		tracker = new JdbcTracker("jdbc_datasource_test");
		tracker.execute("create table t_item(id int primary key, name varchar(20))", "create schema if not exists other");
	}

	@AfterEach
	public void tearDown() throws SQLException {
		tracker.drop();
	}

	@Test
	public void unpooledDataSource_limitsConcurrentConnections() throws Exception {
		UnpooledDataSource dataSource = new UnpooledDataSource(DRIVER, tracker.getUrl(), "sa", "", 2);
		dataSource.setTimeout(1);
		Connection c1 = dataSource.getConnection();
		Connection c2 = dataSource.getConnection();
		SQLException e = assertThrows(SQLException.class, () -> dataSource.getConnection());
		assertTrue(e.getMessage().contains("timeout"));
		c1.close();
		c1.close();
		Connection c3 = dataSource.getConnection();
		SQLException e2 = assertThrows(SQLException.class, () -> dataSource.getConnection(),
				"closing a connection twice must release only one permit");
		assertTrue(e2.getMessage().contains("timeout"));
		c2.close();
		c3.close();
		assertTrue(c1.isClosed());
	}

	@Test
	public void unpooledDataSource_proxyBehaviour() throws SQLException {
		UnpooledDataSource dataSource = new UnpooledDataSource(DRIVER, tracker.getUrl(), "sa", "", 4);
		dataSource.setAutoCommit(false);
		dataSource.setTransactionIsolationLevel(TransactionIsolationLevel.SERIALIZABLE);
		assertEquals(Boolean.FALSE, dataSource.getAutoCommit());
		assertEquals(TransactionIsolationLevel.SERIALIZABLE, dataSource.getTransactionIsolationLevel());
		assertEquals(DRIVER, dataSource.getDriverClassName());
		assertEquals(tracker.getUrl(), dataSource.getJdbcUrl());
		assertEquals("sa", dataSource.getUser());
		assertEquals("", dataSource.getPassword());
		Connection c = dataSource.getConnection();
		Connection other = dataSource.getConnection();
		assertFalse(c.getAutoCommit());
		assertEquals(Connection.TRANSACTION_SERIALIZABLE, c.getTransactionIsolation());
		assertTrue(c.equals(c));
		assertFalse(c.equals(other));
		assertEquals(System.identityHashCode(c), c.hashCode());
		assertTrue(c.toString().length() > 0);
		assertThrows(SQLSyntaxErrorException.class, () -> c.prepareStatement("select * from no_table"),
				"the original SQLException must be thrown, not an UndeclaredThrowableException");
		c.close();
		other.close();
		assertTrue(c.isClosed());
	}

	@Test
	public void unpooledDataSource_failedConnectReleasesPermit() throws SQLException {
		UnpooledDataSource dataSource = new UnpooledDataSource(DRIVER, "jdbc:h2:mem:jdbc_datasource_test;IFEXISTS=TRUE;BAD_SETTING=1", "sa",
				"", 1);
		dataSource.setTimeout(0);
		assertThrows(SQLException.class, () -> dataSource.getConnection());
		assertThrows(SQLException.class, () -> dataSource.getConnection(), "permit must be released after a failed connect");
	}

	@Test
	public void unpooledDataSource_interruptedWhileWaiting() throws Exception {
		UnpooledDataSource dataSource = new UnpooledDataSource(DRIVER, tracker.getUrl(), "sa", "", 1);
		dataSource.setTimeout(0);
		Connection c = dataSource.getConnection();
		CountDownLatch started = new CountDownLatch(1);
		ExecutorService executor = Executors.newSingleThreadExecutor();
		try {
			Future<Throwable> future = executor.submit(() -> {
				started.countDown();
				try {
					dataSource.getConnection().close();
					return null;
				} catch (SQLException e) {
					return e;
				}
			});
			started.await();
			Thread.sleep(50);
			executor.shutdownNow();
			Throwable result = future.get(5, TimeUnit.SECONDS);
			assertTrue(result instanceof SQLException);
			assertTrue(result.getMessage().contains("Interrupted"));
		} finally {
			c.close();
			executor.shutdownNow();
		}
		dataSource.setTimeout(1);
		dataSource.getConnection().close();
	}

	@Test
	public void unpooledDataSource_rejectsSettersAndBadDriver() {
		UnpooledDataSource dataSource = new UnpooledDataSource(DRIVER, tracker.getUrl(), "sa", "", 1);
		assertThrows(UnsupportedOperationException.class, () -> dataSource.setUser("x"));
		assertThrows(UnsupportedOperationException.class, () -> dataSource.setPassword("x"));
		assertThrows(UnsupportedOperationException.class, () -> dataSource.setJdbcUrl("x"));
		assertThrows(UnsupportedOperationException.class, () -> dataSource.setDriverClassName("x"));
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
				() -> new UnpooledDataSource("no.such.Driver", tracker.getUrl(), "sa", "", 1));
		assertTrue(e.getMessage().contains("no.such.Driver"));
	}

	@Test
	public void abstractDataSource_wrapperAndUnsupportedOperations() throws SQLException {
		UnpooledDataSource dataSource = new UnpooledDataSource(DRIVER, tracker.getUrl(), "sa", "", 1);
		assertTrue(dataSource.isWrapperFor(DataSource.class));
		assertTrue(dataSource.isWrapperFor(UnpooledDataSource.class));
		assertFalse(dataSource.isWrapperFor(String.class));
		assertFalse(dataSource.isWrapperFor(null));
		assertSame(dataSource, dataSource.unwrap(DataSource.class));
		assertSame(dataSource, dataSource.unwrap(DriverManagerDataSource.class));
		assertThrows(SQLException.class, () -> dataSource.unwrap(String.class));
		assertThrows(IllegalArgumentException.class, () -> dataSource.unwrap(null));
		assertThrows(UnsupportedOperationException.class, () -> dataSource.getLoginTimeout());
		assertThrows(UnsupportedOperationException.class, () -> dataSource.setLoginTimeout(1));
		assertThrows(UnsupportedOperationException.class, () -> dataSource.getLogWriter());
		assertThrows(UnsupportedOperationException.class, () -> dataSource.setLogWriter(null));
		assertThrows(UnsupportedOperationException.class, () -> dataSource.getConnection("a", "b"));
		assertEquals(java.util.logging.Logger.GLOBAL_LOGGER_NAME, dataSource.getParentLogger().getName());
		dataSource.close();
	}

	@Test
	public void unpooledDataSourceFactory_createsDataSourceOnce() throws SQLException {
		UnpooledDataSourceFactory factory = new UnpooledDataSourceFactory();
		factory.setDriverClassName(DRIVER);
		factory.setJdbcUrl(tracker.getUrl());
		factory.setUser("sa");
		factory.setPassword("");
		factory.setMaximumConnectionSize(3);
		factory.setProperties(new Properties());
		assertEquals(DRIVER, factory.getDriverClassName());
		assertEquals(tracker.getUrl(), factory.getJdbcUrl());
		assertEquals("sa", factory.getUser());
		assertEquals("", factory.getPassword());
		assertEquals(3, factory.getMaximumConnectionSize());
		DataSource dataSource = factory.getDataSource();
		assertSame(dataSource, factory.getDataSource());
		try (Connection c = dataSource.getConnection()) {
			assertFalse(c.isClosed());
		}
		SingletonDataSoruceFactory singleton = new SingletonDataSoruceFactory(dataSource);
		singleton.setProperties(new Properties());
		assertSame(dataSource, singleton.getDataSource());
	}

	@Test
	public void unpooledConnectionFactory_configuresAndRestoresPath() throws SQLException {
		UnpooledConnectionFactory factory = new UnpooledConnectionFactory(DRIVER, tracker.getUrl(), "sa", "");
		factory.setAutoCommit(false);
		factory.setTransactionIsolationLevel(TransactionIsolationLevel.READ_COMMITTED);
		factory.setSchema("PUBLIC");
		assertEquals(DRIVER, factory.getDriverClassName());
		assertEquals(tracker.getUrl(), factory.getJdbcUrl());
		assertEquals("sa", factory.getUser());
		assertEquals("", factory.getPassword());
		assertEquals(Boolean.FALSE, factory.getAutoCommit());
		assertEquals(TransactionIsolationLevel.READ_COMMITTED, factory.getTransactionIsolationLevel());
		assertNull(factory.getCatalog());
		assertEquals("PUBLIC", factory.getSchema());
		Connection c = factory.getConnection(null, "OTHER");
		assertFalse(c.getAutoCommit());
		assertEquals("OTHER", c.getSchema());
		factory.close(c);
		assertTrue(c.isClosed());
		factory.close(c);
		factory.close(null);
		factory.setCatalog("JDBC_DATASOURCE_TEST");
		assertEquals("JDBC_DATASOURCE_TEST", factory.getCatalog());
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> factory.setDriverClassName("no.Driver"));
		assertTrue(e.getCause() instanceof ClassNotFoundException);
	}

	@Test
	public void unpooledConnectionFactory_closesConnectionWhenConfigurationFails() throws SQLException {
		UnpooledConnectionFactory factory = new UnpooledConnectionFactory();
		factory.setDriverClassName(DRIVER);
		factory.setJdbcUrl(tracker.getUrl());
		factory.setUser("sa");
		factory.setPassword("");
		factory.setTransactionIsolationLevel(TransactionIsolationLevel.NONE);
		assertThrows(SQLException.class, () -> factory.getConnection());
		factory.setTransactionIsolationLevel(null);
		Connection c = factory.getConnection();
		assertTrue(c.getAutoCommit());
		factory.close(c);
	}

	@Test
	public void unpooledConnectionFactory_closeStillClosesWhenPathFails() throws SQLException {
		UnpooledConnectionFactory factory = new UnpooledConnectionFactory(DRIVER, tracker.getUrl(), "sa", "");
		factory.setSchema("NO_SUCH_SCHEMA");
		Connection c = factory.getConnection();
		assertThrows(SQLException.class, () -> factory.close(c));
		assertTrue(c.isClosed());
	}

	@Test
	public void pooledConnectionFactory_delegatesToDataSource() throws SQLException {
		UnpooledDataSource dataSource = new UnpooledDataSource(DRIVER, tracker.getUrl(), "sa", "", 2);
		PooledConnectionFactory factory = new PooledConnectionFactory(dataSource, null, "PUBLIC");
		Connection c = factory.getConnection("JDBC_DATASOURCE_TEST", "OTHER");
		assertEquals("OTHER", c.getSchema());
		factory.close(c);
		assertTrue(c.isClosed());
		factory.close(c);
		factory.close(null);
		assertEquals(dataSource.toString(), factory.toString());
		PooledConnectionFactory plain = new PooledConnectionFactory(dataSource);
		Connection c2 = plain.getConnection();
		plain.close(c2);
		assertTrue(c2.isClosed());
	}

	@Test
	public void connectionFactory_defaultMethods() throws SQLException {
		ConnectionFactory factory = tracker.connectionFactory();
		Connection c = factory.getConnection(null, "OTHER");
		assertEquals("OTHER", c.getSchema());
		factory.close(c);
		assertTrue(c.isClosed());
		factory.close(null);
	}

	@Test
	public void unpooledDataSource_concurrentBorrowersNeverExceedLimit() throws Exception {
		final UnpooledDataSource dataSource = new UnpooledDataSource(DRIVER, tracker.getUrl(), "sa", "", 3);
		final AtomicInteger active = new AtomicInteger();
		final AtomicInteger maxActive = new AtomicInteger();
		final CountDownLatch start = new CountDownLatch(1);
		ExecutorService executor = Executors.newFixedThreadPool(8);
		List<Future<?>> futures = new ArrayList<Future<?>>();
		for (int i = 0; i < 8; i++) {
			futures.add(executor.submit(() -> {
				start.await();
				for (int j = 0; j < 20; j++) {
					try (Connection c = dataSource.getConnection()) {
						int n = active.incrementAndGet();
						maxActive.accumulateAndGet(n, Math::max);
						JdbcUtils.fetchOne(c, "select 1", Integer.class);
						active.decrementAndGet();
					}
				}
				return null;
			}));
		}
		start.countDown();
		for (Future<?> f : futures) {
			f.get(15, TimeUnit.SECONDS);
		}
		executor.shutdown();
		assertTrue(maxActive.get() <= 3);
		assertNotEquals(0, maxActive.get());
		Connection c1 = dataSource.getConnection();
		Connection c2 = dataSource.getConnection();
		Connection c3 = dataSource.getConnection();
		c1.close();
		c2.close();
		c3.close();
	}
}
