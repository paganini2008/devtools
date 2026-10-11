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

import static com.github.paganini2008.devtools.time.DateUtils.DEFAULT_DATE_PATTERN;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.StringUtils;

/**
 * Utility methods for {@link Calendar}, null safe where possible.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class CalendarUtils {

	public static final Calendar[] EMPTY_ARRAY = new Calendar[0];

	/**
	 * Formats each calendar with the pattern (default locale, system default time zone), null elements become "".
	 */
	public static String[] formatMany(Calendar[] dates, String datePattern) {
		return formatMany(dates, datePattern, "");
	}

	/**
	 * Formats each calendar with the pattern (default locale, system default time zone), null elements become the default value.
	 */
	public static String[] formatMany(Calendar[] dates, String datePattern, String defaultValue) {
		return formatMany(dates, new SimpleDateFormat(datePattern), defaultValue);
	}

	/**
	 * Formats each calendar with {@link DateUtils#DEFAULT_DATE_PATTERN}, null elements become "".
	 */
	public static String[] formatMany(Calendar[] dates) {
		return formatMany(dates, DEFAULT_DATE_PATTERN);
	}

	/**
	 * Formats each calendar with the given format, null elements become "".
	 */
	public static String[] formatMany(Calendar[] dates, DateFormat df) {
		return formatMany(dates, df, "");
	}

	/**
	 * Formats each calendar with the given format, null elements become the default value.
	 *
	 * @throws IllegalArgumentException if the array is null
	 */
	public static String[] formatMany(Calendar[] dates, DateFormat df, String defaultValue) {
		Assert.isNull(dates, "Calendar array can not be null.");
		String[] values = new String[dates.length];
		int i = 0;
		for (Calendar date : dates) {
			values[i++] = format(date, df, defaultValue);
		}
		return values;
	}

	/**
	 * Formats the time of a calendar with {@link DateUtils#DEFAULT_DATE_PATTERN} in the system default time zone, null returns "".
	 */
	public static String format(Calendar cal) {
		return format(cal, DEFAULT_DATE_PATTERN);
	}

	/**
	 * Formats the time of a calendar with the pattern in the system default time zone (not the calendar's zone), null returns "".
	 */
	public static String format(Calendar c, String datePattern) {
		return format(c, datePattern, "");
	}

	/**
	 * Formats the time of a calendar with the pattern in the system default time zone, returning the default value for null.
	 */
	public static String format(Calendar c, String datePattern, String defaultValue) {
		if (c == null) {
			return defaultValue;
		}
		return DateUtils.format(c.getTime(), datePattern, defaultValue);
	}

	/**
	 * Formats the time of a calendar with the given format, null returns null.
	 */
	public static String format(Calendar c, DateFormat df) {
		return format(c, df, null);
	}

	/**
	 * Formats the time of a calendar with the given format, returning the default value for null.
	 */
	public static String format(Calendar c, DateFormat df, String defaultValue) {
		if (c == null) {
			return defaultValue;
		}
		return DateUtils.format(c.getTime(), df, defaultValue);
	}

	/**
	 * Parses a string to a calendar of the system default time zone, returning null if it can not be parsed.
	 */
	public static Calendar parse(String date, String datePattern) {
		return parse(date, datePattern, null);
	}

	/**
	 * Parses a string with the first matching pattern, returning null if none matches.
	 */
	public static Calendar parse(String date, String[] datePatterns) {
		return parse(date, datePatterns, null);
	}

	/**
	 * Parses a string to a calendar, returning the default value if it is blank or can not be parsed.
	 */
	public static Calendar parse(String date, String datePattern, Calendar defaultValue) {
		if (StringUtils.isBlank(date)) {
			return defaultValue;
		}
		Date value = DateUtils.parse(date, datePattern);
		return toCalendar(value, TimeZone.getDefault(), defaultValue);
	}

	/**
	 * Parses a string with the first matching pattern, returning the default value if it is blank or none matches.
	 */
	public static Calendar parse(String date, String[] datePatterns, Calendar defaultValue) {
		if (StringUtils.isBlank(date)) {
			return defaultValue;
		}
		Date value = DateUtils.parse(date, datePatterns);
		return toCalendar(value, TimeZone.getDefault(), defaultValue);
	}

	/**
	 * Parses each string, unparsable elements become null.
	 */
	public static Calendar[] parseMany(String[] dates, String[] datePatterns) {
		return parseMany(dates, datePatterns, null);
	}

	/**
	 * Parses each string, unparsable elements become the default value.
	 *
	 * @throws IllegalArgumentException if the array is null
	 */
	public static Calendar[] parseMany(String[] dates, String[] datePatterns, Calendar defaultValue) {
		Assert.isNull(dates, "Date string array can not be null.");
		Calendar[] array = new Calendar[dates.length];
		int j = 0;
		for (String date : dates) {
			array[j++] = parse(date, datePatterns, defaultValue);
		}
		return array;
	}

	/**
	 * Converts milliseconds to calendars of the time zone (null means default), null elements stay null.
	 */
	public static Calendar[] toCalendarArray(Long[] dates, TimeZone timeZone) {
		return toCalendarArray(dates, timeZone, null);
	}

	/**
	 * Converts milliseconds to calendars of the time zone (null means default), null elements become the default value.
	 */
	public static Calendar[] toCalendarArray(Long[] dates, TimeZone timeZone, Calendar defaultValue) {
		Assert.isNull(dates, "Long array can not be null.");
		Calendar[] array = new Calendar[dates.length];
		int i = 0;
		for (Long date : dates) {
			array[i++] = toCalendar(date, timeZone, defaultValue);
		}
		return array;
	}

	/**
	 * Converts a value to a new calendar of the time zone (null means default), null returns null.
	 */
	public static Calendar toCalendar(Long ms, TimeZone timeZone) {
		return toCalendar(ms, timeZone, null);
	}

	/**
	 * Converts a value to a new calendar of the time zone (null means default), returning the default value for null.
	 */
	public static Calendar toCalendar(Long ms, TimeZone timeZone, Calendar defaultValue) {
		if (ms == null) {
			return defaultValue;
		}
		return toCalendar(new Date(ms), timeZone, defaultValue);
	}

	/**
	 * Converts a value to a new calendar of the time zone (null means default), null returns null.
	 */
	public static Calendar toCalendar(Date date, TimeZone timeZone) {
		return toCalendar(date, timeZone, null);
	}

	/**
	 * Converts a value to a new calendar of the time zone (null means default), returning the default value for null.
	 */
	public static Calendar toCalendar(Date date, TimeZone timeZone, Calendar defaultValue) {
		if (date == null) {
			return defaultValue;
		}
		Calendar c = getCalendar(timeZone);
		c.setTime(date);
		return c;
	}

	/**
	 * Converts a value to a new calendar of the time zone (null means default), null returns null.
	 */
	public static Calendar toCalendar(Instant instant, TimeZone timeZone) {
		return toCalendar(instant, timeZone, null);
	}

	/**
	 * Converts a value to a new calendar of the time zone (null means default), returning the default value for null.
	 */
	public static Calendar toCalendar(Instant instant, TimeZone timeZone, Calendar defaultValue) {
		if (instant == null) {
			return defaultValue;
		}
		Calendar c = getCalendar(timeZone);
		c.setTime(Date.from(instant));
		return c;
	}

	/**
	 * Converts a value to a new calendar of the time zone (null means default), null returns null.
	 */
	public static Calendar toCalendar(LocalDateTime localDateTime, TimeZone timeZone) {
		return toCalendar(localDateTime, timeZone, null);
	}

	/**
	 * Converts a value to a new calendar of the time zone (null means default), returning the default value for null.
	 */
	public static Calendar toCalendar(LocalDateTime localDateTime, TimeZone timeZone, Calendar defaultValue) {
		if (localDateTime == null) {
			return defaultValue;
		}
		Date date = DateUtils.toDate(localDateTime, timeZone != null ? timeZone.toZoneId() : null);
		return toCalendar(date, timeZone);
	}

	/**
	 * Converts a value to a new calendar of the time zone (null means default), null returns null.
	 */
	public static Calendar toCalendar(LocalDate localDate, TimeZone timeZone) {
		return toCalendar(localDate, timeZone, null);
	}

	/**
	 * Converts a value to a new calendar of the time zone (null means default), returning the default value for null.
	 */
	public static Calendar toCalendar(LocalDate localDate, TimeZone timeZone, Calendar defaultValue) {
		if (localDate == null) {
			return defaultValue;
		}
		Date date = DateUtils.toDate(localDate, timeZone != null ? timeZone.toZoneId() : null);
		return toCalendar(date, timeZone);
	}

	/**
	 * Converts dates to calendars of the time zone (null means default), null elements stay null.
	 */
	public static Calendar[] toCalendarArray(Date[] dates, TimeZone timeZone) {
		return toCalendarArray(dates, timeZone, null);
	}

	/**
	 * Converts dates to calendars of the time zone (null means default), null elements become the default value.
	 */
	public static Calendar[] toCalendarArray(Date[] dates, TimeZone timeZone, Calendar defaultValue) {
		Assert.isNull(dates, "Date array can not be null.");
		Calendar[] array = new Calendar[dates.length];
		int j = 0;
		for (Date date : dates) {
			array[j++] = toCalendar(date, timeZone, defaultValue);
		}
		return array;
	}

	/**
	 * Returns the milliseconds of a calendar, null returns null.
	 */
	public static Long getTimeInMillis(Calendar date) {
		return getTimeInMillis(date, null);
	}

	/**
	 * Returns the milliseconds of a calendar, returning the default value for null.
	 */
	public static Long getTimeInMillis(Calendar date, Long defaultValue) {
		return date != null ? Long.valueOf(date.getTimeInMillis()) : defaultValue;
	}

	/**
	 * Returns the milliseconds of each calendar, null elements stay null.
	 */
	public static Long[] getTimeInMillis(Calendar[] dates) {
		return getTimeInMillis(dates, null);
	}

	/**
	 * Returns the milliseconds of each calendar, null elements become the default value.
	 *
	 * @throws IllegalArgumentException if the array is null
	 */
	public static Long[] getTimeInMillis(Calendar[] dates, Long defaultValue) {
		Assert.isNull(dates, "Calendar array can not be null.");
		Long[] values = new Long[dates.length];
		int j = 0;
		for (Calendar date : dates) {
			values[j++] = getTimeInMillis(date, defaultValue);
		}
		return values;
	}

	/**
	 * Creates a calendar of the system default time zone and sets its hour, minute and second.
	 */
	public static Calendar setTime(long timeInMs, int hour, int minute, int second) {
		return setTime(timeInMs, hour, minute, second, null);
	}

	/**
	 * Creates a calendar of the time zone (null means default) and sets its hour, minute and second,
	 * milliseconds of the second are kept.
	 */
	public static Calendar setTime(long timeInMs, int hour, int minute, int second, TimeZone timeZone) {
		Calendar c = getCalendar(timeZone);
		c.setTimeInMillis(timeInMs);
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, second);
		return c;
	}

	/**
	 * Creates a calendar of the system default time zone at the start of the given day.
	 *
	 * @param month the month, 1-12
	 */
	public static Calendar of(int year, int month, int date) {
		return of(year, month, date, 0, 0, 0);
	}

	/**
	 * Creates a calendar of the system default time zone, milliseconds are zero.
	 *
	 * @param month the month, 1-12
	 */
	public static Calendar of(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
		return of(year, month, dayOfMonth, hourOfDay, minute, second, null);
	}

	/**
	 * Creates a calendar of the time zone (null means default), milliseconds are zero.
	 *
	 * <pre>
	 * CalendarUtils.of(2021, 1, 31, 10, 20, 30, UTC).getTimeInMillis() = 2021-01-31T10:20:30Z
	 * </pre>
	 *
	 * @param month the month, 1-12
	 */
	public static Calendar of(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second, TimeZone timeZone) {
		Calendar c = getCalendar(timeZone);
		c.clear();
		c.set(year, month - 1, dayOfMonth, hourOfDay, minute, second);
		return c;
	}

	private static Calendar getCalendar(TimeZone timeZone) {
		if (timeZone == null) {
			timeZone = TimeZone.getDefault();
		}
		return Calendar.getInstance(timeZone);
	}

}
