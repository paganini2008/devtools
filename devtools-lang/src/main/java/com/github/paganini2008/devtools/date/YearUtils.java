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
package com.github.paganini2008.devtools.date;

import java.time.Year;

/**
 * 
 * TimeAssert
 *
 * @author Fred Feng
 *
 * @since 2.0.4
 */
public abstract class YearUtils {

	public static final Year MIN_YEAR = Year.of(DateUtils.MIN_YEAR);
	public static final Year MAX_YEAR = Year.of(DateUtils.MAX_YEAR);

	public static Year of(int year) {
		TimeAssert.validateYear(year);
		return Year.of(year);
	}

}
