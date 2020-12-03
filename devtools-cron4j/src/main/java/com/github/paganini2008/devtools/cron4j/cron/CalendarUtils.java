package com.github.paganini2008.devtools.cron4j.cron;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.cron4j.parser.MalformedCronException;

/**
 * 
 * CalendarUtils
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public abstract class CalendarUtils {

	private static TimeZone timeZone = TimeZone.getDefault();

	public static void setTimeZone(TimeZone timeZone) {
		CalendarUtils.timeZone = timeZone;
	}

	private static final Map<String, Integer> dayOfWeekValues = new HashMap<String, Integer>();
	private static final Map<Integer, String> dayOfWeekNames;
	private static final Map<String, Integer> monthValues = new HashMap<String, Integer>();
	private static final Map<Integer, String> monthNames;

	static {

		dayOfWeekValues.put("SUN", Calendar.SUNDAY);
		dayOfWeekValues.put("MON", Calendar.MONDAY);
		dayOfWeekValues.put("TUES", Calendar.TUESDAY);
		dayOfWeekValues.put("WED", Calendar.WEDNESDAY);
		dayOfWeekValues.put("THUR", Calendar.THURSDAY);
		dayOfWeekValues.put("FRI", Calendar.FRIDAY);
		dayOfWeekValues.put("SAT", Calendar.SATURDAY);

		dayOfWeekNames = MapUtils.reverse(dayOfWeekValues);

		monthValues.put("JAN", Calendar.JANUARY);
		monthValues.put("FEB", Calendar.FEBRUARY);
		monthValues.put("MAR", Calendar.MARCH);
		monthValues.put("APR", Calendar.APRIL);
		monthValues.put("MAY", Calendar.MAY);
		monthValues.put("JUNE", Calendar.JUNE);
		monthValues.put("JULY", Calendar.JULY);
		monthValues.put("AUG", Calendar.AUGUST);
		monthValues.put("SEPT", Calendar.SEPTEMBER);
		monthValues.put("OCT", Calendar.OCTOBER);
		monthValues.put("NOV", Calendar.NOVEMBER);
		monthValues.put("DEC", Calendar.DECEMBER);

		monthNames = MapUtils.reverse(monthValues);
	}

	public static int getDayOfWeekValue(String repr) {
		if (!dayOfWeekValues.containsKey(repr)) {
			throw new MalformedCronException("Unknown dayOfWeek string: " + repr);
		}
		return dayOfWeekValues.get(repr);
	}

	public static String getDayOfWeekName(int calendarField) {
		if (!dayOfWeekNames.containsKey(calendarField)) {
			throw new MalformedCronException("Unknown calendarField: " + calendarField);
		}
		return dayOfWeekNames.get(calendarField);
	}

	public static int getMonthValue(String repr) {
		if (!monthValues.containsKey(repr)) {
			throw new MalformedCronException("Unknown month string: " + repr);
		}
		return monthValues.get(repr);
	}

	public static String getMonthName(int calendarField) {
		if (!monthNames.containsKey(calendarField)) {
			throw new MalformedCronException("Unknown calendarField: " + calendarField);
		}
		return monthNames.get(calendarField);
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
