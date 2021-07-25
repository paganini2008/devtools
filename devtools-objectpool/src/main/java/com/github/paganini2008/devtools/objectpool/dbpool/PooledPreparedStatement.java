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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.github.paganini2008.devtools.jdbc.JdbcUtils;
import com.github.paganini2008.devtools.logging.Log;
import com.github.paganini2008.devtools.logging.LogFactory;

/**
 * Pooled PreparedStatement object
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class PooledPreparedStatement implements InvocationHandler {

	private static final Log logger = LogFactory.getLog(PooledPreparedStatement.class);

	private static final String CLOSE_METHOD = "close";
	private static final Class<?>[] IFACES = new Class<?>[] { PreparedStatement.class };

	public PooledPreparedStatement(String sql, PreparedStatementCache statementCache, PreparedStatement ps) {
		this.sql = sql;
		this.statementCache = statementCache;
		this.realStatement = ps;
		this.proxyStatement = (PreparedStatement) Proxy.newProxyInstance(getClass().getClassLoader(), IFACES, this);
	}

	private final String sql;
	private final PreparedStatementCache statementCache;
	private PreparedStatement realStatement;
	private PreparedStatement proxyStatement;
	private DailyQueryStatistics queryStatistics;

	public void setQueryStatistics(DailyQueryStatistics queryStatistics) {
		this.queryStatistics = queryStatistics;
	}

	public PreparedStatement getRealStatement() {
		return realStatement;
	}

	public PreparedStatement getProxyStatement() {
		return proxyStatement;
	}

	public boolean isOpened() {
		try {
			return !realStatement.isClosed();
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
	}

	public void close() {
		JdbcUtils.closeQuietly(realStatement);
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		String methodName = method.getName();
		if (methodName.equals("equals")) {
			return (realStatement == args[0]);
		} else if (methodName.equals("hashCode")) {
			return System.identityHashCode(realStatement);
		} else if (methodName.equals("toString")) {
			return realStatement.toString();
		} else if (CLOSE_METHOD.equals(methodName)) {
			statementCache.giveback(sql, this);
			return null;
		} else if (methodName.startsWith("set") && args[0] instanceof Integer) {
			if (logger.isDebugEnabled()) {
				if (args.length >= 2) {
					int parameterIndex = (Integer) args[0];
					Object parameter = args[1];
					logger.debug("[{}] Invoke: {}, Parameter: {}",
							new Object[] { parameterIndex, methodName, parameter });
				}
			}
			return method.invoke(realStatement, args);
		} else {
			if (queryStatistics != null) {
				if (methodName.startsWith("executeQuery")) {
					long start = System.currentTimeMillis();
					Object result = method.invoke(realStatement, args);
					if (queryStatistics != null) {
						queryStatistics.executed(sql, args, start, System.currentTimeMillis());
					}
					return result;
				}
			}
			return method.invoke(realStatement, args);
		}
	}
}
