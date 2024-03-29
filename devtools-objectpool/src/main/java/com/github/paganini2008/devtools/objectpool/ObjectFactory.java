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
package com.github.paganini2008.devtools.objectpool;

/**
 * Customized ObjectFactory interface to manage some reused objects.
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public interface ObjectFactory {

	/**
	 * Create object if unavaliable
	 * 
	 * @return
	 * @throws Exception
	 */
	Object createObject() throws Exception;

	/**
	 * Verify the pooled object when it is idle or borrowed.
	 * 
	 * @param object
	 * @return
	 * @throws Exception
	 */
	default boolean testObject(Object object) throws Exception {
		return true;
	}

	/**
	 * Release the pooled object and remove it from the pool.
	 * 
	 * @param object
	 * @throws Exception
	 */
	void destroyObject(Object object) throws Exception;

}
