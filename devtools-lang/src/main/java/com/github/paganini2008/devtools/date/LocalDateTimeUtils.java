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
import java.time.LocalDateTime;
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
 * LocalDateTimeUtils
 *
 * @author Fred Feng
 *
 * @since 2.0.4
 */
public abstract class LocalDateTimeUtils {

	private final static LruMap<String, DateTimeFormatter> dfCache = new LruMap<String, DateTimeFormatter>(16);

	public static LocalDateTime addSeconds(Date date, int seconds) {
		return addSeconds(toLocalDateTime(date, null), seconds);
	}

	public static LocalDateTime addSeconds(LocalDateTime localDateTime, int seconds) {
		return addAmount(localDateTime, seconds, ChronoUnit.SECONDS);
	}

	public static LocalDateTime addMinutes(Date date, int minutes) {
		return addMinutes(toLocalDateTime(date, null), minutes);
	}

	public static LocalDateTime addMinutes(LocalDateTime localDateTime, int minutes) {
		return addAmount(localDateTime, minutes, ChronoUnit.MINUTES);
	}

	public static LocalDateTime addHours(Date date, int hours) {
		return addHours(toLocalDateTime(date, null), hours);
	}

	public static LocalDateTime addHours(LocalDateTime localDateTime, int hours) {
		return addAmount(localDateTime, hours, ChronoUnit.HOURS);
	}

	public static LocalDateTime addDays(Date date, int days) {
		return addDays(toLocalDateTime(date, null), days);
	}

	public static LocalDateTime addDays(LocalDateTime localDateTime, int days) {
		return addAmount(localDateTime, days, ChronoUnit.DAYS);
	}

	public static LocalDateTime addMonths(Date date, int months) {
		return addMonths(toLocalDateTime(date, null), months);
	}

	public static LocalDateTime addMonths(LocalDateTime localDateTime, int months) {
		return addAmount(localDateTime, months, ChronoUnit.MONTHS);
	}

	public static LocalDateTime addYears(Date date, int years) {
		return addDays(toLocalDateTime(date, null), years);
	}

	public static LocalDateTime addYears(LocalDateTime localDateTime, int years) {
		return addAmount(localDateTime, years, ChronoUnit.YEARS);
	}

	public static LocalDateTime addWeeks(Date date, int weeks) {
		return addWeeks(toLocalDateTime(date, null), weeks);
	}

	public static LocalDateTime addWeeks(LocalDateTime localDateTime, int weeks) {
		return addAmount(localDateTime, weeks, ChronoUnit.WEEKS);
	}

	public static LocalDateTime addAmount(LocalDateTime localDateTime, int amount, ChronoUnit chronoUnit) {
		if (localDateTime == null) {
			localDateTime = LocalDateTime.now();
		}
		return localDateTime.plus(amount, chronoUnit);
	}

	public static LocalDateTime toLocalDateTime(Long ms, ZoneId zoneId) {
		return toLocalDateTime(ms, zoneId, null);
	}

	public static LocalDateTime toLocalDateTime(Long ms, ZoneId zoneId, LocalDateTime defaultValue) {
		if (zoneId == null) {
			zoneId = ZoneId.systemDefault();
		}
		try {
			return ms != null ? Instant.ofEpochMilli(ms).atZone(zoneId).toLocalDateTime() : defaultValue;
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	public static LocalDateTime toLocalDateTime(Date date, ZoneId zoneId) {
		return toLocalDateTime(date, zoneId, null);
	}

	public static LocalDateTime toLocalDateTime(Date date, ZoneId zoneId, LocalDateTime defaultValue) {
		if (zoneId == null) {
			zoneId = ZoneId.systemDefault();
		}
		try {
			return date != null ? date.toInstant().atZone(zoneId).toLocalDateTime() : defaultValue;
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	public static LocalDateTime toLocalDateTime(Calendar calendar, ZoneId zoneId) {
		return toLocalDateTime(calendar, zoneId, null);
	}

	public static LocalDateTime toLocalDateTime(Calendar calendar, ZoneId zoneId, LocalDateTime defaultValue) {
		if (zoneId == null) {
			zoneId = ZoneId.systemDefault();
		}
		try {
			return calendar != null ? calendar.toInstant().atZone(zoneId).toLocalDateTime() : defaultValue;
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	public static String format(LocalDateTime localDateTime, String datePattern) {
		return format(localDateTime, datePattern, "");
	}

	public static String format(LocalDateTime localDateTime, String datePattern, String defaultValue) {
		return localDateTime != null ? localDateTime.format(getDateTimeFormatter(datePattern)) : defaultValue;
	}

	public static String format(LocalDateTime localDateTime) {
		return format(localDateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	}

	public static String format(LocalDateTime localDateTime, DateTimeFormatter dateTimeFormatter) {
		return format(localDateTime, dateTimeFormatter, "");
	}

	public static String format(LocalDateTime localDateTime, DateTimeFormatter dateTimeFormatter, String defaultValue) {
		return localDateTime != null ? localDateTime.format(dateTimeFormatter) : defaultValue;
	}

	public static LocalDateTime parseLocalDateTime(String text) {
		return parseLocalDateTime(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	}

	public static LocalDateTime parseLocalDateTime(String text, DateTimeFormatter formatter) {
		return parseLocalDateTime(text, formatter, null);
	}

	public static LocalDateTime parseLocalDateTime(String text, DateTimeFormatter formatter, LocalDateTime defaultValue) {
		try {
			return StringUtils.isNotBlank(text) ? LocalDateTime.parse(text, formatter) : defaultValue;
		} catch (DateTimeParseException e) {
			return defaultValue;
		}
	}

	public static LocalDateTime parseLocalDateTime(String text, String datePattern) {
		return parseLocalDateTime(text, datePattern, null);
	}

	public static LocalDateTime parseLocalDateTime(String text, String datePattern, LocalDateTime defaultValue) {
		return parseLocalDateTime(text, getDateTimeFormatter(datePattern), defaultValue);
	}

	public static LocalDateTime valueOf(YearMonth yearMonth, int dayOfMonth, int hourOfDay, int minute, int second) {
		if (yearMonth == null) {
			yearMonth = YearMonth.now();
		}
		return yearMonth.atDay(dayOfMonth).atTime(hourOfDay, minute, second);
	}

	public static LocalDateTime valueOf(Year year, Month month, int dayOfMonth, int hourOfDay, int minute, int second) {
		YearMonth yearMonth = YearMonth.now();
		if (year != null && month != null) {
			yearMonth = year.atMonth(month);
		} else if (year != null && month == null) {
			yearMonth = yearMonth.with(year);
		} else if (year == null && month != null) {
			yearMonth = yearMonth.with(month);
		}
		return valueOf(yearMonth, dayOfMonth, hourOfDay, minute, second);
	}

	public static LocalDateTime of(Date date, int hourOfDay, int minute, int second) {
		LocalDate localDate = LocalDateUtils.toLocalDate(date, null, LocalDate.now());
		return valueOf(localDate, hourOfDay, minute, second);
	}

	public static LocalDateTime valueOf(LocalDate localDate, int hourOfDay, int minute, int second) {
		if (localDate == null) {
			localDate = LocalDate.now();
		}
		return localDate.atTime(hourOfDay, minute, second);
	}

	public static LocalDateTime valueOf(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
		return LocalDateTime.of(year, month, dayOfMonth, hourOfDay, minute, second);
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
