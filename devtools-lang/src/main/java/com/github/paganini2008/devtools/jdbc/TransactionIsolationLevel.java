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

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * TransactionIsolationLevel
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public enum TransactionIsolationLevel {
	
	NONE(Connection.TRANSACTION_NONE), 
	READ_COMMITTED(Connection.TRANSACTION_READ_COMMITTED), 
	READ_UNCOMMITTED(Connection.TRANSACTION_READ_UNCOMMITTED), 
	REPEATABLE_READ(Connection.TRANSACTION_REPEATABLE_READ), 
	SERIALIZABLE(Connection.TRANSACTION_SERIALIZABLE);

	private final int level;

	private TransactionIsolationLevel(int level) {
		this.level = level;
	}

	public int getLevel() {
		return level;
	}

	private static final Map<Integer, TransactionIsolationLevel> lookup = new HashMap<Integer, TransactionIsolationLevel>();

	static {
		for (TransactionIsolationLevel level : TransactionIsolationLevel.values()) {
			lookup.put(level.getLevel(), level);
		}
	}

	public static TransactionIsolationLevel get(int level) {
		if (!lookup.containsKey(level)) {
			throw new IllegalArgumentException("Unknown transaction isolation level: " + level);
		}
		return lookup.get(level);
	}
}
