/**
* Copyright 2018-2021 Fred Feng (paganini.fy@gmail.com)

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

import java.io.Serializable;
import java.util.Arrays;

import com.github.paganini2008.devtools.date.Duration;

/**
 * QueryTraceImpl
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class QueryTraceImpl implements QueryTrace, Serializable {

	private static final long serialVersionUID = 5264579507686000051L;
	private final String sql;
	private final Object[] parameters;
	private final long startTime;
	private final long endTime;

	public QueryTraceImpl(String sql, Object[] parameters, long startTime, long endTime) {
		this.sql = sql;
		this.parameters = parameters;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public Object[] getParameters() {
		return parameters;
	}

	public String getSql() {
		return sql;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public String toString() {
		return "[QueryTrace] sql: " + sql + ", parameters: " + Arrays.toString(parameters) + ", elapsed: "
				+ Duration.MINUTE.format(endTime - startTime);
	}

}
