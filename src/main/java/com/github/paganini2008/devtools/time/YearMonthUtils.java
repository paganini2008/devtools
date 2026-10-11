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
package com.github.paganini2008.devtools.time;

import java.time.Month;
import java.time.Year;
import java.time.YearMonth;

import com.github.paganini2008.devtools.Assert;

/**
 * 
 * Utility methods for {@link YearMonth}.
 *
 * @author Fred Feng
 * @date 2021-11-23
 *
 * @since 2.0.4
 */
public abstract class YearMonthUtils {

	/**
	 * Combines a year and a month into a {@link YearMonth}.
	 *
	 * @throws IllegalArgumentException if any argument is null or the year is out of range
	 */
	public static YearMonth toYearMonth(Year year, Month month) {
		Assert.isNull(year, "Year must not be null.");
		Assert.isNull(month, "Month must not be null.");
		TimeAssert.validateYear(year.getValue());
		return year.atMonth(month);
	}

	/**
	 * Obtains a {@link YearMonth} after validating the year and the month (1-12).
	 *
	 * @throws IllegalArgumentException if the year or the month is out of range
	 */
	public static YearMonth of(int year, int month) {
		TimeAssert.validateYear(year);
		TimeAssert.validateMonth(month);
		return YearMonth.of(year, month);
	}

}
