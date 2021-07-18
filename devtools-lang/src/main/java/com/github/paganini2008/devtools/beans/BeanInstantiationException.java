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
package com.github.paganini2008.devtools.beans;

/**
 * 
 * BeanInstantiationException
 * 
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public class BeanInstantiationException extends RuntimeException {

	private static final long serialVersionUID = -6165101337845158099L;

	public BeanInstantiationException(String msg) {
		super(msg);
	}

	public BeanInstantiationException(String msg, Throwable e) {
		super(msg, e);
	}

	public BeanInstantiationException(Throwable e) {
		super(e);
	}

}
