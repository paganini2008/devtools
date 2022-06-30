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
package com.github.paganini2008.devtools.db4j.mapper;

/**
 * RowMapperException
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public class RowMapperException extends RuntimeException {

	private static final long serialVersionUID = -6460344809438661419L;

	public RowMapperException(String msg) {
		super(msg);
	}

	public RowMapperException(Throwable e) {
		super(e);
	}

	public RowMapperException(String msg, Throwable e) {
		super(msg, e);
	}
}
