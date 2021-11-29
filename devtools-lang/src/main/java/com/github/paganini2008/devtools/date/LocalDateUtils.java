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
import java.time.temporal.ChronoField;
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

	public static final LocalDate[] EMPTY_ARRAY = new LocalDate[0];
	private final static LruMap<String, DateTimeFormatter> dfCache = new LruMap<String, DateTimeFormatter>(16);

	public static LocalDate addYears(Date date, int years) {
		return addDays(toLocalDate(date, null), years);
	}

	public static LocalDate addYears(int years) {
		return addYears(LocalDate.now(), years);
	}

	public static LocalDate addYears(LocalDate localDate, int years) {
		return addAmount(localDate, years, ChronoUnit.YEARS);
	}

	public static LocalDate addMonths(Date date, int months) {
		return addMonths(toLocalDate(date, null), months);
	}

	public static LocalDate addMonths(int months) {
		return addMonths(LocalDate.now(), months);
	}

	public static LocalDate addMonths(LocalDate localDate, int months) {
		return addAmount(localDate, months, ChronoUnit.MONTHS);
	}

	public static LocalDate addDays(Date date, int days) {
		return addDays(toLocalDate(date, null), days);
	}

	public static LocalDate addDays(int days) {
		return addDays(LocalDate.now(), days);
	}

	public static LocalDate addDays(LocalDate localDate, int days) {
		return addAmount(localDate, days, ChronoUnit.DAYS);
	}

	public static LocalDate addWeeks(Date date, int weeks) {
		return addWeeks(toLocalDate(date, null), weeks);
	}

	public static LocalDate addWeeks(int weeks) {
		return addWeeks(LocalDate.now(), weeks);
	}

	public static LocalDate addWeeks(LocalDate localDate, int weeks) {
		return addAmount(localDate, weeks, ChronoUnit.WEEKS);
	}

	public static LocalDate addAmount(LocalDate localDate, int amount, ChronoUnit chronoUnit) {
		if (localDate == null) {
			localDate = LocalDate.now();
		}
		return localDate.plus(amount, chronoUnit);
	}

	public static LocalDate setYear(int year) {
		return setYear(LocalDate.now(), year);
	}

	public static LocalDate setYear(Date date, int year) {
		return setYear(toLocalDate(date, null), year);
	}

	public static LocalDate setYear(LocalDate localDate, int year) {
		return setField(localDate, ChronoField.YEAR, year);
	}

	public static LocalDate setMonth(int month) {
		return setMonth(LocalDate.now(), month);
	}

	public static LocalDate setMonth(Date date, int month) {
		return setMonth(toLocalDate(date, null), month);
	}

	public static LocalDate setMonth(LocalDate localDate, int month) {
		return setField(localDate, ChronoField.MONTH_OF_YEAR, month);
	}

	public static LocalDate setDayOfMonth(int dayOfMonth) {
		return setDayOfMonth(LocalDate.now(), dayOfMonth);
	}

	public static LocalDate setDayOfMonth(Date date, int dayOfMonth) {
		return setDayOfMonth(toLocalDate(date, null), dayOfMonth);
	}

	public static LocalDate setDayOfMonth(LocalDate localDate, int dayOfMonth) {
		return setField(localDate, ChronoField.DAY_OF_MONTH, dayOfMonth);
	}

	public static LocalDate setDayOfWeek(int dayOfWeek) {
		return setDayOfMonth(LocalDate.now(), dayOfWeek);
	}

	public static LocalDate setDayOfWeek(Date date, int dayOfWeek) {
		return setDayOfWeek(toLocalDate(date, null), dayOfWeek);
	}

	public static LocalDate setDayOfWeek(LocalDate localDate, int dayOfWeek) {
		return setField(localDate, ChronoField.DAY_OF_WEEK, dayOfWeek);
	}

	public static LocalDate setDayOfYear(int dayOfYear) {
		return setDayOfYear(LocalDate.now(), dayOfYear);
	}

	public static LocalDate setDayOfYear(Date date, int dayOfYear) {
		return setDayOfYear(toLocalDate(date, null), dayOfYear);
	}

	public static LocalDate setDayOfYear(LocalDate localDate, int dayOfYear) {
		return setField(localDate, ChronoField.DAY_OF_YEAR, dayOfYear);
	}

	public static LocalDate setField(LocalDate localDate, ChronoField field, int value) {
		if (localDate == null) {
			localDate = LocalDate.now();
		}
		return localDate.with(field, value);
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

	public static LocalDate of(Year year, int dayOfYear) {
		TimeAssert.validateDayOfYear(year, dayOfYear);
		return year.atDay(dayOfYear);
	}

	public static LocalDate of(YearMonth yearMonth, int dayOfMonth) {
		TimeAssert.validateDayOfMonth(yearMonth, dayOfMonth);
		return yearMonth.atDay(dayOfMonth);
	}

	public static LocalDate of(Year year, Month month, int dayOfMonth) {
		YearMonth yearMonth = YearMonthUtils.toYearMonth(year, month);
		return of(yearMonth, dayOfMonth);
	}

	public static LocalDate of(int year, int month, int dayOfMonth) {
		TimeAssert.validateYear(year);
		TimeAssert.validateMonth(month);
		TimeAssert.validateDayOfMonth(year, month, dayOfMonth);
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
