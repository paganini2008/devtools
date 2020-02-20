package com.github.paganini2008.devtools.date;

import java.text.DateFormat;
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
 * @version 1.0
 */
public abstract class CalendarUtils {

	public static final Calendar[] EMPTY_ARRAY = new Calendar[0];

	public static String[] formatMany(Calendar[] dates) {
		return formatMany(dates, DateUtils.DEFAULT_DATE_FORMATTER);
	}

	public static String[] formatMany(Calendar[] dates, DateFormat df) {
		return formatMany(dates, df, null);
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

	public static String[] formatMany(Calendar[] dates, String datePattern) {
		return formatMany(dates, datePattern, "");
	}

	public static String[] formatMany(Calendar[] dates, String datePattern, String defaultValue) {
		return formatMany(dates, DateUtils.getDateFormatter(datePattern), defaultValue);
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

	public static String format(Calendar cal) {
		return format(cal, DateUtils.DEFAULT_DATE_FORMATTER);
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

	public static Calendar[] toCalendarArray(long[] dates, TimeZone timeZone) {
		Calendar[] array = new Calendar[dates.length];
		int i = 0;
		for (long date : dates) {
			array[i++] = toCalendar(date, timeZone);
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
		Calendar c = Calendar.getInstance();
		c.setTimeZone(timeZone);
		c.setTimeInMillis(ms);
		return c;
	}

	public static Calendar toCalendar(Date date, TimeZone timeZone) {
		return toCalendar(date, timeZone, null);
	}

	public static Calendar toCalendar(Date date, TimeZone timeZone, Calendar defaultValue) {
		if (date == null) {
			return defaultValue;
		}
		Calendar c = Calendar.getInstance();
		c.setTimeZone(timeZone);
		c.setTime(date);
		return c;
	}

	public static Calendar toCalendar(LocalDateTime localDateTime, TimeZone timeZone) {
		return toCalendar(localDateTime, timeZone, null);
	}

	public static Calendar toCalendar(LocalDateTime localDateTime, TimeZone timeZone, Calendar defaultValue) {
		return localDateTime != null ? toCalendar(DateUtils.toDate(localDateTime, timeZone.toZoneId()), timeZone) : defaultValue;
	}

	public static Calendar toCalendar(LocalDate localDate, TimeZone timeZone) {
		return toCalendar(localDate, timeZone, null);
	}

	public static Calendar toCalendar(LocalDate localDate, TimeZone timeZone, Calendar defaultValue) {
		return localDate != null ? toCalendar(DateUtils.toDate(localDate, timeZone.toZoneId()), timeZone) : defaultValue;
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

	public static Calendar valueOf(int year, int month, int date) {
		return valueOf(year, month, date, 0, 0, 0);
	}

	public static Calendar valueOf(int year, int month, int date, int hour, int minute, int second) {
		Calendar c = Calendar.getInstance();
		c.set(year, month - 1, date, hour, minute, second);
		return c;
	}

}
