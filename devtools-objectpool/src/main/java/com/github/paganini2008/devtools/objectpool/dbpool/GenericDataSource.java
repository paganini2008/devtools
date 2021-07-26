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
package com.github.paganini2008.devtools.objectpool.dbpool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import com.github.paganini2008.devtools.jdbc.AbstractDataSource;

/**
 * 
 * GenericDataSource
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public class GenericDataSource extends AbstractDataSource {

	public GenericDataSource(String driverClassName, String url, String username, String password) throws SQLException {
		this.setDriverClassName(driverClassName);
		this.setJdbcUrl(url);
		this.setUser(username);
		this.setPassword(password);
	}

	public GenericDataSource() {
	}

	private final ConnectionPool connectionPool = new ConnectionPool();

	public void setUser(String username) {
		this.connectionPool.setUser(username);
	}

	public void setPassword(String password) {
		this.connectionPool.setPassword(password);
	}

	public void setDriverClassName(String driverClassName) {
		this.connectionPool.setDriverClassName(driverClassName);
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.connectionPool.setJdbcUrl(jdbcUrl);
	}

	public void setTestSql(String testSql) {
		this.connectionPool.setTestSql(testSql);
	}

	public void setAutoCommit(Boolean autoCommit) {
		this.connectionPool.setAutoCommit(autoCommit);
	}

	public void setDefaultTransactionIsolationLevel(Integer defaultTransactionIsolationLevel) {
		this.connectionPool.setDefaultTransactionIsolationLevel(defaultTransactionIsolationLevel);
	}

	public void setMaxIdleSize(int maxIdleSize) {
		this.connectionPool.setMaxIdleSize(maxIdleSize);
	}

	public void setMaxSize(int maxSize) {
		this.connectionPool.setMaxSize(maxSize);
	}

	public void setMaxUsage(int usage) {
		this.connectionPool.setMaxUsage(usage);
	}

	public void setTestWhileIdle(boolean testWhileIdle) {
		this.connectionPool.setTestWhileIdle(testWhileIdle);
	}

	public void setTestWhileIdleInterval(long testWhileIdleInterval) {
		this.connectionPool.setTestWhileIdleInterval(testWhileIdleInterval);
	}

	public void setCheckObjectExpired(boolean checkObjectExpired) {
		this.connectionPool.setCheckObjectExpired(checkObjectExpired);
	}

	public void setCheckObjectExpiredInterval(long checkObjectExpiredInterval) {
		this.connectionPool.setCheckObjectExpiredInterval(checkObjectExpiredInterval);
	}

	public void setMaxWaitTimeForExpiration(long maxWaitTimeForExpiration) {
		this.connectionPool.setMaxWaitTimeForExpiration(maxWaitTimeForExpiration);
	}

	public void setAcceptableExecutionTime(long acceptableExecutionTime) {
		this.connectionPool.getQueryStatistics().setAcceptableExecutionTime(acceptableExecutionTime);
	}

	public void setStatisticalSampleCount(int statisticalSampleCount) {
		this.connectionPool.getQueryStatistics().setStatisticalSampleCount(statisticalSampleCount);
	}

	public Map<String, QuerySpan> getStatisticsResult(String daily) {
		return connectionPool.getStatisticsResult(daily);
	}

	public Connection getConnection() throws SQLException {
		return connectionPool.take();
	}

	public void close() throws SQLException {
		connectionPool.close();
	}

}
