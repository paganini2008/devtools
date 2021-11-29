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

import static com.github.paganini2008.devtools.date.DateUtils.DEFAULT_DATE_PATTERN;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.StringUtils;

/**
 * CalendarUtils
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public abstract class CalendarUtils {

	public static final Calendar[] EMPTY_ARRAY = new Calendar[0];

	public static String[] formatMany(Calendar[] dates, String datePattern) {
		return formatMany(dates, datePattern, "");
	}

	public static String[] formatMany(Calendar[] dates, String datePattern, String defaultValue) {
		return formatMany(dates, new SimpleDateFormat(datePattern), defaultValue);
	}

	public static String[] formatMany(Calendar[] dates) {
		return formatMany(dates, DEFAULT_DATE_PATTERN);
	}

	public static String[] formatMany(Calendar[] dates, DateFormat df) {
		return formatMany(dates, df, "");
	}

	public static String[] formatMany(Calendar[] dates, DateFormat df, String defaultValue) {
		Assert.isNull(dates, "Calendar array can not be null.");
		String[] values = new String[dates.length];
		int i = 0;
		for (Calendar date : dates) {
			values[i++] = format(date, df, defaultValue);
		}
		return values;
	}

	public static String format(Calendar cal) {
		return format(cal, DEFAULT_DATE_PATTERN);
	}

	public static String format(Calendar c, String datePattern) {
		return format(c, datePattern, "");
	}

	public static String format(Calendar c, String datePattern, String defaultValue) {
		if (c == null) {
			return defaultValue;
		}
		return DateUtils.format(c.getTime(), datePattern, defaultValue);
	}

	public static String format(Calendar c, DateFormat df) {
		return format(c, df, null);
	}

	public static String format(Calendar c, DateFormat df, String defaultValue) {
		if (c == null) {
			return defaultValue;
		}
		return DateUtils.format(c.getTime(), df, defaultValue);
	}

	public static Calendar parse(String date, String datePattern) {
		return parse(date, datePattern, null);
	}

	public static Calendar parse(String date, String[] datePatterns) {
		return parse(date, datePatterns, null);
	}

	public static Calendar parse(String date, String datePattern, Calendar defaultValue) {
		if (StringUtils.isBlank(date)) {
			return defaultValue;
		}
		Date value = DateUtils.parse(date, datePattern);
		return toCalendar(value, TimeZone.getDefault(), defaultValue);
	}

	public static Calendar parse(String date, String[] datePatterns, Calendar defaultValue) {
		if (StringUtils.isBlank(date)) {
			return defaultValue;
		}
		Date value = DateUtils.parse(date, datePatterns);
		return toCalendar(value, TimeZone.getDefault(), defaultValue);
	}

	public static Calendar[] parseMany(String[] dates, String[] datePatterns) {
		return parseMany(dates, datePatterns, null);
	}

	public static Calendar[] parseMany(String[] dates, String[] datePatterns, Calendar defaultValue) {
		Calendar[] array = new Calendar[dates.length];
		int j = 0;
		for (String date : dates) {
			array[j++] = parse(date, datePatterns, defaultValue);
		}
		return array;
	}

	public static Calendar[] toCalendarArray(Long[] dates, TimeZone timeZone) {
		return toCalendarArray(dates, timeZone, null);
	}

	public static Calendar[] toCalendarArray(Long[] dates, TimeZone timeZone, Calendar defaultValue) {
		Calendar[] array = new Calendar[dates.length];
		int i = 0;
		for (long date : dates) {
			array[i++] = toCalendar(date, timeZone, defaultValue);
		}
		return array;
	}

	public static Calendar toCalendar(Long ms, TimeZone timeZone) {
		return toCalendar(ms, timeZone, null);
	}

	public static Calendar toCalendar(Long ms, TimeZone timeZone, Calendar defaultValue) {
		if (ms == null) {
			return defaultValue;
		}
		return toCalendar(new Date(ms), timeZone, defaultValue);
	}

	public static Calendar toCalendar(Date date, TimeZone timeZone) {
		return toCalendar(date, timeZone, null);
	}

	public static Calendar toCalendar(Date date, TimeZone timeZone, Calendar defaultValue) {
		if (date == null) {
			return defaultValue;
		}
		Calendar c = getCalendar(timeZone);
		c.setTime(date);
		return c;
	}

	public static Calendar toCalendar(LocalDateTime localDateTime, TimeZone timeZone) {
		return toCalendar(localDateTime, timeZone, null);
	}

	public static Calendar toCalendar(LocalDateTime localDateTime, TimeZone timeZone, Calendar defaultValue) {
		Date date = DateUtils.toDate(localDateTime, timeZone.toZoneId());
		return localDateTime != null ? toCalendar(date, timeZone) : defaultValue;
	}

	public static Calendar toCalendar(LocalDate localDate, TimeZone timeZone) {
		return toCalendar(localDate, timeZone, null);
	}

	public static Calendar toCalendar(LocalDate localDate, TimeZone timeZone, Calendar defaultValue) {
		Date date = DateUtils.toDate(localDate, timeZone.toZoneId());
		return localDate != null ? toCalendar(date, timeZone) : defaultValue;
	}

	public static Calendar[] toCalendarArray(Date[] dates, TimeZone timeZone) {
		return toCalendarArray(dates, timeZone, null);
	}

	public static Calendar[] toCalendarArray(Date[] dates, TimeZone timeZone, Calendar defaultValue) {
		Calendar[] array = new Calendar[dates.length];
		int j = 0;
		for (Date date : dates) {
			array[j++] = toCalendar(date, timeZone, defaultValue);
		}
		return array;
	}

	public static Long getTimeInMillis(Calendar date) {
		return getTimeInMillis(date, null);
	}

	public static Long getTimeInMillis(Calendar date, Long defaultValue) {
		return date != null ? date.getTimeInMillis() : defaultValue;
	}

	public static Long[] getTimeInMillis(Calendar[] dates) {
		return getTimeInMillis(dates, null);
	}

	public static Long[] getTimeInMillis(Calendar[] dates, Long defaultValue) {
		Assert.isNull(dates, "Calendar array can not be null.");
		Long[] values = new Long[dates.length];
		int j = 0;
		for (Calendar date : dates) {
			values[j++] = getTimeInMillis(date, defaultValue);
		}
		return values;
	}

	public static Calendar setTime(long timeInMs, int hour, int minute, int second) {
		Calendar c = toCalendar(timeInMs, null);
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, second);
		return c;
	}

	public static Calendar of(int year, int month, int date) {
		return of(year, month, date, 0, 0, 0);
	}

	public static Calendar of(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
		Calendar c = getCalendar(null);
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
