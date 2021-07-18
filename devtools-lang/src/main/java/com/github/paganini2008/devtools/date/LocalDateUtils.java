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
package com.github.paganini2008.devtools.date;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
 * @version 1.0
 */
public abstract class LocalDateUtils {

	public final static String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
	public final static String DEFAULT_TIME_PATTERN = "HH:mm:ss";
	public final static String DEFAULT_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	public final static DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN, Locale.ENGLISH);
	public final static DateTimeFormatter DEFAULT_TIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_TIME_PATTERN, Locale.ENGLISH);
	public final static DateTimeFormatter DEFAULT_DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATETIME_PATTERN,
			Locale.ENGLISH);
	private final static LruMap<String, DateTimeFormatter> datetimeFormatterCache = new LruMap<String, DateTimeFormatter>(16);

	public static LocalDate toLocalDate(Long ms, ZoneId zoneId) {
		return toLocalDate(ms, zoneId, null);
	}

	public static LocalDate toLocalDate(Long ms, ZoneId zoneId, LocalDate defaultValue) {
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
		try {
			return calendar != null ? calendar.toInstant().atZone(zoneId).toLocalDate() : defaultValue;
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	public static LocalDateTime toLocalDateTime(Long ms, ZoneId zoneId) {
		return toLocalDateTime(ms, zoneId, null);
	}

	public static LocalDateTime toLocalDateTime(Long ms, ZoneId zoneId, LocalDateTime defaultValue) {
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
		try {
			return calendar != null ? calendar.toInstant().atZone(zoneId).toLocalDateTime() : defaultValue;
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	public static LocalTime toLocalTime(Long ms, ZoneId zoneId) {
		return toLocalTime(ms, zoneId, null);
	}

	public static LocalTime toLocalTime(Long ms, ZoneId zoneId, LocalTime defaultValue) {
		try {
			return ms != null ? Instant.ofEpochMilli(ms).atZone(zoneId).toLocalTime() : defaultValue;
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	public static LocalTime toLocalTime(Date date, ZoneId zoneId) {
		return toLocalTime(date, zoneId, null);
	}

	public static LocalTime toLocalTime(Date date, ZoneId zoneId, LocalTime defaultValue) {
		try {
			return date != null ? date.toInstant().atZone(zoneId).toLocalTime() : defaultValue;
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	public static LocalTime toLocalTime(Calendar calendar, ZoneId zoneId) {
		return toLocalTime(calendar, zoneId, null);
	}

	public static LocalTime toLocalTime(Calendar calendar, ZoneId zoneId, LocalTime defaultValue) {
		try {
			return calendar != null ? calendar.toInstant().atZone(zoneId).toLocalTime() : defaultValue;
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	private static DateTimeFormatter getDateTimeFormatter(String datePattern) {
		Assert.hasNoText(datePattern, "Date pattern can not be blank.");
		DateTimeFormatter sdf = datetimeFormatterCache.get(datePattern);
		if (sdf == null) {
			datetimeFormatterCache.put(datePattern, DateTimeFormatter.ofPattern(datePattern, Locale.ENGLISH));
			sdf = datetimeFormatterCache.get(datePattern);
		}
		return sdf;
	}

	public static String format(LocalDate localDate) {
		return format(localDate, DEFAULT_DATETIME_FORMATTER);
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

	public static String format(LocalDateTime localDateTime, String datePattern) {
		return format(localDateTime, datePattern, "");
	}

	public static String format(LocalDateTime localDateTime, String datePattern, String defaultValue) {
		return localDateTime != null ? localDateTime.format(getDateTimeFormatter(datePattern)) : defaultValue;
	}

	public static String format(LocalDateTime localDateTime) {
		return format(localDateTime, DEFAULT_DATETIME_FORMATTER);
	}

	public static String format(LocalDateTime localDateTime, DateTimeFormatter dateTimeFormatter) {
		return format(localDateTime, dateTimeFormatter, "");
	}

	public static String format(LocalDateTime localDateTime, DateTimeFormatter dateTimeFormatter, String defaultValue) {
		return localDateTime != null ? localDateTime.format(dateTimeFormatter) : defaultValue;
	}

	public static Long getTimeInMillis(Instant instant) {
		return getTimeInMillis(instant, null);
	}

	public static Long getTimeInMillis(Instant instant, Long defaultValue) {
		return instant != null ? instant.toEpochMilli() : defaultValue;
	}

	public static LocalDate parseLocalDate(String text) {
		return parseLocalDate(text, DEFAULT_DATE_FORMATTER);
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

	public static LocalDateTime parseLocalDateTime(String text) {
		return parseLocalDateTime(text, DEFAULT_DATETIME_FORMATTER);
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

	public static LocalTime parseLocalTime(String text) {
		return parseLocalTime(text, DEFAULT_TIME_FORMATTER);
	}

	public static LocalTime parseLocalTime(String text, DateTimeFormatter formatter) {
		return parseLocalTime(text, formatter, null);
	}

	public static LocalTime parseLocalTime(String text, DateTimeFormatter formatter, LocalTime defaultValue) {
		try {
			return StringUtils.isNotBlank(text) ? LocalTime.parse(text, formatter) : defaultValue;
		} catch (DateTimeParseException e) {
			return defaultValue;
		}
	}

	public static LocalTime parseLocalTime(String text, String datePattern) {
		return parseLocalTime(text, datePattern, null);
	}

	public static LocalTime parseLocalTime(String text, String datePattern, LocalTime defaultValue) {
		return parseLocalTime(text, getDateTimeFormatter(datePattern), defaultValue);
	}

}
