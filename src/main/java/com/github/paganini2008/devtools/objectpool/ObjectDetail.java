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
package com.github.paganini2008.devtools.objectpool;

/**
 * 
 * ObjectDetail exposes the usage information of a pooled object.
 * 
 * @author Fred Feng
 * @date 2019-07-12
 * @since 2.0.1
 */
public interface ObjectDetail {

	/**
	 * Returns the creation time in milliseconds.
	 */
	long getCreated();

	/**
	 * Returns the pooled object.
	 */
	Object getObject();

	/**
	 * Returns the time of the last borrowing in milliseconds.
	 */
	long getLastBorrowed();

	/**
	 * Returns the time of the last giving back in milliseconds, 0 if never.
	 */
	long getLastReturned();

	/**
	 * Returns the time of the last successful validation in milliseconds.
	 */
	long getLastTested();

	/**
	 * Returns how many times the object has been borrowed.
	 */
	int getUsage();

}