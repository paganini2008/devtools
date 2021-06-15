/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

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
 * ObjectPool
 *
 * @author Fred Feng
 * @version 1.0
 */
public interface ObjectPool {

	/**
	 * Get the pooled object's detail
	 * 
	 * @param object
	 * @return
	 */
	ObjectDetail getDetail(Object object);

	/**
	 * Borrow one from pool until it's available.
	 * 
	 * @return
	 * @throws Exception
	 */
	Object borrowObject() throws Exception;

	/**
	 * Borrow one from pool within given time(ms).
	 * 
	 * @param timeout
	 * @return
	 * @throws Exception
	 */
	Object borrowObject(long timeout) throws Exception;

	/**
	 * Give back the pooled object to pool.
	 * 
	 * @param object
	 * @throws Exception
	 */
	void givebackObject(Object object) throws Exception;

	/**
	 * Discard this one when it was invalid or expired.
	 * 
	 * @param object
	 * @throws Exception
	 */
	void discardObject(Object object) throws Exception;

	/**
	 * Close the pool.
	 * 
	 * @throws Exception
	 */
	void close() throws Exception;

}
