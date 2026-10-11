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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.collection.LruMap;

/**
 * Utility methods for {@link Date}. All methods are thread-safe;
 * methods without a time zone parameter use the system default time zone.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class DateUtils {

	public static final int MIN_YEAR = 1970;
	public static final int MAX_YEAR = 9999;

	public static final Date[] EMPTY_ARRAY = new Date[0];
	public final static String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
	private final static LruMap<String, SimpleDateFormat> dfCache = new LruMap<String, SimpleDateFormat>(16);

	/**
	 * Converts milliseconds since the epoch to a {@code Date}.
	 *
	 * <pre>
	 * DateUtils.toDate(1000L)     = new Date(1000L)
	 * DateUtils.toDate((Long) null) = null
	 * </pre>
	 */
	public static Date toDate(Long ms) {
		return toDate(ms, null);
	}

	/**
	 * Converts milliseconds since the epoch to a {@code Date}, returning the default value for null.
	 */
	public static Date toDate(Long ms, Date defaultValue) {
		return ms != null ? new Date(ms) : defaultValue;
	}

	/**
	 * Converts an {@code Instant} to a {@code Date}, null returns null.
	 */
	public static Date toDate(Instant ms) {
		return toDate(ms, null);
	}

	/**
	 * Converts an {@code Instant} to a {@code Date}, returning the default value for null.
	 */
	public static Date toDate(Instant ms, Date defaultValue) {
		return ms != null ? Date.from(ms) : defaultValue;
	}

	/**
	 * Returns the time of a calendar, null returns null.
	 */
	public static Date toDate(Calendar calendar) {
		return toDate(calendar, null);
	}

	/**
	 * Returns the time of a calendar, returning the default value for null.
	 */
	public static Date toDate(Calendar calendar, Date defaultValue) {
		return calendar != null ? calendar.getTime() : defaultValue;
	}

	/**
	 * Converts a local date to a {@code Date} at the start of that day, null returns null.
	 *
	 * @param zoneId the time zone, null means the system default
	 */
	public static Date toDate(LocalDate localDate, ZoneId zoneId) {
		return toDate(localDate, zoneId, null);
	}

	/**
	 * Converts a local date to a {@code Date} at the start of that day, returning the default value for null.
	 *
	 * @param zoneId the time zone, null means the system default
	 */
	public static Date toDate(LocalDate localDate, ZoneId zoneId, Date defaultValue) {
		if (zoneId == null) {
			zoneId = ZoneId.systemDefault();
		}
		return localDate != null ? Date.from(localDate.atStartOfDay(zoneId).toInstant()) : defaultValue;
	}

	/**
	 * Converts a local date time to a {@code Date}, null returns null.
	 *
	 * @param zoneId the time zone, null means the system default
	 */
	public static Date toDate(LocalDateTime localDateTime, ZoneId zoneId) {
		return toDate(localDateTime, zoneId, null);
	}

	/**
	 * Converts a local date time to a {@code Date}, returning the default value for null.
	 *
	 * @param zoneId the time zone, null means the system default
	 */
	public static Date toDate(LocalDateTime localDateTime, ZoneId zoneId, Date defaultValue) {
		if (zoneId == null) {
			zoneId = ZoneId.systemDefault();
		}
		return localDateTime != null ? Date.from(localDateTime.atZone(zoneId).toInstant()) : defaultValue;
	}

	/**
	 * Converts an array of milliseconds to dates.
	 */
	public static Date[] toDateArray(long[] dates) {
		Date[] array = new Date[dates.length];
		int i = 0;
		for (long date : dates) {
			array[i++] = toDate(date);
		}
		return array;
	}

	/**
	 * Converts an array of milliseconds to dates, null elements stay null.
	 */
	public static Date[] toDateArray(Long[] mss) {
		return toDateArray(mss, null);
	}

	/**
	 * Converts an array of milliseconds to dates, null elements become the default value.
	 */
	public static Date[] toDateArray(Long[] mss, Date defaultValue) {
		Date[] array = new Date[mss.length];
		int i = 0;
		for (Long ms : mss) {
			array[i++] = toDate(ms, defaultValue);
		}
		return array;
	}

	/**
	 * Converts an array of calendars to dates, null elements stay null.
	 */
	public static Date[] toDateArray(Calendar[] array) {
		return toDateArray(array, null);
	}

	/**
	 * Converts an array of calendars to dates, null elements become the default value.
	 */
	public static Date[] toDateArray(Calendar[] array, Date defaultValue) {
		Date[] result = new Date[array.length];
		int i = 0;
		for (Calendar c : array) {
			result[i++] = toDate(c, defaultValue);
		}
		return result;
	}

	/**
	 * Returns the milliseconds of an instant, null returns null.
	 */
	public static Long getTimeInMillis(Instant instant) {
		return getTimeInMillis(instant, null);
	}

	/**
	 * Returns the milliseconds of an instant, returning the default value for null.
	 */
	public static Long getTimeInMillis(Instant instant, Long defaultValue) {
		return instant != null ? Long.valueOf(instant.toEpochMilli()) : defaultValue;
	}

	/**
	 * Formats milliseconds with {@link #DEFAULT_DATE_PATTERN} in the system default time zone, null returns "".
	 */
	public static String format(Long ms) {
		return format(ms, DEFAULT_DATE_PATTERN);
	}

	/**
	 * Formats milliseconds with the pattern in the system default time zone, null returns "".
	 */
	public static String format(Long ms, String datePattern) {
		return format(ms, datePattern, "");
	}

	/**
	 * Formats milliseconds with the pattern in the system default time zone, returning the default value for null.
	 */
	public static String format(Long ms, String datePattern, String defaultValue) {
		return format(ms, getDateFormatter(datePattern), defaultValue);
	}

	/**
	 * Formats milliseconds with the given format, null returns "".
	 */
	public static String format(Long ms, DateFormat df) {
		return format(ms, df, "");
	}

	/**
	 * Formats milliseconds with the given format, returning the default value for null.
	 */
	public static String format(Long ms, DateFormat df, String defaultValue) {
		if (ms == null) {
			return defaultValue;
		}
		return format(new Date(ms), df, defaultValue);
	}

	/**
	 * Formats a date with {@link #DEFAULT_DATE_PATTERN} in the system default time zone.
	 *
	 * <pre>
	 * DateUtils.format(DateUtils.of(2021, 1, 31, 10, 20, 30)) = "2021-01-31 10:20:30"
	 * DateUtils.format((Date) null)                          = ""
	 * </pre>
	 */
	public static String format(Date date) {
		return format(date, DEFAULT_DATE_PATTERN);
	}

	/**
	 * Formats a date with the pattern (English locale) in the system default time zone, null returns "".
	 * Cached formatters are never shared between threads.
	 *
	 * <pre>
	 * DateUtils.format(DateUtils.of(2021, 1, 31), "dd/MMM/yyyy") = "31/Jan/2021"
	 * </pre>
	 *
	 * @throws IllegalArgumentException if the pattern is blank
	 */
	public static String format(Date date, String datePattern) {
		return format(date, datePattern, "");
	}

	/**
	 * Formats a date with the pattern in the system default time zone, returning the default value for null.
	 *
	 * @throws IllegalArgumentException if the pattern is blank
	 */
	public static String format(Date date, String datePattern, String defaultValue) {
		return format(date, getDateFormatter(datePattern), defaultValue);
	}

	/**
	 * Formats a date with the given format, null returns "". Access to the format is synchronized on it.
	 */
	public static String format(Date date, DateFormat df) {
		return format(date, df, "");
	}

	/**
	 * Formats a date with the given format, returning the default value for null or a formatting error.
	 *
	 * @throws IllegalArgumentException if the date is not null and the format is null
	 */
	public static String format(Date date, DateFormat df, String defaultValue) {
		if (date == null) {
			return defaultValue;
		}
		Assert.isNull(df, "DateFormat can not be null.");
		return doFormat(date, df, defaultValue);
	}

	/**
	 * Formats each date with {@link #DEFAULT_DATE_PATTERN}, null elements become "".
	 */
	public static String[] formatMany(Date[] dates) {
		return formatMany(dates, DEFAULT_DATE_PATTERN);
	}

	/**
	 * Formats each date with the given format, null elements become "".
	 */
	public static String[] formatMany(Date[] dates, DateFormat df) {
		return formatMany(dates, df, "");
	}

	/**
	 * Formats each date with the given format, null elements become the default value.
	 *
	 * @throws IllegalArgumentException if the array is null
	 */
	public static String[] formatMany(Date[] dates, DateFormat df, String defaultValue) {
		Assert.isNull(dates, "Date array can not be null.");
		String[] values = new String[dates.length];
		int i = 0;
		for (Date date : dates) {
			values[i++] = format(date, df, defaultValue);
		}
		return values;
	}

	/**
	 * Formats each date with the pattern, null elements become "".
	 */
	public static String[] formatMany(Date[] dates, String datePattern) {
		return formatMany(dates, datePattern, "");
	}

	/**
	 * Formats each date with the pattern, null elements become the default value.
	 */
	public static String[] formatMany(Date[] dates, String datePattern, String defaultValue) {
		return formatMany(dates, getDateFormatter(datePattern), defaultValue);
	}

	/**
	 * Parses a date string with one pattern and formats it with another, returning null if it can not be parsed.
	 *
	 * <pre>
	 * DateUtils.reformat("2021-01-31", "yyyy-MM-dd", "dd/MM/yyyy") = "31/01/2021"
	 * DateUtils.reformat("bad", "yyyy-MM-dd", "dd/MM/yyyy")        = null
	 * </pre>
	 */
	public static String reformat(String str, String srcFormat, String destFormat) {
		return reformat(str, srcFormat, destFormat, null);
	}

	/**
	 * Parses a date string with one pattern and formats it with another, returning the default value if it can not be parsed.
	 */
	public static String reformat(String str, String srcFormat, String destFormat, String defaultValue) {
		DateFormat df = getDateFormatter(srcFormat);
		Date date = doParse(str, df, null);
		if (date == null) {
			return defaultValue;
		}
		df = getDateFormatter(destFormat);
		return doFormat(date, df, defaultValue);
	}

	/**
	 * Parses a date string with the pattern (English locale, system default time zone).
	 *
	 * <pre>
	 * DateUtils.parse("2021-01-31 10:20:30", "yyyy-MM-dd HH:mm:ss") = 2021-01-31 10:20:30
	 * DateUtils.parse("bad", "yyyy-MM-dd")                         = null
	 * DateUtils.parse(null, "yyyy-MM-dd")                          = null
	 * </pre>
	 *
	 * @return the date, or null if the string is blank or can not be parsed
	 * @throws IllegalArgumentException if the pattern is blank
	 */
	public static Date parse(String str, String datePattern) {
		return parse(str, datePattern, null);
	}

	/**
	 * Parses a date string with the pattern, returning the default value if the string is blank or can not be parsed.
	 */
	public static Date parse(String str, String datePattern, Date defaultValue) {
		DateFormat df = getDateFormatter(datePattern);
		return doParse(str, df, defaultValue);
	}

	/**
	 * Parses a date string with the first pattern that matches, returning null if none matches.
	 *
	 * @throws IllegalArgumentException if the patterns are null
	 */
	public static Date parse(String str, String[] datePatterns) {
		return parse(str, datePatterns, null);
	}

	/**
	 * Parses a date string with the first pattern that matches, returning the default value if none matches.
	 *
	 * @throws IllegalArgumentException if the patterns are null
	 */
	public static Date parse(String str, String[] datePatterns, Date defaultValue) {
		Assert.isNull(datePatterns, "Date patterns can not be null.");
		Date date = null;
		for (String datePattern : datePatterns) {
			date = parse(str, datePattern, null);
			if (date != null) {
				return date;
			}
		}
		return defaultValue;
	}

	private static String doFormat(Date date, DateFormat df, String defaultValue) {
		// DateFormat is not thread-safe, so guard the instance itself
		synchronized (df) {
			try {
				return df.format(date);
			} catch (RuntimeException e) {
				return defaultValue;
			}
		}
	}

	private static Date doParse(String str, DateFormat df, Date defaultValue) {
		if (StringUtils.isBlank(str)) {
			return defaultValue;
		}
		// DateFormat is not thread-safe, so guard the instance itself
		synchronized (df) {
			try {
				return df.parse(str);
			} catch (ParseException e) {
				return defaultValue;
			}
		}
	}

	/**
	 * Adds the amount of years to now.
	 *
	 * @see #addYears(Date, int)
	 */
	public static Date addYears(int amount) {
		return addYears(new Date(), amount);
	}

	/**
	 * Adds the amount of years ({@code Calendar.YEAR}) to a date and returns a new date.
	 *
	 * @param date the date, null means now
	 * @param amount the amount to add, may be negative
	 */
	public static Date addYears(Date date, int amount) {
		return addField(date, Calendar.YEAR, amount);
	}

	/**
	 * Adds the amount of months to now.
	 *
	 * @see #addMonths(Date, int)
	 */
	public static Date addMonths(int amount) {
		return addMonths(new Date(), amount);
	}

	/**
	 * Adds the amount of months ({@code Calendar.MONTH}) to a date and returns a new date.
	 *
	 * @param date the date, null means now
	 * @param amount the amount to add, may be negative
	 */
	public static Date addMonths(Date date, int amount) {
		return addField(date, Calendar.MONTH, amount);
	}

	/**
	 * Adds the amount of weeks to now.
	 *
	 * @see #addWeekOfYear(Date, int)
	 */
	public static Date addWeekOfYear(int amount) {
		return addWeekOfYear(new Date(), amount);
	}

	/**
	 * Adds the amount of weeks ({@code Calendar.WEEK_OF_YEAR}) to a date and returns a new date.
	 *
	 * @param date the date, null means now
	 * @param amount the amount to add, may be negative
	 */
	public static Date addWeekOfYear(Date date, int amount) {
		return addField(date, Calendar.WEEK_OF_YEAR, amount);
	}

	/**
	 * Adds the amount of weeks to now.
	 *
	 * @see #addWeekOfMonth(Date, int)
	 */
	public static Date addWeekOfMonth(int amount) {
		return addWeekOfMonth(new Date(), amount);
	}

	/**
	 * Adds the amount of weeks ({@code Calendar.WEEK_OF_MONTH}) to a date and returns a new date.
	 *
	 * @param date the date, null means now
	 * @param amount the amount to add, may be negative
	 */
	public static Date addWeekOfMonth(Date date, int amount) {
		return addField(date, Calendar.WEEK_OF_MONTH, amount);
	}

	/**
	 * Adds the amount of days to now.
	 *
	 * @see #addDayOfYear(Date, int)
	 */
	public static Date addDayOfYear(int amount) {
		return addDayOfYear(new Date(), amount);
	}

	/**
	 * Adds the amount of days ({@code Calendar.DAY_OF_YEAR}) to a date and returns a new date.
	 *
	 * @param date the date, null means now
	 * @param amount the amount to add, may be negative
	 */
	public static Date addDayOfYear(Date date, int amount) {
		return addField(date, Calendar.DAY_OF_YEAR, amount);
	}

	/**
	 * Adds the amount of days to now.
	 *
	 * @see #addDayOfMonth(Date, int)
	 */
	public static Date addDayOfMonth(int amount) {
		return addDayOfMonth(new Date(), amount);
	}

	/**
	 * Adds the amount of days ({@code Calendar.DAY_OF_MONTH}) to a date and returns a new date.
	 *
	 * @param date the date, null means now
	 * @param amount the amount to add, may be negative
	 */
	public static Date addDayOfMonth(Date date, int amount) {
		return addField(date, Calendar.DAY_OF_MONTH, amount);
	}

	/**
	 * Adds the amount of days to now.
	 *
	 * @see #addDayOfWeek(Date, int)
	 */
	public static Date addDayOfWeek(int amount) {
		return addDayOfWeek(new Date(), amount);
	}

	/**
	 * Adds the amount of days ({@code Calendar.DAY_OF_WEEK}) to a date and returns a new date.
	 *
	 * @param date the date, null means now
	 * @param amount the amount to add, may be negative
	 */
	public static Date addDayOfWeek(Date date, int amount) {
		return addField(date, Calendar.DAY_OF_WEEK, amount);
	}

	/**
	 * Adds the amount of hours to now.
	 *
	 * @see #addHourOfDay(Date, int)
	 */
	public static Date addHourOfDay(int amount) {
		return addHourOfDay(new Date(), amount);
	}

	/**
	 * Adds the amount of hours ({@code Calendar.HOUR_OF_DAY}) to a date and returns a new date.
	 *
	 * @param date the date, null means now
	 * @param amount the amount to add, may be negative
	 */
	public static Date addHourOfDay(Date date, int amount) {
		return addField(date, Calendar.HOUR_OF_DAY, amount);
	}

	/**
	 * Adds the amount of minutes to now.
	 *
	 * @see #addMinutes(Date, int)
	 */
	public static Date addMinutes(int amount) {
		return addMinutes(new Date(), amount);
	}

	/**
	 * Adds the amount of minutes ({@code Calendar.MINUTE}) to a date and returns a new date.
	 *
	 * @param date the date, null means now
	 * @param amount the amount to add, may be negative
	 */
	public static Date addMinutes(Date date, int amount) {
		return addField(date, Calendar.MINUTE, amount);
	}

	/**
	 * Adds the amount of seconds to now.
	 *
	 * @see #addSeconds(Date, int)
	 */
	public static Date addSeconds(int amount) {
		return addSeconds(new Date(), amount);
	}

	/**
	 * Adds the amount of seconds ({@code Calendar.SECOND}) to a date and returns a new date.
	 *
	 * @param date the date, null means now
	 * @param amount the amount to add, may be negative
	 */
	public static Date addSeconds(Date date, int amount) {
		return addField(date, Calendar.SECOND, amount);
	}

	/**
	 * Adds an amount to a calendar field of a date and returns a new date, the input is not modified.
	 *
	 * @param date the date, null means now
	 * @param calendarField a {@code Calendar} field constant
	 */
	public static Date addField(Date date, int calendarField, int amount) {
		if (date == null) {
			date = new Date();
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(calendarField, amount);
		return c.getTime();
	}

	/**
	 * Sets the field ({@code Calendar.YEAR}) of now.
	 */
	public static Date setYear(int amount) {
		return setYear(new Date(), amount);
	}

	/**
	 * Sets the field ({@code Calendar.YEAR}) of a date and returns a new date.
	 *
	 * @param date the date, null means now
	 */
	public static Date setYear(Date date, int amount) {
		return setField(date, Calendar.YEAR, amount);
	}

	/**
	 * Sets the field ({@code Calendar.MONTH, 0-based}) of now.
	 */
	public static Date setMonth(int amount) {
		return setMonth(new Date(), amount);
	}

	/**
	 * Sets the field ({@code Calendar.MONTH, 0-based}) of a date and returns a new date.
	 *
	 * @param date the date, null means now
	 */
	public static Date setMonth(Date date, int amount) {
		return setField(date, Calendar.MONTH, amount);
	}

	/**
	 * Sets the field ({@code Calendar.WEEK_OF_YEAR}) of now.
	 */
	public static Date setWeekOfYear(int amount) {
		return setWeekOfYear(new Date(), amount);
	}

	/**
	 * Sets the field ({@code Calendar.WEEK_OF_YEAR}) of a date and returns a new date.
	 *
	 * @param date the date, null means now
	 */
	public static Date setWeekOfYear(Date date, int amount) {
		return setField(date, Calendar.WEEK_OF_YEAR, amount);
	}

	/**
	 * Sets the field ({@code Calendar.WEEK_OF_MONTH}) of now.
	 */
	public static Date setWeekOfMonth(int amount) {
		return setWeekOfMonth(new Date(), amount);
	}

	/**
	 * Sets the field ({@code Calendar.WEEK_OF_MONTH}) of a date and returns a new date.
	 *
	 * @param date the date, null means now
	 */
	public static Date setWeekOfMonth(Date date, int amount) {
		return setField(date, Calendar.WEEK_OF_MONTH, amount);
	}

	/**
	 * Sets the field ({@code Calendar.DAY_OF_WEEK}) of now.
	 */
	public static Date setDayOfWeek(int amount) {
		return setDayOfWeek(new Date(), amount);
	}

	/**
	 * Sets the field ({@code Calendar.DAY_OF_WEEK}) of a date and returns a new date.
	 *
	 * @param date the date, null means now
	 */
	public static Date setDayOfWeek(Date date, int amount) {
		return setField(date, Calendar.DAY_OF_WEEK, amount);
	}

	/**
	 * Sets the field ({@code Calendar.DAY_OF_MONTH}) of now.
	 */
	public static Date setDayOfMonth(int amount) {
		return setDayOfMonth(new Date(), amount);
	}

	/**
	 * Sets the field ({@code Calendar.DAY_OF_MONTH}) of a date and returns a new date.
	 *
	 * @param date the date, null means now
	 */
	public static Date setDayOfMonth(Date date, int amount) {
		return setField(date, Calendar.DAY_OF_MONTH, amount);
	}

	/**
	 * Sets the field ({@code Calendar.DAY_OF_YEAR}) of now.
	 */
	public static Date setDayOfYear(int amount) {
		return setDayOfYear(new Date(), amount);
	}

	/**
	 * Sets the field ({@code Calendar.DAY_OF_YEAR}) of a date and returns a new date.
	 *
	 * @param date the date, null means now
	 */
	public static Date setDayOfYear(Date date, int amount) {
		return setField(date, Calendar.DAY_OF_YEAR, amount);
	}

	private static Date setField(Date date, int calendarField, int amount) {
		if (date == null) {
			date = new Date();
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(calendarField, amount);
		return c.getTime();
	}

	/**
	 * Returns the start (00:00:00.000) of the current day in the system default time zone.
	 */
	public static Date today() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}

	/**
	 * Sets the time of a date to the given hour (0-11) before noon, minute and second.
	 *
	 * @param date the date, null means now
	 */
	public static Date setAM(Date date, int hour, int minute, int second) {
		if (date == null) {
			date = new Date();
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.AM_PM, Calendar.AM);
		c.set(Calendar.HOUR, hour);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, second);
		return c.getTime();
	}

	/**
	 * Sets the time of a date to the given hour (0-11) after noon, minute and second.
	 *
	 * @param date the date, null means now
	 */
	public static Date setPM(Date date, int hour, int minute, int second) {
		if (date == null) {
			date = new Date();
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.AM_PM, Calendar.PM);
		c.set(Calendar.HOUR, hour);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, second);
		return c.getTime();
	}

	/**
	 * Returns the milliseconds of a date, null returns null.
	 */
	public static Long getTimeInMillis(Date date) {
		return getTimeInMillis(date, null);
	}

	/**
	 * Returns the milliseconds of a date, returning the default value for null.
	 */
	public static Long getTimeInMillis(Date date, Long defaultValue) {
		return date != null ? Long.valueOf(date.getTime()) : defaultValue;
	}

	/**
	 * Returns the milliseconds of each date, null elements stay null.
	 *
	 * @throws IllegalArgumentException if the array is null
	 */
	public static Long[] getTimeInMillis(Date[] dates) {
		return getTimeInMillis(dates, null);
	}

	/**
	 * Returns the milliseconds of each date, null elements become the default value.
	 *
	 * @throws IllegalArgumentException if the array is null
	 */
	public static Long[] getTimeInMillis(Date[] dates, Long defaultValue) {
		Assert.isNull(dates, "Date string array must not be null.");
		Long[] values = new Long[dates.length];
		int i = 0;
		for (Date date : dates) {
			values[i++] = getTimeInMillis(date, defaultValue);
		}
		return values;
	}

	/**
	 * Sets the hour, minute and second of the date given in milliseconds, milliseconds of the second are kept.
	 */
	public static Date setTime(long time, int hourOfDay, int minute, int second) {
		return setTime(new Date(time), hourOfDay, minute, second);
	}

	/**
	 * Sets the hour, minute and second of the current date, milliseconds of the second are kept.
	 */
	public static Date setTime(int hourOfDay, int minute, int second) {
		return setTime(new Date(), hourOfDay, minute, second);
	}

	/**
	 * Sets the hour, minute and second of a date, milliseconds of the second are kept.
	 *
	 * @throws IllegalArgumentException if the date is null
	 */
	public static Date setTime(Date date, int hourOfDay, int minute, int second) {
		Assert.isNull(date, "The date must not be null");
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, hourOfDay);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, second);
		return c.getTime();
	}

	/**
	 * Copies the hour, minute and second of {@code time} to {@code date}.
	 *
	 * @throws IllegalArgumentException if any argument is null
	 */
	public static Date setTime(Date date, Date time) {
		Assert.isNull(date, "The date must not be null");
		Assert.isNull(time, "The time must not be null");
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		Calendar copy = (Calendar) c.clone();
		copy.setTime(time);
		c.set(Calendar.HOUR_OF_DAY, copy.get(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, copy.get(Calendar.MINUTE));
		c.set(Calendar.SECOND, copy.get(Calendar.SECOND));
		return c.getTime();
	}

	/**
	 * Parses each string with the patterns, unparsable elements become null.
	 */
	public static Date[] parseMany(String[] strings, String[] datePatterns) {
		return parseMany(strings, datePatterns, null);
	}

	/**
	 * Parses each string with the patterns, unparsable elements become the default value.
	 *
	 * @throws IllegalArgumentException if the array is null
	 */
	public static Date[] parseMany(String[] strings, String[] datePatterns, Date defaultValue) {
		Assert.isNull(strings, "String array can not be null.");
		Date[] result = new Date[strings.length];
		int i = 0;
		for (String str : strings) {
			result[i++] = parse(str, datePatterns, defaultValue);
		}
		return result;
	}

	/**
	 * Returns the field ({@code Calendar.YEAR}) of now.
	 */
	public static int getYear() {
		return getYear(new Date());
	}

	/**
	 * Returns the field ({@code Calendar.YEAR}) of a date.
	 *
	 * @throws IllegalArgumentException if the date is null
	 */
	public static int getYear(Date date) {
		return getField(date, Calendar.YEAR);
	}

	/**
	 * Returns the field ({@code Calendar.MONTH, 0-based}) of now.
	 */
	public static int getMonth() {
		return getMonth(new Date());
	}

	/**
	 * Returns the field ({@code Calendar.MONTH, 0-based}) of a date.
	 *
	 * @throws IllegalArgumentException if the date is null
	 */
	public static int getMonth(Date date) {
		return getField(date, Calendar.MONTH);
	}

	/**
	 * Returns the field ({@code Calendar.DAY_OF_MONTH}) of now.
	 */
	public static int getDayOfMonth() {
		return getDayOfMonth(new Date());
	}

	/**
	 * Returns the field ({@code Calendar.DAY_OF_MONTH}) of a date.
	 *
	 * @throws IllegalArgumentException if the date is null
	 */
	public static int getDayOfMonth(Date date) {
		return getField(date, Calendar.DAY_OF_MONTH);
	}

	/**
	 * Returns the field ({@code Calendar.DAY_OF_WEEK, 1 = Sunday}) of now.
	 */
	public static int getDayOfWeek() {
		return getDayOfWeek(new Date());
	}

	/**
	 * Returns the field ({@code Calendar.DAY_OF_WEEK, 1 = Sunday}) of a date.
	 *
	 * @throws IllegalArgumentException if the date is null
	 */
	public static int getDayOfWeek(Date date) {
		return getField(date, Calendar.DAY_OF_WEEK);
	}

	/**
	 * Returns the field ({@code Calendar.DAY_OF_YEAR}) of now.
	 */
	public static int getDayOfYear() {
		return getDayOfYear(new Date());
	}

	/**
	 * Returns the field ({@code Calendar.DAY_OF_YEAR}) of a date.
	 *
	 * @throws IllegalArgumentException if the date is null
	 */
	public static int getDayOfYear(Date date) {
		return getField(date, Calendar.DAY_OF_YEAR);
	}

	/**
	 * Returns the field ({@code Calendar.WEEK_OF_MONTH}) of now.
	 */
	public static int getWeekOfMonth() {
		return getWeekOfMonth(new Date());
	}

	/**
	 * Returns the field ({@code Calendar.WEEK_OF_MONTH}) of a date.
	 *
	 * @throws IllegalArgumentException if the date is null
	 */
	public static int getWeekOfMonth(Date date) {
		return getField(date, Calendar.WEEK_OF_MONTH);
	}

	/**
	 * Returns the field ({@code Calendar.HOUR_OF_DAY}) of now.
	 */
	public static int getHourOfDay() {
		return getHourOfDay(new Date());
	}

	/**
	 * Returns the field ({@code Calendar.HOUR_OF_DAY}) of a date.
	 *
	 * @throws IllegalArgumentException if the date is null
	 */
	public static int getHourOfDay(Date date) {
		return getField(date, Calendar.HOUR_OF_DAY);
	}

	/**
	 * Returns the field ({@code Calendar.MINUTE}) of now.
	 */
	public static int getMinute() {
		return getMinute(new Date());
	}

	/**
	 * Returns the field ({@code Calendar.MINUTE}) of a date.
	 *
	 * @throws IllegalArgumentException if the date is null
	 */
	public static int getMinute(Date date) {
		return getField(date, Calendar.MINUTE);
	}

	/**
	 * Returns the field ({@code Calendar.SECOND}) of now.
	 */
	public static int getSecond() {
		return getSecond(new Date());
	}

	/**
	 * Returns the field ({@code Calendar.SECOND}) of a date.
	 *
	 * @throws IllegalArgumentException if the date is null
	 */
	public static int getSecond(Date date) {
		return getField(date, Calendar.SECOND);
	}

	/**
	 * Returns the value of a calendar field of a date in the system default time zone.
	 *
	 * @throws IllegalArgumentException if the date is null
	 */
	public static int getField(Date date, int calendarField) {
		Assert.isNull(date, "The date must not be null");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(calendarField);
	}

	/**
	 * Returns the number of days of the year of the date (365 or 366).
	 */
	public static int getLastDayOfYear(Date date) {
		return getLastDay(date, Calendar.DAY_OF_YEAR);
	}

	/**
	 * Returns the number of days of the month of the date (28 to 31).
	 */
	public static int getLastDayOfMonth(Date date) {
		return getLastDay(date, Calendar.DAY_OF_MONTH);
	}

	/**
	 * Returns the number of weeks of the year of the date.
	 */
	public static int getLastWeekOfYear(Date date) {
		return getLastDay(date, Calendar.WEEK_OF_YEAR);
	}

	/**
	 * Returns the actual maximum value of a calendar field for the date.
	 *
	 * @throws IllegalArgumentException if the date is null
	 */
	public static int getLastDay(Date date, int calendarField) {
		Assert.isNull(date, "The date must not be null");
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.getActualMaximum(calendarField);
	}

	/**
	 * Creates a date at the start of the given day in the system default time zone.
	 *
	 * @param month the month, 1-12 (unlike {@link #getMonth(Date)} which is 0-based)
	 */
	public static Date of(int year, int month, int dayOfMonth) {
		return of(year, month, dayOfMonth, 0, 0, 0);
	}

	/**
	 * Creates a date in the system default time zone, milliseconds are zero.
	 *
	 * <pre>
	 * DateUtils.of(2021, 1, 31, 10, 20, 30) = 2021-01-31 10:20:30.000
	 * </pre>
	 *
	 * @param month the month, 1-12
	 */
	public static Date of(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(year, month - 1, dayOfMonth, hourOfDay, minute, second);
		return c.getTime();
	}

	/**
	 * @deprecated misspelled, use {@link #convertToSeconds(long, TimeUnit)} instead
	 */
	@Deprecated
	public static long converToSecond(long interval, TimeUnit timeUnit) {
		return convertToSeconds(interval, timeUnit);
	}

	/**
	 * Converts a non-negative interval to seconds.
	 *
	 * @throws IllegalArgumentException if the interval is negative
	 */
	public static long convertToSeconds(long interval, TimeUnit timeUnit) {
		if (interval < 0) {
			throw new IllegalArgumentException("interval < 0");
		}
		return timeUnit != TimeUnit.SECONDS ? TimeUnit.SECONDS.convert(interval, timeUnit) : interval;
	}

	/**
	 * Converts a non-negative interval to milliseconds.
	 *
	 * @throws IllegalArgumentException if the interval is negative
	 */
	public static long convertToMillis(long interval, TimeUnit timeUnit) {
		if (interval < 0) {
			throw new IllegalArgumentException("interval < 0");
		}
		return timeUnit != TimeUnit.MILLISECONDS ? TimeUnit.MILLISECONDS.convert(interval, timeUnit) : interval;
	}

	/**
	 * Converts a non-negative interval to nanoseconds.
	 *
	 * @throws IllegalArgumentException if the interval is negative
	 */
	public static long convertToNanos(long interval, TimeUnit timeUnit) {
		if (interval < 0) {
			throw new IllegalArgumentException("interval < 0");
		}
		return timeUnit != TimeUnit.NANOSECONDS ? TimeUnit.NANOSECONDS.convert(interval, timeUnit) : interval;
	}

	/**
	 * Same as {@link #populate(Date, Date, int, int, Function)} ending {@code days} days after {@code from}.
	 */
	public static <R> Map<Date, R> populate(Date from, int days, int interval, int calendarField, Function<Calendar, R> valueHandler) {
		return populate(from, addDayOfMonth(from, days), interval, calendarField, valueHandler);
	}

	/**
	 * Builds an ordered map from each date of the range [from, to) to a value computed from its calendar.
	 */
	public static <R> Map<Date, R> populate(Date from, Date to, int interval, int calendarField, Function<Calendar, R> valueHandler) {
		return populate(from, to, interval, calendarField, c -> c.getTime(), valueHandler);
	}

	/**
	 * Same as {@link #populate(Date, Date, int, int, Function, Function)} ending {@code days} days after {@code from}.
	 */
	public static <T, R> Map<T, R> populate(Date from, int days, int interval, int calendarField, Function<Calendar, T> keyHandler,
			Function<Calendar, R> valueHandler) {
		return populate(from, addDayOfMonth(from, days), interval, calendarField, keyHandler, valueHandler);
	}

	/**
	 * Builds an ordered map over the range [from, to) with keys and values computed from each calendar.
	 */
	public static <T, R> Map<T, R> populate(Date from, Date to, int interval, int calendarField, Function<Calendar, T> keyHandler,
			Function<Calendar, R> valueHandler) {
		Map<T, R> data = new LinkedHashMap<T, R>();
		Iterator<Calendar> dateIterator = toIterator(from, to, interval, calendarField);
		Calendar calendar;
		while (dateIterator.hasNext()) {
			calendar = dateIterator.next();
			data.put(keyHandler.apply(calendar), valueHandler.apply(calendar));
		}
		return data;
	}

	/**
	 * Same as {@link #toIterator(Date, Date, int, int)} ending {@code days} days after {@code from}.
	 */
	public static Iterator<Calendar> toIterator(Date from, int days, int interval, int calendarField) {
		Assert.isNull(from, "From date must not be null");
		return toIterator(from, addDayOfMonth(from, days), interval, calendarField);
	}

	/**
	 * Iterates from {@code from} (inclusive) to {@code to} (exclusive) by the interval of a calendar field.
	 * If {@code from} is after {@code to} the iteration goes backwards. Each element is a new calendar.
	 *
	 * @throws IllegalArgumentException if a date is null, or the dates differ and the interval is not positive
	 */
	public static Iterator<Calendar> toIterator(Date from, Date to, int interval, int calendarField) {
		Assert.isNull(from, "From date must not be null");
		Assert.isNull(to, "To date must not be null");
		Assert.isTrue(interval <= 0 && !from.equals(to), "Interval must be positive, otherwise the iteration never ends.");
		return from.before(to) ? new DateIterator(from, to, interval, calendarField)
				: new ReverseDateIterator(from, to, interval, calendarField);
	}

	/**
	 * Returns the time between two dates in the given unit, negative if {@code to} is before {@code from}.
	 */
	public static long until(Date from, Date to, TimeUnit timeUnit) {
		return timeUnit.convert(to.getTime() - from.getTime(), TimeUnit.MILLISECONDS);
	}

	/**
	 * 
	 * DateIterator
	 * 
	 * @author Fred Feng
	 *
	 * @since 2.0.2
	 */
	static class DateIterator implements Iterator<Calendar> {

		private final Date to;
		private final Calendar calendar;
		private final int interval;
		private final int calendarField;

		DateIterator(Date from, Date to, int interval, int calendarField) {
			this.calendar = CalendarUtils.toCalendar(from, null);
			this.to = to;
			this.interval = interval;
			this.calendarField = calendarField;
		}

		@Override
		public boolean hasNext() {
			return calendar.getTime().compareTo(to) < 0;
		}

		@Override
		public Calendar next() {
			Calendar copy = (Calendar) calendar.clone();
			calendar.add(calendarField, interval);
			return copy;
		}

	}

	/**
	 * 
	 * ReverseDateIterator
	 * 
	 * @author Fred Feng
	 *
	 * @since 2.0.2
	 */
	static class ReverseDateIterator implements Iterator<Calendar> {

		private final Date to;
		private final Calendar calendar;
		private final int interval;
		private final int calendarField;

		ReverseDateIterator(Date from, Date to, int interval, int calendarField) {
			this.calendar = CalendarUtils.toCalendar(from, null);
			this.to = to;
			this.interval = interval;
			this.calendarField = calendarField;
		}

		@Override
		public boolean hasNext() {
			return calendar.getTime().compareTo(to) > 0;
		}

		@Override
		public Calendar next() {
			Calendar copy = (Calendar) calendar.clone();
			calendar.add(calendarField, -1 * interval);
			return copy;
		}

	}

	private static DateFormat getDateFormatter(String datePattern) {
		Assert.hasNoText(datePattern, "Date pattern can not be blank.");
		SimpleDateFormat sdf = dfCache.get(datePattern);
		if (sdf == null) {
			sdf = new SimpleDateFormat(datePattern, Locale.ENGLISH);
			SimpleDateFormat existing = dfCache.putIfAbsent(datePattern, sdf);
			if (existing != null) {
				sdf = existing;
			}
		}
		// SimpleDateFormat is not thread-safe, never hand out the shared prototype
		return (DateFormat) sdf.clone();
	}
}
