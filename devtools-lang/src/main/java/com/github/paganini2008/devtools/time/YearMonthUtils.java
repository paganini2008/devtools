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
package com.github.paganini2008.devtools.time;

import java.time.Month;
import java.time.Year;
import java.time.YearMonth;

import com.github.paganini2008.devtools.Assert;

/**
 * 
 * YearMonthUtils
 *
 * @author Fred Feng
 *
 * @since 2.0.4
 */
public abstract class YearMonthUtils {

	public static YearMonth toYearMonth(Year year, Month month) {
		Assert.outOfRange(year, YearUtils.MIN_YEAR, YearUtils.MAX_YEAR, "Out of year range. Input: " + year);
		return year.atMonth(month);
	}

	public static YearMonth of(int year, int month) {
		TimeAssert.validateYear(year);
		TimeAssert.validateMonth(month);
		return YearMonth.of(year, month);
	}

}
