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

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.collection.LruMap;

/**
 * 
 * LocalDateUtils
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public abstract class LocalDateUtils {

	private final static LruMap<String, DateTimeFormatter> dfCache = new LruMap<String, DateTimeFormatter>(16);

	public static LocalDate addDays(Date date, int days) {
		return addDays(toLocalDate(date, null), days);
	}

	public static LocalDate addDays(LocalDate localDate, int days) {
		return localDate.plus(days, ChronoUnit.DAYS);
	}

	public static LocalDate addMonths(Date date, int months) {
		return addMonths(toLocalDate(date, null), months);
	}

	public static LocalDate addMonths(LocalDate localDate, int months) {
		return localDate.plus(months, ChronoUnit.MONTHS);
	}

	public static LocalDate addYears(Date date, int years) {
		return addDays(toLocalDate(date, null), years);
	}

	public static LocalDate addYears(LocalDate localDate, int years) {
		return localDate.plus(years, ChronoUnit.YEARS);
	}

	public static LocalDate addWeeks(Date date, int weeks) {
		return addWeeks(toLocalDate(date, null), weeks);
	}

	public static LocalDate addWeeks(LocalDate localDate, int weeks) {
		return localDate.plus(weeks, ChronoUnit.WEEKS);
	}

	public static LocalDate toLocalDate(Long ms, ZoneId zoneId) {
		return toLocalDate(ms, zoneId, null);
	}

	public static LocalDate toLocalDate(Long ms, ZoneId zoneId, LocalDate defaultValue) {
		if (zoneId == null) {
			zoneId = ZoneId.systemDefault();
		}
		try {
			return ms != null ? Instant.ofEpochMilli(ms).atZone(zoneId).toLocalDate() : defaultValue;
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	public static LocalDate toLocalDate(Date date, ZoneId zoneId) {
		return toLocalDate(date, zoneId, null);
	}

	public static LocalDate toLocalDate(Date date, ZoneId zoneId, LocalDate defaultValue) {
		if (zoneId == null) {
			zoneId = ZoneId.systemDefault();
		}
		try {
			return date != null ? date.toInstant().atZone(zoneId).toLocalDate() : defaultValue;
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	public static LocalDate toLocalDate(Calendar calendar, ZoneId zoneId) {
		return toLocalDate(calendar, zoneId, null);
	}

	public static LocalDate toLocalDate(Calendar calendar, ZoneId zoneId, LocalDate defaultValue) {
		if (zoneId == null) {
			zoneId = ZoneId.systemDefault();
		}
		try {
			return calendar != null ? calendar.toInstant().atZone(zoneId).toLocalDate() : defaultValue;
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	public static String format(LocalDate localDate) {
		return format(localDate, DateTimeFormatter.ISO_LOCAL_DATE);
	}

	public static String format(LocalDate localDate, DateTimeFormatter dateTimeFormatter) {
		return format(localDate, dateTimeFormatter, "");
	}

	public static String format(LocalDate localDate, DateTimeFormatter dateTimeFormatter, String defaultValue) {
		return localDate != null ? localDate.format(dateTimeFormatter) : defaultValue;
	}

	public static String format(LocalDate localDate, String datePattern) {
		return format(localDate, datePattern, "");
	}

	public static String format(LocalDate localDate, String datePattern, String defaultValue) {
		return localDate != null ? localDate.format(getDateTimeFormatter(datePattern)) : defaultValue;
	}

	public static LocalDate parseLocalDate(String text) {
		return parseLocalDate(text, DateTimeFormatter.ISO_LOCAL_DATE);
	}

	public static LocalDate parseLocalDate(String text, DateTimeFormatter formatter) {
		return parseLocalDate(text, formatter, null);
	}

	public static LocalDate parseLocalDate(String text, DateTimeFormatter formatter, LocalDate defaultValue) {
		try {
			return StringUtils.isNotBlank(text) ? LocalDate.parse(text, formatter) : defaultValue;
		} catch (DateTimeParseException e) {
			return defaultValue;
		}
	}

	public static LocalDate parseLocalDate(String text, String datePattern) {
		return parseLocalDate(text, datePattern, null);
	}

	public static LocalDate parseLocalDate(String text, String datePattern, LocalDate defaultValue) {
		return parseLocalDate(text, getDateTimeFormatter(datePattern), defaultValue);
	}

	public static LocalDate valueOf(Year year, int dayOfYear) {
		return year.atDay(Math.min(dayOfYear, year.isLeap() ? 366 : 365));
	}

	public static LocalDate valueOf(YearMonth yearMonth, int dayOfMonth) {
		return yearMonth.atDay(dayOfMonth);
	}

	public static LocalDate valueOf(Year year, Month month, int dayOfMonth) {
		return valueOf(year.atMonth(month), dayOfMonth);
	}

	public static LocalDate valueOf(int year, int month, int dayOfMonth) {
		return LocalDate.of(year, month, dayOfMonth);
	}

	private static DateTimeFormatter getDateTimeFormatter(String datePattern) {
		Assert.hasNoText(datePattern, "DatePattern can not be blank.");
		DateTimeFormatter sdf = dfCache.get(datePattern);
		if (sdf == null) {
			dfCache.put(datePattern, DateTimeFormatter.ofPattern(datePattern, Locale.ENGLISH));
			sdf = dfCache.get(datePattern);
		}
		return sdf;
	}

}
