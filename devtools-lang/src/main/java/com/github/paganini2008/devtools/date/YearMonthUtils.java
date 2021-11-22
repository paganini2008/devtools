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

import static com.github.paganini2008.devtools.date.DateUtils.YEAR_END_WITH;
import static com.github.paganini2008.devtools.date.DateUtils.YEAR_START_FROM;

import java.time.Month;
import java.time.Year;
import java.time.YearMonth;

/**
 * 
 * YearMonthUtils
 *
 * @author Fred Feng
 *
 * @since 2.0.4
 */
public abstract class YearMonthUtils {

	public static final Year YEAR_END = Year.of(YEAR_END_WITH);
	public static final Year YEAR_START = Year.of(YEAR_START_FROM);

	public static YearMonth toYearMonth(Year year, Month month) {
		YearMonth yearMonth = YearMonth.now();
		if (year != null && month != null) {
			yearMonth = year.atMonth(month);
		} else if (year != null && month == null) {
			yearMonth = yearMonth.with(year);
		} else if (year == null && month != null) {
			yearMonth = yearMonth.with(month);
		}
		return yearMonth;
	}

	public static YearMonth of(int year, int month) {
		TimeAsserts.validateYear(year);
		TimeAsserts.validateMonth(month);
		return YearMonth.of(year, month);
	}

}
