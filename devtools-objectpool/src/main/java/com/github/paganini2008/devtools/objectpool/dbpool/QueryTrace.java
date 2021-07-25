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

/**
 * Record each query info(Sql,SqlParameters,StartTime and EndTime)
 * 
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public interface QueryTrace extends Comparable<QueryTrace> {

	Object[] getParameters();

	String getSql();

	long getStartTime();

	long getEndTime();

	default int compareTo(QueryTrace qt) {
		long result = qt.getEndTime() - qt.getStartTime();
		if (result > 0) {
			return -1;
		}
		if (result < 0) {
			return 1;
		}
		return 0;
	}

}
