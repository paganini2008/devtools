package com.github.paganini2008.devtools.cron4j.cron;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 
 * CalendarUtils
 *
 * @author Fred Feng
 * @version 1.0
 */
public abstract class CalendarUtils {

	private static TimeZone timeZone = TimeZone.getDefault();

	public static void setTimeZone(TimeZone timeZone) {
		CalendarUtils.timeZone = timeZone;
	}

	public static Calendar setField(Date date, int calendarField, int value) {
		return setField(date, calendarField, value, true);
	}

	public static Calendar setField(Date date, int calendarField, int value, boolean reset) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.setTimeZone(timeZone);
		return setField(calendar, calendarField, value, reset);
	}

	public static Calendar setField(Calendar date, int calendarField, int value) {
		return setField(date, calendarField, value, true);
	}

	public static Calendar setField(Calendar date, int calendarField, int value, boolean reset) {
		Calendar calendar = (Calendar) date.clone();
		calendar.set(calendarField, value);
		if (reset) {
			if (calendarField == Calendar.WEEK_OF_MONTH || calendarField == Calendar.WEEK_OF_YEAR) {
				calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
			} else {
				switch (calendarField) {
				case Calendar.YEAR:
					calendar.set(Calendar.MONTH, Calendar.JANUARY);
				case Calendar.MONTH:
					calendar.set(Calendar.DAY_OF_MONTH, 1);
				case Calendar.DAY_OF_YEAR:
				case Calendar.DAY_OF_MONTH:
				case Calendar.DAY_OF_WEEK:
					calendar.set(Calendar.HOUR_OF_DAY, 0);
				case Calendar.HOUR:
					calendar.set(Calendar.MINUTE, 0);
				case Calendar.MINUTE:
					calendar.set(Calendar.SECOND, 0);
					break;
				}
			}
		}
		return calendar;
	}

	public static void main(String[] args) {
		Calendar calendar = setField(new Date(), Calendar.WEEK_OF_MONTH, 1);
		System.out.println(calendar.getActualMaximum(Calendar.WEEK_OF_YEAR));
	}

}
