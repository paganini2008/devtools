/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

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

import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

/**
 * 
 * UnpooledDataSourceFactory
 *
 * @author Fred Feng
 *
 * @since 2.0.4
 */
public class UnpooledDataSourceFactory implements DataSourceFactory {

	protected String driverClassName;
	protected String jdbcUrl;
	protected String user;
	protected String password;
	protected int maximumConnectionSize = 16;

	private DataSource dataSource;

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getMaximumConnectionSize() {
		return maximumConnectionSize;
	}

	public void setMaximumConnectionSize(int maximumConnectionSize) {
		this.maximumConnectionSize = maximumConnectionSize;
	}

	@Override
	public void setProperties(Properties config) throws SQLException {
	}

	@Override
	public DataSource getDataSource() throws SQLException {
		if (dataSource == null) {
			dataSource = new UnpooledDataSource(driverClassName, jdbcUrl, user, password, maximumConnectionSize);
		}
		return dataSource;
	}

}
