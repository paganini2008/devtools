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
package com.github.paganini2008.devtools.jdbc;

import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * DriverManagerDataSource
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface DriverManagerDataSource extends DataSource {

	/**
	 * Set username
	 * 
	 * @param user
	 */
	void setUser(String user);

	/**
	 * Set password
	 * 
	 * @param password
	 */
	void setPassword(String password);

	/**
	 * Set jdbc url
	 * 
	 * @param url
	 */
	void setJdbcUrl(String jdbcUrl);

	/**
	 * Set DriverClass Name
	 * 
	 * @param driverClassName
	 */
	void setDriverClassName(String driverClassName) throws SQLException;

	/**
	 * Close the DataSource
	 */
	void close() throws SQLException;

}
