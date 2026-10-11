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
package com.github.paganini2008.devtools.enums;

/**
 * 
 * EnumConstant
 * 
 * An enum constant with an integer value, a display representation and an optional group.
 *
 * @author Fred Feng
 * @date 2019-08-06
 * 
 * 
 * @since 2.0.1
 */
public interface EnumConstant {

	static final String DEFAULT_GROUP = "DEFAULT";

	/**
	 * Returns the integer value of the constant.
	 */
	int getValue();

	/**
	 * Returns the display representation of the constant.
	 */
	String getRepr();

	/**
	 * Returns the group of the constant; {@link #DEFAULT_GROUP} by default.
	 */
	default String getGroup() {
		return DEFAULT_GROUP;
	}
}
