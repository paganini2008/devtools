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

/**
 * 
 * PageableSql builds the count statement and the paging statement of a query.
 *
 * @author Fred Feng
 * @date 2020-02-02
 * @since 2.0.1
 */
public interface PageableSql {

	/**
	 * Returns the statement counting the records of the query.
	 */
	String countableSql();

	/**
	 * Returns the statement fetching a range of the query.
	 * 
	 * @param maxResults the max count of records, non-positive means no limit
	 * @param firstResult the 0-based offset of the first record
	 */
	String pageableSql(int maxResults, int firstResult);

}
